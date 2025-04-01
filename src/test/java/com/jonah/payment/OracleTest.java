package com.jonah.payment;

import java.sql.*;

public class OracleTest {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "your_username";
        String pass = "your_password";

        Connection conn = DriverManager.getConnection(url, user, pass);
        System.out.println("✅ Connected to Oracle!");

        conn.close();
    }
}

