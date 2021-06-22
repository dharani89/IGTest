package test;

public interface OrderHandler {
	void addOrder(Side side, int quantity, double price, String symbol);
	void modifyOrder(int orderId, int newQuantity, double newPrice);
	void removeOrder(int orderId);
	double getPrice(String symbol, int quantity, Side side);
}