package org.jianyi.yibuyiqu.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGPoolingDataSource;

public class PostgresDataSource {
	
	private static PostgresDataSource datasource;
	
	private PGPoolingDataSource source;
	
	private PostgresDataSource() {
		source = new PGPoolingDataSource();
		//source.setDataSourceName("PGDataSource");
		source.setServerName("localhost");
		source.setDatabaseName("yi");
		source.setUser("yi");
		source.setPassword("yi");
		source.setMaxConnections(10);
	}
	
	public Connection getConnection(){
		Connection conn = null;
		try {
			conn = source.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	public void close() {
		source.close();
	}
	
	public static PostgresDataSource getInstance()  {
		if (datasource == null) {
			datasource = new PostgresDataSource();
		}
		return datasource;
	}
	

}
