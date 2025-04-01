package com.jonah.payment.core;

import com.jonah.payment.data.*;
import com.jonah.payment.network.*;
import java.sql.Connection;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * PaymentProcessorServlet handles incoming XML requests representing transit card tap events.
 * It routes requests either through account verification (entry) or fare processing (exit),
 * and returns XML-based authorization responses.
 */
@WebServlet(name = "PaymentProcessorServlet", urlPatterns = "/api/payment")
public class PaymentProcessorServlet extends HttpServlet {

    // Configurable debug flag to print table contents after each request.
    private static final boolean DEBUG_MODE = true;

    // Flag to toggle between real Visa integration and the simulator.
    private static final boolean USE_REAL_VISA = true;

    // Determines whether to reset the database schema upon startup.
    private static final boolean RESET_TABLES = true;

    // List of database tables to print if debug mode is enabled.
    private final String[] tables = {"tap_events", "denylist", "seen_cards"};

    // Dependencies for request handling.
    private DenylistDAO denylistDAO;
    private Connection conn;
    private AcquirerSimulator acquirer;
    private AVRHandler avrHandler;
    private AccountBasedProcessor fareProcessor;

    /**
     * Initializes the servlet by creating a database connection, schema,
     * DAOs, and choosing the acquirer implementation (real or simulated).
     */
    @Override
    public void init() {
        try {
            // Connect to Oracle database.
            this.conn = OracleConnector.getConnection();

            // Reset tables if configured to do so.
            SchemaInitializer.setupSchema(conn, RESET_TABLES);

            // Choose real Visa integration or local simulator.
            this.acquirer = new AcquirerSimulator(USE_REAL_VISA);

            // Initialize DAOs and handlers for data access and processing logic.
            this.denylistDAO = new DenylistDAO(conn);
            this.avrHandler = new AVRHandler(acquirer, conn);
            this.fareProcessor = new AccountBasedProcessor();

            System.out.println("\u2705 Database ready.");
        } catch (Exception e) {
            System.err.println("\u274C Failed to initialize DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main entry point for all incoming XML payment requests.
     * Parses the XML, determines tap mode (entry/exit), and routes accordingly.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read the entire XML body from the incoming request.
        String xml = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("\n\u2709\uFE0F Incoming PaymentRequest:\n" + xml);

        PaymentRequest payment;
        try {
            // Attempt to parse the XML body into a Java PaymentRequest object.
            payment = XMLParser.fromXml(xml);
        } catch (Exception e) {
            // Respond with HTTP 400 Bad Request if parsing fails.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<Error>Invalid PaymentRequest XML</Error>");
            return;
        }

        // Construct TapEvent object from parsed PaymentRequest.
        TapEvent tap = new TapEvent();
        tap.setPan(payment.getCardData().getPan());
        tap.setExpiry(payment.getCardData().getExpiry());
        tap.setAid(payment.getCardData().getAid());
        tap.setCryptogram(payment.getCardData().getCryptogram());
        tap.setTerminalId(payment.getTerminalId());
        tap.setTimestamp(LocalDateTime.now());
        tap.setMode(payment.getMode());

        // Handle tap based on mode.
        if (tap.getMode().equals("entry")) {
            // ENTRY MODE ────────────────────────────────────────────
            // Call AVR handler to check denylist, log tap, and contact Visa.
            boolean approved = avrHandler.processAVR(payment, tap);

            // Print result to server log.
            System.out.println(approved
                    ? "\u2705 Approved entrance at " + tap.getTerminalId()
                    : "\u274C Denied entrance at " + tap.getTerminalId());

            // Respond with appropriate HTTP status and XML body.
            response.setContentType("application/xml");
            response.setStatus(approved ? HttpServletResponse.SC_ACCEPTED : HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("<PaymentResponse><Status>" + (approved ? "Accepted" : "AVR Declined") + "</Status></PaymentResponse>");

        } else {
            // EXIT MODE ───────────────────────────────────────────────
            TapEventDAO dao = new TapEventDAO(conn);
            TapEvent matchedEntry = null;
            boolean approved = false;

            try {
                // Step 1: Find most recent unmatched approved entry tap.
                matchedEntry = dao.findMostRecentUnmatchedEntry(tap.getPan());

                // Step 2: Calculate fare and set amount in PaymentRequest.
                fareProcessor.processTapFare(payment, matchedEntry, tap);

                // Step 3: Send authorization request to Visa/acquirer.
                approved = acquirer.sendAuthorization(payment);

                // Step 4: Log this exit tap (whether approved or not).
                dao.insert(tap, approved);

                // Step 5: If approved exit, mark the entry as matched.
                if (approved) {
                    dao.markEntryAsMatched(tap.getPan(), tap.getTimestamp());
                }

            } catch (Exception e) {
                // Internal server error due to DB or processing failure.
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/xml");
                response.getWriter().write("<PaymentResponse><Status>Server Error</Status></PaymentResponse>");
                return;
            }

            // Denylist PAN if authorization fails.
            if (!approved) {
                try {
                    denylistDAO.addToDenylist(tap.getPan());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Log final approval or rejection to server console.
            System.out.println(approved
                    ? String.format("\u2705 Approved exit at %s: fare $%.2f", tap.getTerminalId(), payment.getAmount())
                    : "\u274C Denied exit at " + tap.getTerminalId());

            // Respond with HTTP status and authorization result in XML.
            response.setContentType("application/xml");
            response.setStatus(approved ? HttpServletResponse.SC_ACCEPTED : HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("<PaymentResponse><Status>" + (approved ? "Accepted" : "AVR Declined") + "</Status></PaymentResponse>");
        }

        // Optional: Print contents of all tables for debugging.
        if (DEBUG_MODE) {
            try (Statement stmt = conn.createStatement()) {
                for (String table : tables) {
                    System.out.println("\n\uD83D\uDCCB Contents of table: " + table);

                    try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {
                        ResultSetMetaData meta = rs.getMetaData();
                        int colCount = meta.getColumnCount();

                        int colWidth = 32;
                        for (int i = 1; i <= colCount; i++) {
                            System.out.printf("%-" + colWidth + "s", meta.getColumnName(i));
                        }
                        System.out.println();

                        while (rs.next()) {
                            for (int i = 1; i <= colCount; i++) {
                                String value = rs.getString(i);
                                if (value == null) value = "NULL";
                                System.out.printf("%-" + colWidth + "s", value);
                            }
                            System.out.println();
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("\u274C Error while querying tables:");
                e.printStackTrace();
            }
        }
    }
}