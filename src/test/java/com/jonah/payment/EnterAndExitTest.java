package com.jonah.payment;

import com.jonah.payment.core.TerminalSimulator;
import com.jonah.payment.data.TestCardRequest;

public class EnterAndExitTest {

    public static void main(String[] args) throws Exception {
        simulateJourney("4761739001010010", "GATE_01", "GATE_99", 10_000);  // Baseline test
        simulateJourney("4761739001010011", "GATE_02", "GATE_99", 2_000);   // Short ride
        simulateJourney("4761739001010012", "GATE_03", "GATE_99", 30_000);  // Long ride

        runMultipleSequentialRiders();
        simulateDeniedCard();
        simulateExitOnly();
        simulateEntryOnly();
        simulateReusedCard();

        System.out.println("✅ All journey tests complete.");
    }

    public static void simulateJourney(String pan, String entryGate, String exitGate, int rideDurationMs) throws Exception {
        System.out.printf("🧍 Entering at %s...\n", entryGate);
        new TerminalSimulator(new TestCardRequest(
                pan, entryGate, "entry", "CRYPT1", "12/25", 0)).run();

        System.out.println("🕒 Simulating ride...");
        Thread.sleep(rideDurationMs);

        System.out.printf("🚪 Exiting at %s...\n", exitGate);
        new TerminalSimulator(new TestCardRequest(
                pan, exitGate, "exit", "CRYPT2", "12/25", 0)).run();

        System.out.println("🎫 Journey complete.\n");
    }

    public static void runMultipleSequentialRiders() throws Exception {
        System.out.println("👥 Simulating 3 back-to-back riders...\n");
        for (int i = 0; i < 3; i++) {
            String pan = "41111111111111" + i;
            simulateJourney(pan, "GATE_" + (10 + i), "GATE_99", 4000 + i * 1000);
        }
    }

    public static void simulateDeniedCard() throws Exception {
        System.out.println("🚫 Simulating denied card...");
        String deniedPAN = "9999888877776666";  // Should be pre-denied in DB
        new TerminalSimulator(new TestCardRequest(
                deniedPAN, "GATE_DENY", "entry", "CRYPT9", "12/25", 0)).run();
        System.out.println("❌ Card should be denied.\n");
    }

    public static void simulateExitOnly() throws Exception {
        System.out.println("🕳️ Simulating exit-only tap (no entry)...");
        new TerminalSimulator(new TestCardRequest(
                "5555444433332222", "GATE_99", "exit", "CRYPTX", "12/25", 0)).run();
        System.out.println("🚪 Exit-only tap done.\n");
    }

    public static void simulateEntryOnly() throws Exception {
        System.out.println("🕳️ Simulating entry-only tap (no exit)...");
        new TerminalSimulator(new TestCardRequest(
                "1234567890123456", "GATE_01", "entry", "CRYPTY", "12/25", 0)).run();
        System.out.println("🚉 Entry-only tap done.\n");
    }

    public static void simulateReusedCard() throws Exception {
        System.out.println("🔁 Simulating reused card across different rides...");
        String reusedPan = "1111222233334444";
        for (int i = 0; i < 2; i++) {
            new TerminalSimulator(new TestCardRequest(
                    reusedPan, "GATE_01", "entry", "CRYPT" + i, "12/25", 0)).run();

            Thread.sleep(2000);

            new TerminalSimulator(new TestCardRequest(
                    reusedPan, "GATE_99", "exit", "CRYPT" + (i + 10), "12/25", 0)).run();

            Thread.sleep(1000);
        }
        System.out.println("📆 Reused card test done.\n");
    }
}




