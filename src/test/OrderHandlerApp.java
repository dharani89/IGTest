package test;

public class OrderHandlerApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Trade Order Handler");
		
		callAPIs(); 
	}
	
	/* Test the OrderHandler API Implementation */
	private static void callAPIs() {
		
		OrderHandler ordHand = new OrderHandlerImpl();
		
		/* Test AddOrder */
		ordHand.addOrder(Side.BUY, 2, 28, "IGG");
		ordHand.addOrder(Side.BUY, 5, 30, "IGG");
		ordHand.addOrder(Side.BUY, 3, 23, "IGG");
		
		/* Generates Trade as matching Buy Order Request available */
		ordHand.addOrder(Side.SELL,5,30,"IGG");
		
		/* Generates Trade as matching 2 Buy Order Request available */
		ordHand.addOrder(Side.SELL,5,20,"IGG");
		/* Above all Orders are closed and removed from DB */
		
		/* Add few Orders to not generate Trade and test getPrice */
		ordHand.addOrder(Side.SELL,5,20,"IGG");
		ordHand.addOrder(Side.SELL,20,15,"IGG");
		
		ordHand.addOrder(Side.BUY, 5, 10, "IGG");
		ordHand.addOrder(Side.BUY, 10, 11, "IGG");
		
		/* Test Modify Order - Updates if resource available - Else gives as resource not available */
		ordHand.modifyOrder(160, 2, 20);
		
		/* Test Remove Order - Removes if resource available - Else gives as resource not available*/
		ordHand.removeOrder(3);
		
		/* Test getPrice */
		System.out.println("Price for Selling 3 Quantity on IGG: " + ordHand.getPrice("IGG", 2, Side.SELL));
		System.out.println("Price for Selling 17 Quantity on TEST: " + ordHand.getPrice("TEST", 17, Side.SELL));
		
		System.out.println("Price for Buying 5 Quantity on IGG: " + ordHand.getPrice("IGG", 5, Side.BUY));
		
	}
}
