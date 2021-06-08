package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;


public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct

        double time = (outHour.getTime() - inHour.getTime()); // result in milliseconds
        time = time / (60*60*1000); // convert from milliseconds to hours

        double duration = time < 0.5 ? time = 0 : time; // 30 mins free functionnality

        double CAR_RATE = Fare.CAR_RATE_PER_HOUR;
        double BIKE_RATE = Fare.BIKE_RATE_PER_HOUR;

        if (ticket.getFidelity()==true){
            System.out.println(" > Utilisateur Récurrent Reconnu");

            BIKE_RATE= BIKE_RATE*(1.0-5.0/100);
            CAR_RATE=CAR_RATE*(1.0-(5.0/100));
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * CAR_RATE);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * BIKE_RATE);
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}