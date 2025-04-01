package com.jonah.payment.data;

/**
 * TestCardRequest is a simple POJO used to configure test simulations
 * for card taps via the TerminalSimulator.
 *
 * Each instance represents one tap (entry or exit) with parameters like
 * delay, PAN, and terminal ID. It is not used for real-time XML parsing.
 */
public class TestCardRequest {

    public String terminalId;     // Simulated terminal ID (e.g. "gate-a")
    public String pan;            // Card number to simulate
    public String expiry;         // Expiration date for test card
    public String cryptogram;     // Fake EMV cryptogram for simulation
    public String mode;           // "entry" or "exit"
    public long delayAfterMs;     // How long to wait after the tap (in ms)

    /**
     * Constructs a new TestCardRequest with all required fields.
     *
     * @param terminalId Terminal ID string
     * @param pan Card PAN (Primary Account Number)
     * @param expiry Expiration date in YYMM format
     * @param cryptogram Simulated EMV cryptogram
     * @param mode Either "entry" or "exit"
     * @param delayAfterMs Delay in milliseconds after tap simulation
     */
    public TestCardRequest(String terminalId, String pan, String expiry, String cryptogram, String mode, long delayAfterMs) {
        this.terminalId = terminalId;
        this.pan = pan;
        this.expiry = expiry;
        this.cryptogram = cryptogram;
        this.mode = mode;
        this.delayAfterMs = delayAfterMs;
    }

    /**
     * Default constructor to support frameworks and manual object creation.
     */
    public TestCardRequest() {
    }
}

