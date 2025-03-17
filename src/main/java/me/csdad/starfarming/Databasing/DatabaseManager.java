package me.csdad.starfarming.Databasing;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.csdad.starfarming.Core;

// this class should only be used in the Core class.
// child dependencies should pull a connection from the established pool, so that we're not
// actively trying to check and establish a conn after the plugin has loaded.
public class DatabaseManager {
	
	
	private Core plugin;
	private boolean useMySQL;
	
	// create a new hikari datasource for use by our manager
	private HikariDataSource dataSource;
	
	public DatabaseManager() {
		// init the plugin so we can grab config option
		plugin = Core.getInstance();
		
		useMySQL = this.plugin.getConfig().getBoolean("database.use-mysql");
		
		// init the database
		setupDatabase();
	}
	
	private void setupDatabase() {
		
		// create a new hikari config object for connection pooling
		HikariConfig config = new HikariConfig();
		
		// if the owner has enabled sql, we'll pull the necessary information from the config
		if(useMySQL) { 
			
			// all of the info
			String host = plugin.getConfig().getString("database.mysql.host");
			int port = plugin.getConfig().getInt("database.mysql.port");
			String database = plugin.getConfig().getString("database.mysql.database");
			String username = plugin.getConfig().getString("database.mysql.username");
			String password = plugin.getConfig().getString("database.mysql.password");
			
			// set the pools connection url
			config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false");
			
			// set the user/pass
            config.setUsername(username);
            config.setPassword(password);
			
		} // otherwise the plugin will utilize SQLlite for all data needs.
		else {
			File databaseFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("database.sqlite.database-file"));
			
			
			// set the sqlite file in hikari's cp
			config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
		}
		
		// configure the connection pool
		config.setMaximumPoolSize(10);
		config.setMinimumIdle(2); // min idle conns
		config.setConnectionTimeout(30000); // 30s
		config.setIdleTimeout(600000); // 10m
		config.setMaxLifetime(180000); // 30m
		
		
	    dataSource = new HikariDataSource(config);
		
		
		Bukkit.getLogger().info("Connected to " + (useMySQL ? "MySQL" : "SQLite") + " database.");
		
	}
	
	/**
	 * Method to get a connection from the conn pool
	 * @return A connection object to perform SQL operations with
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	/**
	 * Method to close the connection pool safely.
	 */
	public void close() {
		if(dataSource != null) {
			dataSource.close();
		}
	}

}
