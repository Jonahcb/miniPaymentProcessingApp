package com.jonah.payment.core;

import com.jonah.payment.data.PaymentRequest;
import com.jonah.payment.data.TapEvent;
import com.jonah.payment.data.DenylistDAO;
import com.jonah.payment.data.SeenCardDAO;
import com.jonah.payment.data.TapEventDAO;
import com.jonah.payment.network.AcquirerSimulator;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * AVRHandler processes Account Verification Requests (AVR) for entry tap events.
 * It performs denylist checks, logs seen cards, and sends AVR to the acquirer.
 */
public class AVRHandler {

    private final AcquirerSimulator aquirer;      // Visa acquirer interface (real or simulated)
    private final Connection conn;                // Active DB connection
    private final TapEventDAO tapEventDAO;        // DAO for tap event logging
    private final DenylistDAO denylistDAO;        // DAO for checking/storing denylisted PANs
    private final SeenCardDAO seenCardDAO;        // DAO for first-seen card tracking

    /**
     * Constructs an AVRHandler with required dependencies.
     *
     * @param acquirer Acquirer client (real or simulated).
     * @param conn     Active JDBC connection to Oracle.
     */
    public AVRHandler(AcquirerSimulator acquirer, Connection conn) {
        this.aquirer = acquirer;
        this.conn = conn;
        this.tapEventDAO = new TapEventDAO(this.conn);
        this.denylistDAO = new DenylistDAO(this.conn);
        this.seenCardDAO = new SeenCardDAO(this.conn);
    }

    /**
     * Main entry point for processing a contactless entry tap.
     * Stores the tap, checks for denylist, and calls the acquirer for verification.
     *
     * @param request The full payment request.
     * @param tap     Tap event object extracted from the request.
     * @return true if AVR is approved and not denylisted, false otherwise.
     */
    public boolean processAVR(PaymentRequest request, TapEvent tap) {
        String pan = request.getCardData().getPan();

        try {
            // Always log the tap first (even if it’s later denied)
            tapEventDAO.insert(tap, true);

            // Check if card has been seen before; if not, mark as first seen
            if (!seenCardDAO.hasSeenCard(pan)) {
                seenCardDAO.insert(pan);
                System.out.println("\uD83D\uDC40 First time seeing PAN: " + pan);
            }

            // Denylist enforcement: immediately reject if PAN is listed
            if (denylistDAO.isDenied(pan)) {
                System.out.println("\u274C PAN is denylisted: " + pan);
                return false;
            }

            // Call Visa/acquirer for real-time account verification
            boolean approved = aquirer.sendAVR(request);

            if (!approved) {
                // If declined by Visa, proactively denylist the PAN
                denylistDAO.addToDenylist(pan);
                System.out.println("\uD83D\uDEAB AVR declined — PAN added to denylist");
                return false;
            }

            System.out.println("\u2705 AVR approved: " + pan);
            return true;

        } catch (SQLException e) {
            System.err.println("\u274C AVR DB error: " + e.getMessage());
            return false;
        }
    }
}




