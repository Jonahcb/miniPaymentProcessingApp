package com.jonah.payment.core;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * OracleConnector establishes a JDBC connection to an Oracle database instance.
 * Used throughout the backend to perform data operations on tables like tap_events,
 * denylist, and seen_cards.
 */
public class OracleConnector {

    /**
     * Opens a JDBC connection to a local Oracle database using preset credentials.
     *
     * @return A live Connection object to the Oracle database.
     * @throws Exception if the JDBC driver is not found or the connection fails.
     */
    public static Connection getConnection() throws Exception {
        // Explicitly load the Oracle JDBC driver class into the JVM
        Class.forName("oracle.jdbc.OracleDriver");

        // JDBC URL syntax: jdbc:oracle:thin:@//host:port/serviceName
        String jdbcUrl = "jdbc:oracle:thin:@//localhost:1521/ORCLPDB1";

        // Authentication credentials for database login (replace in production!)
        String username = "system";
        String password = "mypassword1";

        // Open and return a connection to the database
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}

