package com.jonah.payment.data;

import com.jonah.payment.utils.CryptoUtils;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * TapEventDAO provides data access logic for managing contactless tap events.
 * It enables tap insertion, retrieval of unmatched entry events, and updating
 * those entries once matched with corresponding exits.
 */
public class TapEventDAO {

    // Persistent database connection used for all operations in this DAO
    private final Connection conn;

    /**
     * Constructs a new DAO for accessing and modifying the tap_events table.
     *
     * @param conn JDBC connection to the Oracle database
     */
    public TapEventDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Records a single tap (either entry or exit) into the database.
     * It securely hashes the PAN before saving to protect cardholder data.
     *
     * @param tap      The tap event to persist
     * @param approved Indicates whether the transaction was authorized
     * @throws SQLException if hashing or database write fails
     */
    public void insert(TapEvent tap, boolean approved) throws SQLException {
        String sql = "INSERT INTO tap_events (pan, terminal_id, cryptogram, tap_time, approved, enter_or_exit) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try {
                // Hash PAN for privacy before storing
                stmt.setString(1, CryptoUtils.hashPAN(tap.getPan()));
            } catch (Exception e) {
                throw new SQLException("Failed to hash PAN", e);
            }

            // Set terminal ID, cryptogram, and timestamp
            stmt.setString(2, tap.getTerminalId());
            stmt.setString(3, tap.getCryptogram());
            stmt.setTimestamp(4, Timestamp.valueOf(tap.getTimestamp()));

            // Use 'Y' or 'N' string to indicate approval
            stmt.setString(5, approved ? "Y" : "N");

            // Save the mode as either 'entry' or 'exit'
            stmt.setString(6, tap.getMode().toLowerCase());

            // Execute insert query
            stmt.executeUpdate();
        }
    }

    /**
     * Finds the latest approved entry tap with no associated exit for the same PAN.
     * This is used to calculate a fare when a rider taps out.
     *
     * @param pan The raw PAN to look up; will be hashed to match stored format
     * @return The most recent unmatched entry TapEvent, or null if none found
     * @throws SQLException if hashing or DB query fails
     */
    public TapEvent findMostRecentUnmatchedEntry(String pan) throws SQLException {
        String hashedPan;
        try {
            // Hash PAN for lookup
            hashedPan = CryptoUtils.hashPAN(pan);
        } catch (Exception e) {
            throw new SQLException("Failed to hash PAN", e);
        }

        // SQL to find the most recent entry tap that hasn’t been matched yet
        String sql = """
            SELECT * FROM tap_events
            WHERE pan = ? AND enter_or_exit = 'entry' AND approved = 'Y' AND matched_exit_time IS NULL
            ORDER BY tap_time DESC
            FETCH FIRST 1 ROWS ONLY
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TapEvent tap = new TapEvent();

                    // Populate tap event from query results
                    tap.setTerminalId(rs.getString("terminal_id"));
                    tap.setCryptogram(rs.getString("cryptogram"));
                    tap.setTimestamp(rs.getTimestamp("tap_time").toLocalDateTime());
                    tap.setMode("entry");

                    // Set original PAN for downstream logic (e.g., fare calculation)
                    tap.setPan(pan);
                    return tap;
                }
            }
        }

        // No unmatched entry found
        return null;
    }

    /**
     * Updates the most recent unmatched entry tap by adding a matched_exit_time.
     * This indicates the entry has been successfully paired with an exit.
     *
     * @param pan       The raw PAN used to find the entry
     * @param matchTime The timestamp from the corresponding exit event
     * @throws SQLException if hashing or update fails
     */
    public void markEntryAsMatched(String pan, LocalDateTime matchTime) throws SQLException {
        String hashedPan;
        try {
            // Convert PAN to hashed format
            hashedPan = CryptoUtils.hashPAN(pan);
        } catch (Exception e) {
            throw new SQLException("Failed to hash PAN", e);
        }

        // SQL update to set matched_exit_time on most recent unmatched approved entry
        String sql = """
            UPDATE tap_events
            SET matched_exit_time = ?
            WHERE pan = ? AND enter_or_exit = 'entry' AND approved = 'Y' AND matched_exit_time IS NULL
              AND tap_time = (
                SELECT MAX(tap_time) FROM tap_events
                WHERE pan = ? AND enter_or_exit = 'entry' AND approved = 'Y' AND matched_exit_time IS NULL
              )
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(matchTime)); // Set match time to current exit
            stmt.setString(2, hashedPan);                       // Match entry by hashed PAN
            stmt.setString(3, hashedPan);                       // Use subquery to find the latest one
            stmt.executeUpdate();
        }
    }
}



