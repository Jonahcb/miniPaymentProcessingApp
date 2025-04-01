package com.jonah.payment.data;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * SchemaInitializer is responsible for setting up and optionally resetting
 * the Oracle database schema used by the payment processing application.
 * It creates and clears the 'tap_events', 'denylist', and 'seen_cards' tables.
 */
public class SchemaInitializer {

    /**
     * Initializes the database schema. Optionally clears existing data.
     *
     * @param conn Active JDBC connection to the database.
     * @param reset If true, existing data is deleted before table creation.
     * @throws SQLException If SQL execution fails.
     */
    public static void setupSchema(Connection conn, boolean reset) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            if (reset) {
                // Attempt to drop existing tables (ignore errors if they don’t exist).
                try { stmt.execute("DROP TABLE tap_events"); } catch (SQLException ignored) {}
                try { stmt.execute("DROP TABLE denylist"); } catch (SQLException ignored) {}
                try { stmt.execute("DROP TABLE seen_cards"); } catch (SQLException ignored) {}
            }

            // Create table for storing tap events (entry and exit).
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tap_events (
                    pan VARCHAR2(256),
                    terminal_id VARCHAR2(64),
                    cryptogram VARCHAR2(128),
                    tap_time TIMESTAMP,
                    approved CHAR(1),
                    enter_or_exit VARCHAR2(8),
                    matched_exit_time TIMESTAMP
                )""");

            // Create table for storing denylisted PANs.
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS denylist (
                    pan VARCHAR2(256) PRIMARY KEY
                )""");

            // Create table for tracking PANs seen for the first time.
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS seen_cards (
                    pan VARCHAR2(256) PRIMARY KEY
                )""");
        }
    }
}



