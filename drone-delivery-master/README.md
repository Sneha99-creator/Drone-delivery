
### Drone Delivery Challenge

This program implements an algorithm to schedule drone
deliveries to customers in a small town. This town is a 
perfect grid, with deliveries originating at a warehouse 
and drone-launch facility at the center, so the warehouse is
located at (0,0). 

The town only owns one drone, which is allowed to operate from
6 a.m. until 10 p.m. The drone's "ground speed" is exactly one
horizontal or vertical grid block per minute, and the drone can 
move vertically, horizontally, or diagonally. The drone can 
only hold one delivery at a time, so it must return back to 
the warehouse between orders. (If the drone could hold packages
intended for multiple customers, we would also need to 
consider methods for making sure that the packages end up with
 the right customer.) For simplicity we're also 
disregarding the time that it would take to load or unload
a package from the drone.

Given a list of orders, this program schedules the 
order in which the orders should be fulfilled to maximize
the net promoter score (NPS). The **Net Promoter Score**
is the percentage of promoters minus the percentage of detractors.
See the assignment sheet for more information regarding the NPS.

#### Input

The program accepts a file that contains one order per line.
Each order has an order identifier, followed by a space, the 
customer's grid coordinate, another space, and finally the order
timestamp. The following is an example order entry:

```
WM0001 N11W5 05:11:50
...
```

#### Output

The program produces a file that contains one output line for
each customer order. Each line includes the order number 
and the drone's departure time. The last line in the output
file should contain the estimated NPS score. For example:

```
WM002 06:00:00
... 
NPS 87
```

#### Implementation

How do we pick the order in which orders should be fulfilled?

We're fortunate that our input file contains the orders for
the entire day, but in reality, when considering which order to
fulfill, we can only pick between the orders that have come in 
before the drone leaves with another delivery. So first we need
to filter out all of the orders that we "don't have yet".

Then for the orders that are left, we need to determine how
many minutes we have left to still get a promoter or neutral score
for that order. To determine this, we need to take into account
how long ago the order was placed, and the time it takes for the 
drone to reach the order location. Then, for each order, we determine
how many other orders would miss their promoter or neutral score
window because the drone went on that order. For each other order
that it interferes with, that order will get a -1 score which will
count against us choosing to fulfill that order next.

Once we obtain the "interference" scores for each order, we
will pick one of the orders with the highest score to fulfill next.
This continues until all of the orders have been fulfilled
or if the drone cannot go on an order and return to the warehouse
before 10 p.m. If there are orders left undelivered, those 
contribute to the detractors when calculating the NPS

#### Running the Application

```
 ./gradlew run --args='SampleOrders.txt output.txt' --info
 ```
Where SampleOrders.txt is the path to the input file, and output.txt
is the path to the output file 
 
 
#### Running tests
 
 ```
 ./gradlew test --info 
 ```
 An HTML report describing the outcome of the tests can 
 be found at `/build/reports/tests/test/index.html`

