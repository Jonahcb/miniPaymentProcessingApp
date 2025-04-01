package com.jonah.payment.core;

import javax.net.ssl.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * VisaSSLContextLoader is responsible for creating an SSLContext configured
 * for mutual TLS authentication using a client certificate and private key.
 * This SSLContext is used when communicating with Visa's APIs securely.
 */
public class VisaSSLContextLoader {

    /**
     * Loads an SSLContext using a PEM client certificate and private key.
     * The method also uses the default JVM trust store to validate the Visa server certificate.
     *
     * @param certPath Path to the PEM-encoded client certificate file.
     * @param keyPath Path to the PEM-encoded private key file.
     * @param unusedCaPaths Array of unused CA certificate file paths (kept for compatibility).
     * @return Configured SSLContext instance ready for mutual TLS.
     * @throws Exception If an error occurs while reading, parsing, or initializing SSL context.
     */
    public static SSLContext loadSSLContext(String certPath, String keyPath, String[] unusedCaPaths) throws Exception {
        System.out.println("\uD83D\uDD10 Loading Visa client cert and key for mutual TLS...");

        // ─────────────────────────────────────────────────────────────
        // Read and decode the PEM-encoded certificate file
        // ─────────────────────────────────────────────────────────────
        String certPem = Files.readString(Paths.get(certPath));
        String certContent = certPem.replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
        byte[] certBytes = Base64.getDecoder().decode(certContent);

        // Generate a Java X509Certificate from the decoded bytes
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));

        // ─────────────────────────────────────────────────────────────
        // Read and decode the PEM-encoded private key file
        // ─────────────────────────────────────────────────────────────
        String keyPem = Files.readString(Paths.get(keyPath));
        String keyContent = keyPem.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(keyContent);

        // Generate a PrivateKey object using RSA algorithm from the decoded key bytes
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

        // ─────────────────────────────────────────────────────────────
        // Create an in-memory KeyStore with the certificate and private key
        // ─────────────────────────────────────────────────────────────
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null); // Initialize empty KeyStore
        ks.setKeyEntry("client", privateKey, new char[0], new X509Certificate[]{cert});

        // Prepare a KeyManagerFactory with the client identity from the KeyStore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, new char[0]);

        // Prepare a TrustManagerFactory using the JVM's default trusted CA set
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);

        // Final SSLContext initialization with both key and trust managers
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }
}

