package com.jonah.payment.core;

import com.jonah.payment.data.TestCardRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * TerminalSimulator mimics a payment terminal sending XML-based payment requests
 * to the backend servlet. It supports entry/exit modes and configurable delays.
 * Useful for testing concurrency and flow without actual hardware.
 */
public class TerminalSimulator implements Runnable {

  // Represents the test configuration for this simulated card tap event
  private final TestCardRequest cardRequest;

  /**
   * Constructs a TerminalSimulator with a test card request configuration.
   *
   * @param cardRequest The parameters for simulating this tap (e.g., PAN, terminal ID, etc.)
   */
  public TerminalSimulator(TestCardRequest cardRequest) {
    this.cardRequest = cardRequest;
  }

  /**
   * Entry point for the thread. Sends the XML transaction and optionally waits after.
   */
  @Override
  public void run() {
    try {
      // Generate the XML payload based on the cardRequest
      String xml = generateCardData();

      // Send the XML payload to the payment servlet
      String response = sendXmlToPPA(xml);

      // Print response for visibility
      System.out.printf("[%s - %s] Response: %s%n",
              cardRequest.terminalId,
              cardRequest.mode.toUpperCase(),
              response);

      // Simulate post-tap delay (e.g., person walking to exit gate)
      Thread.sleep(cardRequest.delayAfterMs);
    } catch (Exception e) {
      System.err.printf("[%s - %s] Terminal failed: %s%n",
              cardRequest.terminalId,
              cardRequest.mode.toUpperCase(),
              e.getMessage());
    }
  }

  /**
   * Generates the XML representation of a PaymentRequest based on the test card input.
   *
   * @return A well-formed PaymentRequest XML string.
   */
  private String generateCardData() {
    return String.format("""
                <PaymentRequest>
                    <TerminalId>%s</TerminalId>
                    <CardData>
                        <PAN>%s</PAN>
                        <Expiry>%s</Expiry>
                        <AID>A0000000031010</AID>
                        <Cryptogram>%s</Cryptogram>
                    </CardData>
                    <Amount>0.00</Amount>
                    <Currency>USD</Currency>
                    <EntryMode>07</EntryMode>
                    <POSConditionCode>00</POSConditionCode>
                    <TVR>8000008000</TVR>
                    <TerminalType>3</TerminalType>
                    <TerminalCapability>8</TerminalCapability>
                    <CardholderVerification>3</CardholderVerification>
                    <MessageReasonCode>5206</MessageReasonCode>
                    <Mode>%s</Mode>
                </PaymentRequest>
                """,
            cardRequest.terminalId,
            cardRequest.pan,
            cardRequest.expiry,
            cardRequest.cryptogram,
            cardRequest.mode);
  }

  /**
   * Sends the given XML transaction to the servlet endpoint and returns the response.
   *
   * @param xmlTransaction The XML request to send.
   * @return The response body returned by the servlet.
   * @throws Exception if network communication or server response fails.
   */
  private String sendXmlToPPA(String xmlTransaction) throws Exception {
    HttpClient client = HttpClient.newHttpClient();

    // Build an HTTP POST request to the servlet
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/miniPaymentProcessingApp/api/payment"))
            .header("Content-Type", "application/xml")
            .POST(HttpRequest.BodyPublishers.ofString(xmlTransaction))
            .build();

    // Send the request and return the response body
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }
}
