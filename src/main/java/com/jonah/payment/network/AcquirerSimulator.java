package com.jonah.payment.network;

import com.jonah.payment.data.PaymentRequest;

/**
 * AcquirerSimulator acts as a wrapper that delegates AVR and Authorization
 * requests to either a real VisaClient or a simulated VisaClientSimulator,
 * depending on the configuration.
 */
public class AcquirerSimulator {

    private final VisaClientInterface visaClient; // The chosen Visa client instance (real or simulated)

    /**
     * Constructs the AcquirerSimulator with a flag determining whether to use a real Visa client
     * or a simulator.
     *
     * @param useRealVisaClient If true, uses the real VisaClient; if false, uses VisaClientSimulator.
     * @throws Exception if VisaClient instantiation fails due to SSL issues or configuration.
     */
    public AcquirerSimulator(boolean useRealVisaClient) throws Exception {
        this.visaClient = useRealVisaClient ? new VisaClient() : new VisaClientSimulator();
    }

    /**
     * Sends an Account Verification Request (AVR) to the configured Visa client.
     *
     * @param request The payment request containing card and terminal data.
     * @return true if the AVR is approved, false otherwise.
     */
    public boolean sendAVR(PaymentRequest request) {
        return visaClient.sendAVR(request);
    }

    /**
     * Sends a payment authorization request to the configured Visa client.
     *
     * @param request The payment request with fare and card data.
     * @return true if the payment is authorized, false otherwise.
     */
    public boolean sendAuthorization(PaymentRequest request) {
        return visaClient.sendAuthorization(request);
    }
}

