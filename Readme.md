# OrderHandler to build OrderBook

Implemented OrderHandler interface.

# Methods:

1. void addOrder(Side side, int quantity, double price, String symbol);
 - Adds the order request to DB
 - Creates/Updates OrderBook
 - Generates Trade if matching order available for sell and buy

2. void modifyOrder(int orderId, int newQuantity, double newPrice);
 - Updates the existing Order with Price or Quantity
 - Updates the OrderBook

3. void removeOrder(int orderId);
 - Removes the existing order by OrderId 
 - Updates the OrderBook

4. double getPrice(String symbol, int quantity, Side side);
 - Calculate Price with available Orders for the symbol on the OrderBook

# Database table

TableName: order
Fields:
order_id
side
quantity
price
symbol
created_on

# External Libraries

Used Below External Library/JARs for connecting DB and utilizing Connecion Pool

```bash
HikariCP-4.0.1.jar
slf4j-api-2.0.0-alpha1.jar

mysql-connector-java-8.0.18.jar
```

# File Info

1. OrderHandler - Interface given for implementation
2. OrderHandlerImpl - Implementation of OrderHandler interface
3. Order - Order class to access Database Table and methods
4. OrderBook - OrderBook for the specific Symbol. Used to build OrderBooks in 5. OrderHandlerImpl
6. ConnectionManager - Handling DB Connection Activity
7. OrderHandlerApp - Having main method to test OrderHandler Interface implemenation
8. application.properties - Having DB connectivity configs

# Execution

Please use below command to build and start the Applicaion
under IGTest\src\test

```bash
javac *.java
java OrderHandlerApp
```
