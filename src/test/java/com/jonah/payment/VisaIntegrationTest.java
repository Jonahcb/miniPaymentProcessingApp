package com.jonah.payment;

import com.jonah.payment.core.TerminalSimulator;
import com.jonah.payment.data.TestCardRequest;

/**
 * Simple test to trigger a Visa transaction using either the real Visa API or the simulator.
 * Toggling is controlled by the USE_REAL_VISA environment variable.
 */
public class VisaIntegrationTest {
    public static void main(String[] args) {
        boolean useRealVisa = Boolean.parseBoolean(System.getenv("USE_REAL_VISA"));
        System.out.println("🔌 Using " + (useRealVisa ? "REAL" : "SIMULATED") + " Visa client");

        TestCardRequest testCard = new TestCardRequest(
                "4761739001010119",  // Visa sandbox test PAN
                "TERM-001",
                "entry",
                "123456ABCDEF",
                "2501",
                1000
        );

        TerminalSimulator terminal = new TerminalSimulator(testCard);
        terminal.run(); // Run in the main thread for simplicity
    }
}

