package com.mitchell.drone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DroneDelivery {
    protected static int LOAD_TIME = 0;
    protected static int UNLOAD_TIME = 0;
    private static Logger logger = LoggerFactory.getLogger(DroneDelivery.class);

    private static Boolean canReturnBefore10(List<Order> orders, LocalTime currentTime) {
        for(Order order : orders) {
            Delivery delivery = new Delivery(order, currentTime);

            if(delivery.getReturnTime().isBefore(LocalTime.parse("22:00:00"))) {
                return true;
            }
        }

        return false;
    }


    protected static Order selectOrder(List<Order> orders, LocalTime currentTime) {

        Map<Order, Integer> idToWeight = new HashMap<>();

        for(Order order : orders) {
            idToWeight.put(order, 0);

            for(Order otherOrder : orders) {

                if(!order.getId().equals(otherOrder.getId()) && (order.getTravelTime() * 2 > otherOrder.getWiggleRoom(currentTime))) {
                    int currentWeight = idToWeight.get(order);
                    idToWeight.put(order, currentWeight - 1);
                }
            }
        }

        Integer highestWeight = Integer.MIN_VALUE;

        Map<Integer, ArrayList<Order>> weightToIds = new HashMap<>();

        for(Map.Entry<Order, Integer> entry : idToWeight.entrySet()) {
            logger.debug(String.format("%s: %d", entry.getKey().getId(), entry.getValue()));

            if(entry.getValue() > highestWeight) {
                highestWeight = entry.getValue();
            }

            if(weightToIds.get(entry.getValue()) != null) {
                ArrayList<Order> ords = weightToIds.get(entry.getValue());
                ords.add(entry.getKey());
                weightToIds.put(entry.getValue(), ords);
            } else {
                ArrayList<Order> ords = new ArrayList<>();
                ords.add(entry.getKey());
                weightToIds.put(entry.getValue(), ords);
            }
        }

        return weightToIds.get(highestWeight).get(0);
    }

    protected static String executeOrders(List<Order> orders) {
        StringBuilder sb = new StringBuilder();

        int numOrders = orders.size();
        final AtomicReference<LocalTime> currentTime = new AtomicReference<>();
        currentTime.set(LocalTime.parse("06:00:00"));

        int promoters = 0;
        int detractors = 0;

        while(orders.size() > 0 && canReturnBefore10(orders, currentTime.get())) {

            //Get the list of orders that have come in by the time
            //the drone is scheduled to depart
            List<Order> knownOrders = orders.stream()
                    .filter(order -> order.getTimestamp().isBefore(currentTime.get()) ||
                            order.getTimestamp().equals(currentTime.get()))
                    .collect(Collectors.toList());

            logger.info(String.format("CurrentTime: %s, Orders: %s", currentTime.get(),
                   knownOrders.stream().map(order -> order.getId()).collect(Collectors.toList())));

            //Check to see if we have any orders that we can
            //Fulfill at this time. Otherwise, advance the time
            if(knownOrders.size() > 0) {
                Order selectedOrder = selectOrder(knownOrders, currentTime.get());

                logger.info(String.format("Selected: %s",selectedOrder.getId()));

                //TODO: Add time for loading a package.
                Delivery delivery = new Delivery(selectedOrder, currentTime.get());
                currentTime.set(delivery.getReturnTime().plusMinutes(LOAD_TIME));
                sb.append(String.format("%s\n", delivery));

                if(delivery.getPs() > 0) {
                    promoters += 1;
                }

                if(delivery.getPs() < 0) {
                    detractors += 1;
                }

                orders.remove(selectedOrder);
            } else {
                //Set the time to the timestamp of the first order in the orders list
                currentTime.set(orders.get(0).getTimestamp());
            }
        }

        //Check to see if there were any orders that we could not deliver because we ran out of time.
        //Add those to the detractors
        if(orders.size() > 0) {
            logger.info(String.format("%d orders were not delivered", orders.size()));
            detractors += orders.size() * -1;
        }

        int nps = (int) Math.floor(((float) promoters / numOrders * 100) + ((float) detractors / numOrders * 100));
        sb.append(String.format("NPS %d\n", nps));

        return sb.toString();
    }

    public static void main(String[] args) {
        List<Order> orders;
        String inputFilePath;
        String outputFilePath;

        try {
            inputFilePath  = args[0];
            outputFilePath  = args[1];
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Missing Input/Output File Path");
        }

        try {
            Stream<String> stream = Files.lines(Paths.get(inputFilePath));

            orders = stream.map(line -> {
                        try {
                            return Order.StringToOrder(line);
                        } catch(IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    ).collect(Collectors.toList());

            File outputFile = new File(outputFilePath);

            FileWriter fr = new FileWriter(outputFile);
            fr.write(DroneDelivery.executeOrders(orders));
            fr.close();

            System.out.println(outputFile.getAbsolutePath());
        } catch(IOException e) {
            System.out.println("Unable to read from the Input File");
            e.printStackTrace();
        } catch(RuntimeException e) {
            System.out.println("Unable to convert a line to an order in the input file");
            e.printStackTrace();
        }
    }
}
