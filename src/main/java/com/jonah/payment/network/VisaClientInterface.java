package com.jonah.payment.network;

import com.jonah.payment.data.PaymentRequest;

/**
 * VisaClientInterface defines a contract for sending payment requests to VisaNet.
 * Implementations may simulate the behavior or send real network requests.
 */
public interface VisaClientInterface {

    /**
     * Sends an Account Verification Request (AVR) for a payment request.
     * AVR checks whether the card is valid and capable of processing transactions.
     *
     * @param request The payment request to verify.
     * @return true if AVR is approved, false otherwise.
     */
    boolean sendAVR(PaymentRequest request);

    /**
     * Sends an authorization request for an actual payment.
     *
     * @param request The payment request to authorize.
     * @return true if the payment is authorized by the network, false otherwise.
     */
    boolean sendAuthorization(PaymentRequest request);
}

