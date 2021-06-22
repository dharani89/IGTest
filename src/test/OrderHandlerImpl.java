package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.sql.*;  


enum Side {
	SELL(1),
	BUY(2);
	
	public final int value;

    private Side(int value) {
        this.value = value;
    }
}
enum DBAction {
	MODIFY(1),
	DELETE(2);
	public final int value;

    private DBAction(int value) {
        this.value = value;
    }
}
public class OrderHandlerImpl implements OrderHandler {

	private Map<String, OrderBook> orderBooks = null;
	public static Connection conn = null;
	public OrderHandlerUtil ordUtil = new OrderHandlerUtil();
	
	public OrderHandlerImpl() {
		orderBooks = new HashMap<String, OrderBook>();
		//load orderBooks from DB - Helps in case of app restart or getPrice to access without DB hit
		
		loadOrderBooks();
		System.out.println("Initial Load of OrderBook from DB success on Application start");
	}
		
	/* Adds Order Request to DB, Updates OrderBook, matches with existing order to validate and generate the Trade */
	@Override
	public void addOrder(Side side, int quantity, double price, String symbol) {
		
		OrderBook book = null;
		//Create OrderBook for the symbol if not available already
		if (!orderBooks.containsKey(symbol)) {
	          book = new OrderBook(symbol);
	          orderBooks.put(symbol, book);
		}
		else
			book = getOrderBook(symbol);
		
		Order ord = new Order(side.value, quantity, price, symbol);
				
		System.out.println("addOrder symbol: " +  ord.getSymbol() + " Price: " + ord.getPrice() + " Qty: "+ ord.getQuantity() + " Side: "+ side);
				
		//Add to new order to OrderBook for the specific symbol
		boolean matchOrd = true;
    	book.addOrder(ord, matchOrd);
	}

	/* Modifies the Order in DB, refreshes the OrderBook*/
	@Override
	public void modifyOrder(int orderId, int newQuantity, double newPrice ) {
		
		Order modOrd = ordUtil.findOrder(orderId);
		boolean priceUpd = false;
		boolean quantUpd = false;
		double oldPrice;
		//modify the order in DB and refresh the OrderBook for the symbol
		if(modOrd != null) {
			oldPrice = modOrd.getPrice();
			if(modOrd.getPrice() != newPrice)
			{
				modOrd.setPrice(newPrice);
				priceUpd = true;
			}
			if(modOrd.getQuantity() != newQuantity )
			{
				modOrd.setQuantity(newQuantity);
				quantUpd = true;
			}
			if(quantUpd || priceUpd) {
				modOrd.updateOrd();	
				updateOrderBook(modOrd,DBAction.MODIFY,priceUpd,oldPrice);
				
			}
		}
		else{
			System.out.println("Requested resource up-to-date or not available now");
		}
				
	}
	
	/* Removes the order from DB, refreshes OrderBook*/
	@Override
	public void removeOrder(int orderId) {
		
		Order modOrd = ordUtil.findOrder(orderId);
		
		//remove Order from DB and refresh OrderBook for the symbol 
		if(modOrd != null) {
			updateOrderBook(modOrd,DBAction.DELETE, false, -1);
			modOrd.removeOrd();
		}
		else {
			System.out.println("Requested resource not available now");
		}
		
	}

	/* Access Available OrderBook and generate best Price - No DB hit as OrderBook already available on App Launch*/
	/* Performance Optimization done by 1. Not hitting DB, 2. Using best Java Data structure like TreeMap, Queue */
	@Override
	public double getPrice(String symbol, int quantity, Side side) {
		// TODO Auto-generated method stub
		System.out.println("getPrice for Symbol: "+ symbol +" Quantity: "+ quantity + " Side: "+side);
		
		double price = 0;
		boolean priceCalculated = false;
		Map<Double, List<Order>> priceOrdMap = null;
		
		int orgReqQuantity = quantity;
		
		OrderBook book = null;
		//Get OrderBook for the symbol
		if (orderBooks.containsKey(symbol)) {
	          book =  getOrderBook(symbol);
		}
		else
		{
			System.out.println("No Orders Available to generate price");
			return 0;
		}
		
		//Get Resting orders with price and quantity for the specific Side(Buy/Sell)
		if(side == Side.SELL) {
			priceOrdMap = book.getSellOrderMap();
		}
		else if(side == Side.BUY) {
			priceOrdMap = book.getBuyOrderMap();
		}

		//Calculate Price from Orders available
		while(!priceCalculated && (priceOrdMap.size() > 0)) {
			
			for (Double p: priceOrdMap.keySet()) {
				
				int totQuanForPrice = ordUtil.totalQuantity(priceOrdMap.get(p));
				
				if(totQuanForPrice <= 0) {
					continue;
				}
				else if(totQuanForPrice >= quantity) {
					price += p * quantity;
					priceCalculated  = true;
					break;
				}
				else if(totQuanForPrice < quantity) {
					price += p * totQuanForPrice;
					quantity = quantity - totQuanForPrice;
				}
			}
			/* No Orders Available to calculate Price */
			if(price <=0)
				priceCalculated  = true;
		}
		price = price/orgReqQuantity;
		System.out.println("Available Price for the request: "+price);
		return price;
	}	

