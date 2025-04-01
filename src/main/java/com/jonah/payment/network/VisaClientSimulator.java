package com.jonah.payment.network;

import com.jonah.payment.data.PaymentRequest;

/**
 * VisaClientSimulator is a mock implementation of the VisaClientInterface.
 * It simulates the behavior of a real VisaNet integration for local testing and development.
 * It does not perform any network operations and uses deterministic rules for success/failure.
 */
public class VisaClientSimulator implements VisaClientInterface {

    /**
     * Simulates an Account Verification Request (AVR) authorization.
     *
     * @param request The incoming payment request object.
     * @return true if the AVR is approved, false if declined.
     */
    @Override
    public boolean sendAVR(PaymentRequest request) {
        // Retrieve the card's Primary Account Number (PAN)
        String pan = request.getCardData().getPan();

        // Simulated rejection rule: if PAN ends in '9', AVR fails
        if (pan.endsWith("9")) {
            System.out.printf("\u274C [SIM] AVR declined for PAN: %s%n", pan);
            return false;
        }

        // If rule not triggered, AVR is approved
        System.out.printf("\u2705 [SIM] AVR approved for PAN: %s%n", pan);
        return true;
    }

    /**
     * Simulates a transaction authorization after fare calculation.
     *
     * @param request The payment request containing amount and PAN.
     * @return true if the authorization is approved, false if declined.
     */
    @Override
    public boolean sendAuthorization(PaymentRequest request) {
        // Extract the PAN and amount from the request
        String pan = request.getCardData().getPan();
        double amount = request.getAmount();

        // Simulated rejection rule: decline if amount > 20 or PAN ends in '8'
        if (amount > 20.00 || pan.endsWith("8")) {
            System.out.printf("\u274C [SIM] Authorization declined: PAN=%s | Amount=%.2f%n", pan, amount);
            return false;
        }

        // If rule not triggered, authorization is approved
        System.out.printf("\u2705 [SIM] Authorization approved: PAN=%s | Amount=%.2f%n", pan, amount);
        return true;
    }
}

