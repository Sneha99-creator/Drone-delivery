package com.mitchell.drone;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class DroneDeliveryTest {

    private static Order order1, order2, order3, order4, order5, order6, order7, order8;

    @BeforeAll
    static void setUp() {
        order1 = new Order("WM001", new Coordinates(11, -5), LocalTime.parse("05:11:50"));
        order2 = new Order("WM002", new Coordinates(-3, 2), LocalTime.parse("05:11:55"));
        order3 = new Order("WM003", new Coordinates(7, 50), LocalTime.parse("05:31:50"));
        order4 = new Order("WM004", new Coordinates(11, 5), LocalTime.parse("06:01:50"));

        order5 = new Order("WM005", new Coordinates(11, -5), LocalTime.parse("20:11:50"));
        order6 = new Order("WM006", new Coordinates(-3, 2), LocalTime.parse("20:11:55"));
        order7 = new Order("WM007", new Coordinates(7, 50), LocalTime.parse("20:31:50"));
        order8 = new Order("WM008", new Coordinates(11, 5), LocalTime.parse("21:01:50"));
    }

    @Test
    void selectingOrders1() {
        List<Order> orders = new ArrayList<>();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        Order selectedOrder = DroneDelivery.selectOrder(orders, LocalTime.parse("06:00:00"));
        Assertions.assertEquals("WM002", selectedOrder.getId());
    }

    @Test
    void selectingOrders2() {
        List<Order> orders = new ArrayList<>();

        orders.add(order1);
        orders.add(order3);
        orders.add(order4);

        Order selectedOrder = DroneDelivery.selectOrder(orders, LocalTime.parse("06:07:14"));

        //Executing this test, either WM001 or WM004 will be selected.
        //They have the same score, so which one is picked is non-deterministic
        Boolean doesMatch = "WM001".equals(selectedOrder.getId()) || "WM004".equals(selectedOrder.getId());

        Assertions.assertEquals(true, doesMatch);
    }

    @Test
    void selectingOrders3() {
        List<Order> orders = new ArrayList<>();

        orders.add(order3);
        orders.add(order4);

        Order selectedOrder = DroneDelivery.selectOrder(orders, LocalTime.parse("06:31:24"));
        Assertions.assertEquals("WM004", selectedOrder.getId());
    }

    @Test
    void executeOrders1() {
        List<Order> orders = new ArrayList<>();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        String result = DroneDelivery.executeOrders(orders);

        //The fact that the executeOrders function returns a string that represents the output file
        //makes this difficult to test.

        String[] lines = result.split("\n");

        //All 5 orders get delivered.
        Assertions.assertEquals(5, lines.length);
        Assertions.assertEquals("NPS 75", lines[lines.length - 1]);
    }

    @Test
    void executeOrders2() {
        List<Order> orders = new ArrayList<>();

        orders.add(order5);
        orders.add(order6);
        orders.add(order7);
        orders.add(order8);

        String result = DroneDelivery.executeOrders(orders);

        //The fact that the executeOrders function returns a string that represents the output file
        //makes this difficult to test.

        String[] lines = result.split("\n");

        //Only 2 orders get executed, since we run out of time.
        Assertions.assertEquals(3, lines.length);
        Assertions.assertEquals("NPS 0", lines[lines.length - 1]);
    }
}