	/* Returns the OrderBook for specific symbol */
	private OrderBook getOrderBook(String symbol) {
		OrderBook book = orderBooks.get(symbol);
		return book;
	}
	
	/*Loads the OrderBooks for the all available symbol from DB */
	private void loadOrderBooks() {
		
		List<String> symbols = ordUtil.getSymbols();

		for(String symbol: symbols) {
			//Create fresh OrderBook
			OrderBook book = new OrderBook(symbol);
			boolean matchOrd = false;
			
			List<Order> ordLst = ordUtil.getOrderList(symbol); 
			
			//Update OrderBook with DB orders
			for(Order ord: ordLst) {		
				book.addOrder(ord, matchOrd);
			}
			
			orderBooks.put(symbol, book);
		}         
	}
	
	/* Updates the OrderBook on User Modify/Delete on Order - Minimizes DB Hit */
	private void updateOrderBook(Order ord, DBAction action, boolean priceUpd, double price) {
		System.out.println("Update OrderBook on DB update/delete with priceUpd: " + priceUpd);
		
		OrderBook book = getOrderBook(ord.getSymbol());
		Map<Double, List<Order>> priceOrdMap = null;
		PriorityQueue<Double> priceQueue = null;
		List<Order> ordLst = null;
		
		if(ord.getSide() == Side.SELL.value) {
			priceOrdMap = book.getSellOrderMap();
			priceQueue = new PriorityQueue<Double>(500);
		}
		else if(ord.getSide() == Side.BUY.value) {
			priceOrdMap = book.getBuyOrderMap();
			priceQueue = new PriorityQueue<Double>(500, Collections.reverseOrder()); 
		}
		
		if(action == DBAction.DELETE) {
			ordLst = priceOrdMap.get(ord.getPrice());
			//Remove the matching order from order List
			for(int i=0; i < ordLst.size(); i++) {
				if(ordLst.get(i).getOrderId() == ord.getOrderId()) {
					ordLst.remove(i);
					break;
				}
			}
			if(ordLst.size() > 0)
				priceOrdMap.put(ord.getPrice(), ordLst);
			else
				priceOrdMap.remove(ord.getPrice());
		}
		else if(action == DBAction.MODIFY) {
			//if Price changing -> Load Bid/Sell OrderList from DB and map to OrderBook
			
			if(priceUpd) {
				
				//Update PriceList
				List<Double> priceLst = ordUtil.getOrderListBySymbolAndSide(ord.getSymbol(),ord.getSide());
				
				for(double p: priceLst) {
					priceQueue.add(p);
				}
				
				//Update PriceOrder Map for modified prices
				List<Order> dbOrdLst = ordUtil.getOrderListBySymbolAndSideAndPrice(ord.getSymbol(),ord.getSide(),ord.getPrice());
				
				if(dbOrdLst.size() > 0)
					priceOrdMap.put(ord.getPrice(), dbOrdLst);
				else
					priceOrdMap.remove(ord.getPrice());
				
				dbOrdLst = ordUtil.getOrderListBySymbolAndSideAndPrice(ord.getSymbol(),ord.getSide(),price);
				
				if(dbOrdLst.size() > 0)
					priceOrdMap.put(price, ordLst);
				else
					priceOrdMap.remove(price);
			}
			//if Quantity alone changing -> Update matching Order with quantity in OrderBook
			else {
				ordLst = priceOrdMap.get(ord.getPrice());
				for(int i=0; i < ordLst.size(); i++) {
					if(ordLst.get(i).getOrderId() == ord.getOrderId()) {
						ordLst.get(i).setQuantity(ord.getQuantity());
						break;
					}
				}
				if(ordLst.size() > 0)
					priceOrdMap.put(ord.getPrice(), ordLst);
				else
					priceOrdMap.remove(ord.getPrice());
			}
		}
		/* Update Order Book with Latest priceMapOrders and PriceQueue */
		if(ord.getSide() == Side.SELL.value) {
			book.setSellOrderMap(priceOrdMap);
			if(!priceQueue.isEmpty()) {
				book.setSellPriceList(priceQueue);
			}
		}
		else if(ord.getSide() == Side.BUY.value) {
			book.setBuyOrderMap(priceOrdMap);
			if(!priceQueue.isEmpty()) {
				book.setBuyPriceList(priceQueue);
			}
		}
		orderBooks.put(ord.getSymbol(), book);
		       
	}
}
