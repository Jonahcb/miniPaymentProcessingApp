package com.jonah.payment.data;
import com.jonah.payment.utils.CryptoUtils;
import java.sql.*;

/**
 * DenylistDAO manages card denylisting logic.
 * Cards that fail AVR or are flagged for fraud are added to this list.
 */
public class DenylistDAO {

    // Database connection used for all queries
    private final Connection conn;

    /**
     * Constructs the DAO with a live JDBC connection.
     *
     * @param conn JDBC connection to the Oracle DB.
     */
    public DenylistDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Checks if a PAN (card) is on the denylist.
     * The PAN is hashed before lookup to protect cardholder data.
     *
     * @param pan Raw PAN to check
     * @return true if the card is denylisted, false otherwise
     * @throws SQLException if a hashing or DB access error occurs
     */
    public boolean isDenied(String pan) throws SQLException {
        String hashedPan;
        try {
            // Convert to secure hashed representation
            hashedPan = CryptoUtils.hashPAN(pan);
        } catch (Exception e) {
            throw new SQLException("Failed to hash PAN", e);
        }

        // Query to check for existence of hashed PAN in denylist
        String sql = "SELECT 1 FROM denylist WHERE pan = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPan);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // True if record exists
            }
        }
    }

    /**
     * Adds a PAN to the denylist table. Duplicate entries are ignored.
     * The PAN is stored securely using a hashed value.
     *
     * @param pan Raw PAN to add to denylist
     * @throws SQLException if hashing or DB insert fails
     */
    public void addToDenylist(String pan) throws SQLException {
        String hashedPan;
        try {
            hashedPan = CryptoUtils.hashPAN(pan);
        } catch (Exception e) {
            throw new SQLException("Failed to hash PAN", e);
        }

        // Insert or ignore duplicate PANs based on primary key constraint
        String sql = "INSERT INTO denylist (pan) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPan);
            stmt.executeUpdate();
        }
    }
}

