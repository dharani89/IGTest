package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class Order {

	private int orderId;
	private int side;
	private int quantity;
	private double price;
	private String symbol;
	
	public Order(int side, int quantity,double price, String symbol) {
		this.side = side;
		this.quantity = quantity;
		this.price = price;
		this.symbol = symbol;
		
		//Add To DB
		this.orderId = createOrder(side,quantity,price,symbol);
	}

	public Order(int orderId,int side, int quantity,double price, String symbol) {
		this.side = side;
		this.quantity = quantity;
		this.price = price;
		this.symbol = symbol;
		
		this.orderId = orderId;
	}

	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	/* Creates Order entry in DB */
	public int createOrder(int side, int quantity, double price, String symbol) {
		// TODO Auto-generated method stub
		int orderId = -1;
		long time = System.currentTimeMillis();
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			String query = "insert into testdb.order (side,quantity,price,symbol,created_on) values (?, ?, ?, ?,?)";
			PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setInt (1, side);
			preparedStmt.setInt (2, quantity);
			preparedStmt.setDouble(3, price);
			preparedStmt.setString(4, symbol);
			preparedStmt.setObject(5,  new java.sql.Timestamp(time));
	      
			preparedStmt.executeUpdate();
			
			ResultSet rs = preparedStmt.getGeneratedKeys();
			while(rs.next())
			{
				orderId = rs.getInt(1);
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
	      
		return orderId;
	}
	
	/* Updates/Modifies the Order in DB with given quantity and price */
	public void updateOrd() {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			String query = "update testdb.order set quantity = ? , price  = ? where order_id=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt (1, this.quantity);
			preparedStmt.setDouble (2, this.price);
			preparedStmt.setInt(3, this.orderId);
	      
			preparedStmt.executeUpdate();
			
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
	}
	
	/* Removes the Order entry from DB */
	public void removeOrd() {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			String query = "delete from testdb.order where order_id=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt (1, this.orderId);

			preparedStmt.executeUpdate();
			
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
	}
}