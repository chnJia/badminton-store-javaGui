package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Connect {
	private static final String USERNAME = "root"; 
	private static final String PASSWORD = ""; 
	private static final String DATABASE = "ho-ohdie";
	private static final String HOST = "localhost:3306";
	private static final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);
	
	public ResultSet rs; 
	public ResultSetMetaData rsm;
	public Statement st; 
	private Connection connect;
	
	public static Connect instance; 
	
	public static Connect getInstance() {
		if (instance == null) instance = new Connect();
		return instance;
	}
	
	private Connect() {
		try {
			connect = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
			st = connect.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execQuery(String query) {
		try {
			rs = st.executeQuery(query);
			rsm = rs.getMetaData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execUpdate(String query) {
		try {
			st.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}