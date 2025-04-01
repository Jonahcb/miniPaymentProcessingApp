package com.jonah.payment.core;

import com.jonah.payment.data.PaymentRequest;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;

/**
 * XMLParser provides utility functionality to convert an XML string
 * into a PaymentRequest Java object using JAXB (Java Architecture for XML Binding).
 */
public class XMLParser {

    /**
     * Deserializes a raw XML string into a PaymentRequest instance.
     * This method uses JAXB to automatically bind XML elements to their
     * corresponding fields in the PaymentRequest class (and nested CardData).
     *
     * @param xml The XML input string representing a PaymentRequest.
     * @return A deserialized PaymentRequest object.
     * @throws JAXBException if the XML is malformed or does not match expected structure.
     */
    public static PaymentRequest fromXml(String xml) throws JAXBException {
        // Create a new JAXB context for the PaymentRequest class (root XML element)
        JAXBContext context = JAXBContext.newInstance(PaymentRequest.class);

        // Create an unmarshaller from the context
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Unmarshal the XML string to a Java object
        return (PaymentRequest) unmarshaller.unmarshal(new StringReader(xml));
    }
}

