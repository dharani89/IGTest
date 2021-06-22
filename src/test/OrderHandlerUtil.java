package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OrderHandlerUtil {

	/* Gives the total number of Quantity from orders for the given Price */
	public int totalQuantity(List<Order> ordLst) {
		
		int totalQuan = 0;
		for(Order ord: ordLst) {
			totalQuan += ord.getQuantity();
		}
		return totalQuan;
	}

	/* Returns the unique symbols from DB */
	public List<String> getSymbols() {
		// TODO Auto-generated method stub
		Connection conn = null;
		List<String> symbols = new ArrayList<String>();
		try {
			conn = ConnectionManager.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select distinct symbol from testdb.order");
            
            while (rs.next()) {
            	symbols.add(rs.getString("SYMBOL"));
            }
			
		} catch (SQLException e){
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	      try {
		      conn.rollback();
		  } catch(SQLException se){
		    	  System.err.println(se.getMessage());
		  }
	    } finally {
	    	if(conn != null) {
		    	  ConnectionManager.closeConnection();
		      }
	    }
		return symbols;
	}

	/* Returns Order identified by the orderid */
	public Order findOrder(int orderId) {
		
		Order order = null;
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			
			String query = "select * from testdb.order where order_id=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt (1, orderId);
			
			ResultSet rs = preparedStmt.executeQuery();
            
            while (rs.next()) {
            	
            	order = new Order(rs.getInt("ORDER_ID"),rs.getInt("SIDE"),rs.getInt("QUANTITY"),rs.getDouble("PRICE"),rs.getString("SYMBOL"));
            }
            
			
		} catch (SQLException e){
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
			try {
			      conn.rollback();
			  } catch(SQLException se){
			    	  System.err.println(se.getMessage());
			  }
	    } finally {
	    	if(conn != null)
				ConnectionManager.closeConnection();
	    }
		
		return order;
	}

	/* Returns the List of Orders for the specific Symbol for both Ask and Bid */
	public List<Order> getOrderList(String symbol) {
		
		List<Order> ordLst = new LinkedList<Order>();
		Connection conn = null;
		try {
			conn =  ConnectionManager.getConnection();
			String query = "select * from testdb.order where symbol=? order by created_on";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString (1, symbol);
			
			ResultSet rs = preparedStmt.executeQuery();
            
            while (rs.next()) {
            	Order order = new Order(rs.getInt("ORDER_ID"),rs.getInt("SIDE"),rs.getInt("QUANTITY"),rs.getDouble("PRICE"),rs.getString("SYMBOL"));
            	ordLst.add(order);
            }
			
		} catch (SQLException e){
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	      try {
		      conn.rollback();
		  } catch(SQLException se){
		    	  System.err.println(se.getMessage());
		  }
	    } finally {
	    	if(conn != null)
	    		ConnectionManager.closeConnection();
	    }
		return ordLst;
	}
	
	/*Returns List of price for the symbol */
	public List<Double> getOrderListBySymbolAndSide(String symbol, int side) {
		List<Double> priceLst = new ArrayList<Double>();
		Connection conn = null;
		try {
			conn =  ConnectionManager.getConnection();
			String query = "select price from testdb.order where symbol=? and side=? order by created_on";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString (1, symbol);
			preparedStmt.setInt (2, side);
			
			ResultSet rs = preparedStmt.executeQuery();
            
            while (rs.next()) {
            	priceLst.add(rs.getDouble(1));
            }
			
		} catch (SQLException e){
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	      try {
		      conn.rollback();
		  } catch(SQLException se){
		    	  System.err.println(se.getMessage());
		  }
	    } finally {
	    	if(conn != null)
	    		ConnectionManager.closeConnection();
	    }
		return priceLst;
	}

	/* Returns list of Orders for the symbol,side,price */
	public List<Order> getOrderListBySymbolAndSideAndPrice(String symbol, int side, double price) {
		List<Order> ordLst = new LinkedList<Order>();
		Connection conn = null;
		try {
			conn =  ConnectionManager.getConnection();
			String query = "select * from testdb.order where symbol=? and side = ? and price = ? order by created_on";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString (1, symbol);
			preparedStmt.setInt (2, side);
			preparedStmt.setDouble (3, price);
			
			ResultSet rs = preparedStmt.executeQuery();
            
            while (rs.next()) {
            	Order order = new Order(rs.getInt("ORDER_ID"),rs.getInt("SIDE"),rs.getInt("QUANTITY"),rs.getDouble("PRICE"),rs.getString("SYMBOL"));
            	ordLst.add(order);
            }
			
		} catch (SQLException e){
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	      try {
	    	  conn.rollback();
	      } catch(SQLException se){
	    	  System.err.println(se.getMessage());
	      }
	    } finally {
	    	if(conn != null)
	    		ConnectionManager.closeConnection();
	    }
		return ordLst;
	}
}
