package com.jonah.payment.data;

import com.jonah.payment.utils.CryptoUtils;

import java.sql.*;

/**
 * SeenCardDAO handles tracking of PANs (cards) that have been seen for the first time.
 * This enables logging first-time users and suppressing repeated AVR attempts.
 */
public class SeenCardDAO {

    private final Connection conn; // Active JDBC connection

    /**
     * Constructs a DAO to interact with the seen_cards table.
     *
     * @param conn JDBC connection to the database
     */
    public SeenCardDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Checks whether the card's PAN has been logged before.
     * The PAN is hashed before lookup for privacy.
     *
     * @param pan Raw PAN to query
     * @return true if the PAN exists in seen_cards, false otherwise
     * @throws SQLException if hashing or DB read fails
     */
    public boolean hasSeenCard(String pan) throws SQLException {
        String hashedPan;
        try {
            // Hash PAN for secure lookup
            hashedPan = CryptoUtils.hashPAN(pan);
        } catch (Exception e) {
            throw new SQLException("Failed to hash PAN", e);
        }

        String sql = "SELECT 1 FROM seen_cards WHERE pan = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPan);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if exists
            }
        }
    }

    /**
     * Inserts a new PAN into the seen_cards table.
     * The card is considered "seen" and will not be re-logged.
     *
     * @param pan Raw card PAN to store
     * @throws SQLException if hashing or insertion fails
     */
    public void insert(String pan) throws SQLException {
        String hashedPan;
        try {
            hashedPan = CryptoUtils.hashPAN(pan);
        } catch (Exception e) {
            throw new SQLException("Failed to hash PAN", e);
        }

        String sql = "INSERT INTO seen_cards (pan) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPan);
            stmt.executeUpdate();
        }
    }
}
