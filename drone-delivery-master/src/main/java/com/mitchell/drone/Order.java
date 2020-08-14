package com.mitchell.drone;

import java.io.IOException;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.MINUTES;

public class Order {
    private String id;
    private Coordinates coordinates;
    private LocalTime timestamp;
    private double travelTime;

    Order(String id, Coordinates coordinates, LocalTime timestamp) {
        this.id = id;
        this.coordinates = coordinates;
        this.timestamp = timestamp;

        calculateTravelTime();
    }

    public String getId() {
        return id;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public static Order StringToOrder(String line) throws IOException {
        Pattern r = Pattern.compile("(\\S+)\\s(\\S+)\\s(\\S+)");
        Matcher m = r.matcher(line);

        if(m.find()) {
            Pattern coordR = Pattern.compile("([NS])(\\d+)([EW])(\\d+)");
            Matcher coordM = coordR.matcher(m.group(2));

            if(coordM.find()) {
                int nsCoordinate = parseInt(coordM.group(2));
                int ewCoordinate = parseInt(coordM.group(4));

                if("S".equals(coordM.group(1))) {
                    nsCoordinate = -nsCoordinate;
                }

                if("W".equals(coordM.group(3))) {
                    ewCoordinate = -ewCoordinate;
                }

                Coordinates coordinates = new Coordinates(nsCoordinate, ewCoordinate);
                return new Order(m.group(1), coordinates, LocalTime.parse(m.group(3)));
            } else {
                throw new IOException("Unable to parse line to Order");
            }
        } else {
            throw new IOException("Unable to parse line to Order");
        }
    }

    public int getWiggleRoom(LocalTime currentTime) {
        int minutesSinceOrder = (int) timestamp.until(currentTime, MINUTES);
        int timeToCap = minutesSinceOrder + (int) Math.ceil(travelTime);

        if(timeToCap <= 60) {
            return 60 - timeToCap;
        } else if (timeToCap <= 180) {
            return 180 - timeToCap;
        } else {
            return -1;
        }
    }

    private void calculateTravelTime() {
        travelTime = Math.sqrt((coordinates.getNsCoord() * coordinates.getNsCoord()) + (coordinates.getEwCoord() * coordinates.getEwCoord()));
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", coordinates='" + coordinates + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
