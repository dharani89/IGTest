package test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;


public class OrderBook {
	
	private Map<Double, List<Order>> buyOrderMap = null;
	private Map<Double, List<Order>> sellOrderMap = null;
	
	private PriorityQueue<Double> buyPriceList = null;
	private PriorityQueue<Double> sellPriceList = null;	
	
	public OrderBook(String symbol) {
		buyOrderMap = new TreeMap<Double, List<Order>>(Collections.reverseOrder());
        sellOrderMap = new TreeMap<Double, List<Order>>(Collections.reverseOrder());

        buyPriceList = new PriorityQueue<Double>(500, Collections.reverseOrder()); // top is highest bid price
        sellPriceList = new PriorityQueue<Double>(500); //top is lowest ask price
     
	}
	
	/* Adds order to orderbook */
	public void addOrder(Order newOrd, boolean matchOrd) {
		
		if(newOrd.getSide() == Side.SELL.value) {
			List<Order> orders = getOrders(sellOrderMap, newOrd.getPrice());
			orders.add(newOrd);
	        sellOrderMap.put(newOrd.getPrice(), orders);
	        sellPriceList.add(newOrd.getPrice());
		}
		else if(newOrd.getSide() == Side.BUY.value) {
			List<Order> orders = getOrders(buyOrderMap, newOrd.getPrice());
			orders.add(newOrd);
	        buyOrderMap.put(newOrd.getPrice(), orders);
	        buyPriceList.add(newOrd.getPrice());
		}
		//Match and generate Trade only when new Order requests are coming - not on App Launch OrderBook load
		if(matchOrd)
			matchOrders();
	}
	
	/* Returns order list if price match, otherwise returns new list */
    public List<Order> getOrders(Map<Double, List<Order>> hashmap, Double price)
    {
        List<Order> orders;
        if(hashmap.containsKey(price))
        {
            orders = hashmap.get(price);
        }
        else
        {
            orders = new LinkedList<Order>();
        }
        return orders;
    }
	
    /* Matches ask and bid based on pricetime priority and generates trade */
    public void matchOrders()
    {
        List<Order> bidOrders = null;
        List<Order> askOrders = null;
        Double lowestAsk = null;
        Double highestBid = null;
        boolean finished = false;
       
        while(!finished)
        {
            // Peek because we don't want to remove the top element until the order is closed
            highestBid = buyPriceList.peek();
            lowestAsk = sellPriceList.peek();

            // No possible trade if either list is empty or no bid higher than an ask
            // When Order is ready to sell is less than the ready buyer order -> remove/modify the orders
            if(lowestAsk == null || highestBid == null || lowestAsk > highestBid)
            {
                finished = true;
            	System.out.println("No Trade possible with available orders");
            }
            else
            {
                // Gets Order List for both maps
                bidOrders = buyOrderMap.get(buyPriceList.peek());
                askOrders = sellOrderMap.get(sellPriceList.peek());

                // Gets first element from each OrderList since they're the oldest
                int bidQuantity = bidOrders.get(0).getQuantity();
                int askQuantity = askOrders.get(0).getQuantity();

                if(bidQuantity > askQuantity)
                {	
                    System.out.println(successfulTrade(askQuantity, lowestAsk));

                    // Decrements quantity in bid
                    bidQuantity -= askQuantity;
                    bidOrders.get(0).setQuantity(bidQuantity);
                    bidOrders.get(0).updateOrd();
                	System.out.println("bidQuantity remaining qty : " + bidQuantity);

                    // Closes previous offer
                    askOrders.get(0).removeOrd();
                    askOrders.remove(0);
                    sellPriceList.remove();
                }
                else if(askQuantity > bidQuantity)
                {
                    System.out.println(successfulTrade(bidQuantity, lowestAsk));

                    // Decrements quantity in ask
                    askQuantity -= bidQuantity;
                    askOrders.get(0).setQuantity(askQuantity);
                	System.out.println("askQuantity remaining qty: " + askQuantity);
                	askOrders.get(0).updateOrd();
                	
                    //  Closes previous bid
                	bidOrders.get(0).removeOrd();
                    bidOrders.remove(0);
                    buyPriceList.remove();
                }
                else
                {
                    // bidQuantity and askQuantity are same. lowestAsk is chosen because it's the price at which the trade is made.
                    System.out.println(successfulTrade(bidQuantity, lowestAsk));

                    // Removes bid and ask because they're both closed
                    bidOrders.get(0).removeOrd();
                    bidOrders.remove(0);
                    buyPriceList.remove();
                    
                    askOrders.get(0).removeOrd();
                    askOrders.remove(0);
                    sellPriceList.remove();
                }
            }
        }
    }
    
    /* Generates SuccessfulTrade */
    public String successfulTrade(int quantity, double price)
    {
    	System.out.println("successfulTrade bidQuantity: " + quantity);
    	System.out.println("successfulTrade lowestAsk: " + price);
        return quantity + " shares traded for $" + price + " per share.";
    }
    
    /* Getter and Setter */

	public PriorityQueue<Double> getBuyPriceList() {
		return buyPriceList;
	}

	public void setBuyPriceList(PriorityQueue<Double> buyPriceList) {
		this.buyPriceList = buyPriceList;
	}

	public PriorityQueue<Double> getSellPriceList() {
		return sellPriceList;
	}

	public void setSellPriceList(PriorityQueue<Double> sellPriceList) {
		this.sellPriceList = sellPriceList;
	}

	public Map<Double, List<Order>> getBuyOrderMap() {
		return buyOrderMap;
	}
	
	public void setBuyOrderMap(Map<Double, List<Order>> buyOrderMap) {
		this.buyOrderMap = buyOrderMap;
	}

	public Map<Double, List<Order>> getSellOrderMap() {
		return sellOrderMap;
	}
	
	public void setSellOrderMap(Map<Double, List<Order>> sellOrderMap) {
		this.sellOrderMap = sellOrderMap;
	}
	
}
