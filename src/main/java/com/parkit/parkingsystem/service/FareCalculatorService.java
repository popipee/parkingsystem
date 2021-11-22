package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.time.Duration;

public class FareCalculatorService {

    /**
     * Calculate the fare regarding a ticket's information
     * @param ticket is the ticket concerned by the fare calculation
     */
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double duration = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes();

        if(duration>30){
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice((duration / 60.0) * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice((duration / 60.0) * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }

        else {
            freeParking(ticket);
        }

        if(ticket.getAlreadyExists()){
            applyDiscount(ticket);
        }
    }

    /**
     * Apply a discount of 5 percent on a ticket's price.
     * @param ticket is the tocket concerned by the discount
     */
    public void applyDiscount (Ticket ticket) {
        ticket.setPrice(ticket.getPrice()*0.95);
    }

    /**
     * Set the price to 0 for a specified ticket
     * @param ticket is the ticket concerned by the fees' suppression.
     */
    public void freeParking (Ticket ticket) {
        ticket.setPrice(0);
    }
}