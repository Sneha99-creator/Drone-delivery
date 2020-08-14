package com.mitchell.drone;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.mitchell.drone.DroneDelivery.UNLOAD_TIME;
import static java.time.temporal.ChronoUnit.HOURS;

public class Delivery {
    private Order order;
    private LocalTime departureTime;
    private LocalTime deliveryTime;
    private LocalTime returnTime;
    private int ps;

    public Delivery(Order order, LocalTime departureTime) {
        this.order = order;
        this.departureTime = departureTime;

        calculateTravelTime();
    }

    public Order getOrder() {
        return order;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getDeliveryTime() {
        return deliveryTime;
    }

    public LocalTime getReturnTime() {
        return returnTime;
    }

    public int getPs() {
        return ps;
    }

    private void calculateTravelTime() {
        //Assume that the drone starts at 0,0 for each delivery
        double travelTime = order.getTravelTime();
        int travelMinutes = (int) travelTime;
        int travelSeconds = (int) Math.ceil((travelTime - travelMinutes) * 60);


        //System.out.println(travelTime);
        deliveryTime = departureTime.plusMinutes(travelMinutes).plusSeconds(travelSeconds);

        //TODO: Add time to unload a package.
        returnTime = deliveryTime.plusMinutes(travelMinutes + UNLOAD_TIME).plusSeconds(travelSeconds);

        long hoursToDelivery = order.getTimestamp().until(deliveryTime, HOURS);
//        System.out.println(String.format("%s, %s, %d",
//                order.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_TIME),
//                deliveryTime.format(DateTimeFormatter.ISO_LOCAL_TIME), hoursToDelivery));


        if(hoursToDelivery == 0 || hoursToDelivery == 1) {
            ps = 1;
        } else if(hoursToDelivery == 2 || hoursToDelivery == 3) {
            ps = 0;
        } else {
            ps = -1;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", order.getId(), getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
    }
}
