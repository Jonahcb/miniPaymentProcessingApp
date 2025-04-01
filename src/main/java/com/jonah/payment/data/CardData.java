package com.jonah.payment.data;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * CardData encapsulates raw EMV card attributes used for payment requests.
 * These values are typically extracted from an NFC transaction.
 *
 * Annotated with JAXB to support XML serialization/deserialization.
 */
@XmlRootElement
public class CardData {

    private String pan;          // Primary Account Number (e.g., 16-digit card number)
    private String expiry;       // Expiration date in YYMM format
    private String aid;          // Application Identifier for EMV chip app (tag 4F)
    private String cryptogram;   // EMV dynamic cryptogram (tag 9F26)

    /**
     * @return PAN - the card number
     */
    @XmlElement(name = "PAN")
    public String getPan() {
        return pan;
    }

    /**
     * @param pan The card number to set
     */
    public void setPan(String pan) {
        this.pan = pan;
    }

    /**
     * @return Card expiration date
     */
    @XmlElement(name = "Expiry")
    public String getExpiry() {
        return expiry;
    }

    /**
     * @param expiry Expiry date to set
     */
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    /**
     * @return Application Identifier (EMV AID)
     */
    @XmlElement(name = "AID")
    public String getAid() {
        return aid;
    }

    /**
     * @param aid EMV Application Identifier
     */
    public void setAid(String aid) {
        this.aid = aid;
    }

    /**
     * @return EMV cryptogram
     */
    @XmlElement(name = "Cryptogram")
    public String getCryptogram() {
        return cryptogram;
    }

    /**
     * @param cryptogram Set the dynamic EMV cryptogram
     */
    public void setCryptogram(String cryptogram) {
        this.cryptogram = cryptogram;
    }
}


