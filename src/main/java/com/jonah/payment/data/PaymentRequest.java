package com.jonah.payment.data;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * PaymentRequest encapsulates all the information needed for processing a payment tap.
 * It serves as the root XML model deserialized from incoming tap requests.
 *
 * The object includes:
 * - Card data (nested CardData class)
 * - Terminal and contextual metadata
 * - Transaction amount and type (entry or exit)
 *
 * Used as input to both AVR (Account Verification Request) and Authorization flows.
 */
@XmlRootElement
public class PaymentRequest {

    private String terminalId;     // Unique identifier of the transit terminal
    private CardData cardData;     // Contains card-level EMV attributes like PAN, AID, Expiry, etc.
    private double amount;         // Monetary fare amount (can be zero for AVR)
    private String currency;       // Currency code for the transaction (e.g., USD, JPY)
    private String entryMode;      // Mode of card entry (07 = contactless)
    private String posConditionCode;  // Point-of-Service condition code (EMV field)
    private String tvr;            // Terminal Verification Results (bitfield from EMV kernel)
    private String terminalType;   // Integer code representing the terminal class (e.g., unattended kiosk)
    private String terminalCapability; // EMV capability level of the terminal
    private String cardholderVerification; // CVM method used (e.g., No CVM, Online PIN)
    private String messageReasonCode;     // Reason code for initiating the transaction (e.g., 5206 = Transit Entry)
    private String mode;           // Either "entry" or "exit", used to control business logic

    /**
     * Gets the terminal ID.
     * @return Terminal identifier string
     */
    @XmlElement(name = "TerminalId")
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * Sets the terminal ID.
     * @param terminalId Identifier for the transit terminal
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    /**
     * Gets the card data associated with the payment.
     * @return CardData object
     */
    @XmlElement(name = "CardData")
    public CardData getCardData() {
        return cardData;
    }

    /**
     * Sets the card data object.
     * @param cardData Contains EMV attributes (PAN, AID, etc.)
     */
    public void setCardData(CardData cardData) {
        this.cardData = cardData;
    }

    /**
     * Gets the fare amount.
     * @return Payment amount
     */
    @XmlElement(name = "Amount")
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the fare amount.
     * @param amount Value to charge
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the transaction currency.
     * @return ISO currency code
     */
    @XmlElement(name = "Currency")
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency used for payment.
     * @param currency ISO currency string (e.g., USD, JPY)
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the card entry mode (e.g., contactless).
     * @return Entry mode code
     */
    @XmlElement(name = "EntryMode")
    public String getEntryMode() {
        return entryMode;
    }

    /**
     * Sets the card entry mode.
     * @param entryMode Mode used to present the card
     */
    public void setEntryMode(String entryMode) {
        this.entryMode = entryMode;
    }

    /**
     * Gets the POS condition code.
     * @return Point-of-service condition string
     */
    @XmlElement(name = "POSConditionCode")
    public String getPosConditionCode() {
        return posConditionCode;
    }

    /**
     * Sets the POS condition code.
     * @param posConditionCode Point-of-service condition (EMV field)
     */
    public void setPosConditionCode(String posConditionCode) {
        this.posConditionCode = posConditionCode;
    }

    /**
     * Gets the Terminal Verification Results (TVR).
     * @return EMV bitfield string
     */
    @XmlElement(name = "TVR")
    public String getTvr() {
        return tvr;
    }

    /**
     * Sets the Terminal Verification Results.
     * @param tvr EMV bitfield from card validation
     */
    public void setTvr(String tvr) {
        this.tvr = tvr;
    }

    /**
     * Gets the terminal type.
     * @return Terminal type code
     */
    @XmlElement(name = "TerminalType")
    public String getTerminalType() {
        return terminalType;
    }

    /**
     * Sets the terminal type.
     * @param terminalType Terminal class code (e.g., unattended kiosk)
     */
    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    /**
     * Gets the terminal capability code.
     * @return Capability level string
     */
    @XmlElement(name = "TerminalCapability")
    public String getTerminalCapability() {
        return terminalCapability;
    }

    /**
     * Sets the terminal capability code.
     * @param terminalCapability Capability bitmask (EMV level)
     */
    public void setTerminalCapability(String terminalCapability) {
        this.terminalCapability = terminalCapability;
    }

    /**
     * Gets the cardholder verification method.
     * @return CVM code string
     */
    @XmlElement(name = "CardholderVerification")
    public String getCardholderVerification() {
        return cardholderVerification;
    }

    /**
     * Sets the cardholder verification method.
     * @param cardholderVerification Code for CVM (e.g., No CVM, Online PIN)
     */
    public void setCardholderVerification(String cardholderVerification) {
        this.cardholderVerification = cardholderVerification;
    }

    /**
     * Gets the message reason code.
     * @return Message reason string (e.g., 5206)
     */
    @XmlElement(name = "MessageReasonCode")
    public String getMessageReasonCode() {
        return messageReasonCode;
    }

    /**
     * Sets the message reason code.
     * @param messageReasonCode Reason for transaction (e.g., entry)
     */
    public void setMessageReasonCode(String messageReasonCode) {
        this.messageReasonCode = messageReasonCode;
    }

    /**
     * Gets the mode of transaction (entry or exit).
     * @return Either "entry" or "exit"
     */
    @XmlElement(name = "Mode")
    public String getMode() {
        return mode;
    }

    /**
     * Sets the mode of transaction.
     * @param mode Either "entry" or "exit"
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
}

