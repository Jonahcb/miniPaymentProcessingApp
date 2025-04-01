package com.jonah.payment.core;

import com.jonah.payment.data.TapEvent;
import com.jonah.payment.data.PaymentRequest;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * AccountBasedProcessor is responsible for calculating transit fares based on entry and exit tap events.
 * It operates on the principle of account-based ticketing, where fare computation is delayed until exit.
 */
public class AccountBasedProcessor {

    /**
     * Processes a completed tap-out event by calculating the fare between entry and exit.
     * The method directly modifies the PaymentRequest object to set the calculated fare amount.
     *
     * @param payment The mutable PaymentRequest to populate with fare.
     * @param enter The entry TapEvent marking when the rider entered the system.
     * @param exit The exit TapEvent marking when the rider left the system.
     */
    public void processTapFare(PaymentRequest payment, TapEvent enter, TapEvent exit) {
        payment.setAmount(calculateFare(enter, exit));
    }

    /**
     * Computes fare as the duration (in seconds) between entry and exit timestamps.
     * This simplistic fare model is used for demonstration purposes.
     *
     * @param enter The entry tap event.
     * @param exit The exit tap event.
     * @return The number of seconds spent in the system, treated as the fare.
     */
    private double calculateFare(TapEvent enter, TapEvent exit) {
        // Extract timestamps from entry and exit events
        LocalDateTime entryTime = enter.getTimestamp();
        LocalDateTime exitTime = exit.getTimestamp();

        // Calculate the total duration between entry and exit
        long fare = Duration.between(entryTime, exitTime).getSeconds();

        // Return fare as a double (e.g., 75 seconds = $75.00 for demo)
        return fare;
    }
}




