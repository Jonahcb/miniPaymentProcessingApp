package com.jonah.payment.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * CryptoUtils provides cryptographic utility functions for securing data.
 * Currently includes functionality for hashing Primary Account Numbers (PANs)
 * using HMAC-SHA256.
 */
public class CryptoUtils {

    // This is a symmetric secret key used for HMAC hashing.
    // In production systems, this key should be securely stored in environment variables or a config vault.
    private static final String SECRET_KEY = "super-secret-key";

    /**
     * Hashes a PAN (Primary Account Number) using HMAC-SHA256 algorithm.
     * This is used to anonymize sensitive cardholder data before database insertion.
     *
     * @param pan The plain-text card number to hash.
     * @return A Base64-encoded HMAC-SHA256 hash of the PAN.
     * @throws Exception If the cryptographic algorithm is not available or fails.
     */
    public static String hashPAN(String pan) throws Exception {
        // Get an instance of HMAC using SHA256 as the hashing algorithm
        Mac hmac = Mac.getInstance("HmacSHA256");

        // Create a key specification using the secret key and algorithm
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");

        // Initialize the HMAC instance with the key
        hmac.init(keySpec);

        // Compute the HMAC hash of the PAN bytes
        byte[] result = hmac.doFinal(pan.getBytes());

        // Encode the binary hash result into a Base64 string for storage/transmission
        return Base64.getEncoder().encodeToString(result);
    }
}

