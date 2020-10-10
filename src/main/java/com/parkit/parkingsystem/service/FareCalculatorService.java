package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
           throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        /** We will use getTime method to get the time
         as milliseconds and then convert it to hours.
         The duration variable should be a double.
         */

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();



        double duration = (double) (outHour -inHour) /(1000*3600);


        /** create a condition if duration is less than
         * thirty minutes, the duration will be 0.
         * Else 30 minute will deducted from the duration
         */
        if (duration < 0.5)
            duration = 0;
        else
            duration= duration - 0.5;




        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
