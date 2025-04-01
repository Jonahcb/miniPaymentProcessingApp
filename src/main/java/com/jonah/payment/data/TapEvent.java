package com.jonah.payment.data;

import java.time.LocalDateTime;

/**
 * TapEvent represents a single contactless tap made by a rider.
 * It stores entry or exit information, time, approval status,
 * and card metadata used to identify and authorize the tap.
 */
public class TapEvent {

    private String pan;                // Primary Account Number (cleartext — will be hashed before storing)
    private String terminalId;         // Terminal where the tap occurred
    private String aid;                // Application Identifier (EMV tag 4F)
    private String expiry;             // Card expiration date (EMV tag 5F24)
    private String cryptogram;         // EMV cryptographic value (tag 9F26)
    private LocalDateTime timestamp;   // Local timestamp of the tap event
    private String mode;               // Either "entry" or "exit"
    private boolean approved;          // Indicates whether the tap was successfully authorized

    // Getters and setters for each field ────────────────────────────────

    /**
     * @return PAN (will be hashed later before persistence).
     */
    public String getPan() {
        return pan;
    }

    /**
     * @param pan Set the PAN (Primary Account Number).
     */
    public void setPan(String pan) {
        this.pan = pan;
    }

    /**
     * @return Terminal ID where the tap was made.
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * @param terminalId Set terminal identifier for the tap.
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    /**
     * @return EMV application identifier.
     */
    public String getAid() {
        return aid;
    }

    /**
     * @param aid Set EMV application identifier.
     */
    public void setAid(String aid) {
        this.aid = aid;
    }

    /**
     * @return Card expiration date.
     */
    public String getExpiry() {
        return expiry;
    }

    /**
     * @param expiry Set card expiration date.
     */
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    /**
     * @return EMV cryptogram value for the transaction.
     */
    public String getCryptogram() {
        return cryptogram;
    }

    /**
     * @param cryptogram Set EMV cryptogram for the tap.
     */
    public void setCryptogram(String cryptogram) {
        this.cryptogram = cryptogram;
    }

    /**
     * @return Timestamp of when the tap occurred.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp Set timestamp of the tap.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Tap mode: "entry" or "exit".
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode Set tap mode (either "entry" or "exit").
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return true if the tap was authorized, false otherwise.
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * @param approved Set approval status of the tap.
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}




