package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionManager {

	public static Connection conn = null;
	
	private static HikariDataSource dataSource = null;
	 
	static {
		try {
			FileInputStream fis = new FileInputStream("G:/workspace/IGTest/configs/application.properties");
			
			Properties propObj = new Properties();
			propObj.load(fis);
			
			HikariConfig config = new HikariConfig();

	        config.setJdbcUrl(propObj.getProperty("MYSQL_DB_URL"));
	        config.setUsername(propObj.getProperty("MYSQL_DB_USERNAME"));
	        config.setPassword(propObj.getProperty("MYSQL_DB_PASSWORD"));
	        
	        config.addDataSourceProperty("minimumIdle", Integer.parseInt(propObj.getProperty("DB_MIN_CONNECTION")));
	        config.addDataSourceProperty("maximumPoolSize", Integer.parseInt(propObj.getProperty("DB_MAX_CONNECTION")));
	        
	        dataSource = new HikariDataSource(config);
	        //dataSource.setAutoCommit(false);
	        
		} catch(IOException e) {
			e.printStackTrace();
		}
		
        
    }
	public static Connection getConnection() {
		try {
			conn = dataSource.getConnection();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	public static void closeConnection() {
		try {
			if(conn != null) {
				conn.close();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
