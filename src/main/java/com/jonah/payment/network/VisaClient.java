package com.jonah.payment.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jonah.payment.core.VisaSSLContextLoader;
import com.jonah.payment.data.CardData;
import com.jonah.payment.data.PaymentRequest;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * VisaClient implements the VisaClientInterface to send real payment requests
 * to VisaNet using mutual TLS and the Visa API endpoints.
 */
public class VisaClient implements VisaClientInterface {

  private final HttpClient client; // The HTTP client used to communicate with Visa API
  private final ObjectMapper mapper = new ObjectMapper(); // JSON mapper for request bodies
  private final String apiKey; // API key for authentication
  private final String apiSecret; // API secret for authentication
  private final String visaUrl; // Visa API endpoint

  /**
   * Initializes the VisaClient by configuring TLS, authentication credentials,
   * and the target Visa endpoint URL.
   */
  public VisaClient() throws Exception {
    this.apiKey = "Stub";    // Replace with your actual sandbox API key
    this.apiSecret = "Stub";  // Replace with your actual sandbox API secret

    this.visaUrl = "https://sandbox.api.visa.com/acs/v3/payments/authorizations/v3/voids";

    // Load mutual TLS context using the provided certificate and key
    SSLContext sslContext = VisaSSLContextLoader.loadSSLContext(
            "visa/cert.pem", // Path to client certificate
            "visa/secret_key.pem", // Path to private key
            new String[] {
                    "visa/SBX-2024-Prod-Inter.pem",
                    "visa/SBX-2024-Prod-Root.pem"
            }
    );

    // Configure HTTP client to use SSL context
    this.client = HttpClient.newBuilder()
            .sslContext(sslContext)
            .build();
  }

  /**
   * Builds a properly structured JSON request compatible with Visa's API
   * from a PaymentRequest object.
   */
  private String buildVisaRequest(PaymentRequest req) throws Exception {
    CardData card = req.getCardData();
    ObjectNode root = mapper.createObjectNode();

    // Add required transaction fields
    root.put("primaryAccountNumber", card.getPan());
    root.put("amount", String.format("%.2f", req.getAmount()));
    root.put("currencyCode", "392"); // Example: JPY. Adjust as needed.
    root.put("retrievalReferenceNumber", "123456789012");
    root.put("systemTraceAuditNumber", 123456);

    // Add EMV fields
    ObjectNode emv = mapper.createObjectNode();
    emv.put("applicationIdentifier", card.getAid());
    emv.put("applicationCryptogram", card.getCryptogram());

    root.set("emv", emv);

    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
  }

  /**
   * Sends an actual authorization request to Visa using HTTPS and mutual TLS.
   *
   * @param req The payment request to be authorized.
   * @return true if the request was approved (HTTP 200), false otherwise.
   */
  @Override
  public boolean sendAuthorization(PaymentRequest req) {
    try {
      String json = buildVisaRequest(req);

      System.out.println("\u27A1\uFE0F Request to Visa:\n" + json);

      HttpRequest httpRequest = HttpRequest.newBuilder()
              .uri(URI.create(visaUrl))
              .header("Content-Type", "application/json")
              .header("Authorization", "Basic " + base64Credentials())
              .POST(HttpRequest.BodyPublishers.ofString(json))
              .build();

      HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

      System.out.println("\u2B05\uFE0F Visa response code: " + response.statusCode());
      System.out.println("\u2B05\uFE0F Visa response body:\n" + response.body());

      return response.statusCode() == 200;

    } catch (Exception e) {
      System.err.println("VisaClient error: " + e.getMessage());
      return false;
    }
  }

  /**
   * Sends an AVR request, reusing the same authorization call but setting amount to 0.
   *
   * @param req The payment request to verify.
   * @return true if approved by Visa, false otherwise.
   */
  @Override
  public boolean sendAVR(PaymentRequest req) {
    req.setAmount(0.00);
    return sendAuthorization(req);
  }

  /**
   * Encodes API credentials using Base64 for HTTP Basic authentication.
   *
   * @return Base64 encoded credentials string.
   */
  private String base64Credentials() {
    String raw = apiKey + ":" + apiSecret;
    return Base64.getEncoder().encodeToString(raw.getBytes());
  }
}





