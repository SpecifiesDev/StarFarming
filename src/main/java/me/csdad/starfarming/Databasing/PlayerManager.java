package me.csdad.starfarming.Databasing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.DataStructures.Players.StarPlayerSettings;
import me.csdad.starfarming.DataStructures.Players.PlayerPerks.FarmingPerks;
import me.csdad.starfarming.Errors.DatabaseLogging;
import me.csdad.starfarming.Errors.GeneralLogging;
import me.csdad.starfarming.Utility.StringFormatting;

public class PlayerManager {
	
	
	/**
	 * Method to check if the player is in the player's table
	 * @param p Player object
	 * @param conn Database connection object
	 * @param forceClose Boolean indicating if this method should return a conn back to the cp or keep it alive for passthrough
	 */
	public boolean checkIfExists(Player p, Connection conn, boolean forceClose) {
		
		boolean exists = false;
		
		try {
			
			// select all from players table where uuid is a wild card
			String sql_statement = "SELECT EXISTS (SELECT 1 FROM players WHERE uuid = ?) AS pe";
			
			PreparedStatement stmt = conn.prepareStatement(sql_statement);
			
			// set the uuid params
			stmt.setString(1, p.getUniqueId().toString());
			
			// execute the query
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				exists = rs.getBoolean("pe");
			} 
			
			if(forceClose) { conn.close(); } // return back to cp
		} catch(SQLException e) {
			
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		} 
		
		

		return exists;
		
		
	}
	
	public void addNewPlayer(Player p, Connection conn, boolean forceClose) {
		
		try {
			
			// insert the new player with default data
			String sql_statement = "INSERT INTO players (uuid, name, experience, starcoins, settings) VALUES(?, ?, ?, ?, ?)";
			
			PreparedStatement stmt = conn.prepareStatement(sql_statement);
			
			stmt.setString(1, p.getUniqueId().toString());
			stmt.setString(2, p.getName());
			stmt.setInt(3, 0); // default experience
			stmt.setInt(4, 0); // default starcoins
			stmt.setString(5, "{\"scoreboard\": \"enabled\"}");
			
			stmt.executeUpdate();
			
			if(forceClose) { conn.close(); } // return back to cp
			
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		}
		
	}
	
	public String getPlayerUUID(String name, Connection conn, boolean forceClose) {
		
		String uuid = null; // returns null if not found
		String sql_statement = "SELECT uuid FROM players WHERE name = ?";
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql_statement);
			
			// set our query parameter
			stmt.setString(1, name);
			
			// result set
			ResultSet rs = stmt.executeQuery();
			
			// if a result is found, get the uuid
			if(rs.next()) uuid = rs.getString("uuid");
			
			// forceclose the conn if enabled
			if(forceClose) { conn.close(); }
			
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		}
		
		// return the uuid
		return uuid;
		
	}
	
	public FarmingPerks createDefaultFarmingPerks(Connection conn, String uuid, boolean forceClose) {
		
	    String query = "INSERT INTO farming_perks (owner_uuid, farming_level, " +
                "perk_1, perk_1_opt, perk_1_opt_2, perk_1_selected, " +
                "perk_2, perk_2_opt, perk_2_opt_2, perk_2_selected, " +
                "perk_3, perk_3_opt, perk_3_opt_2, perk_3_selected) " +
                "VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
	    
	    try {
	    	PreparedStatement stmt = conn.prepareStatement(query);
	    	
	    	stmt.setString(1, uuid);
	    	stmt.executeUpdate();
	    	
	    	// return conn to cp
	    	if(forceClose) conn.close();
	    	
	    } catch(SQLException e) {
	    	
	    	Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
	    	e.printStackTrace();
	    	
	    }
	    
	    return new FarmingPerks(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		
	}
	
	public boolean checkIfPlayerHasFarmingPerks(Connection conn, String uuid, boolean forceClose) {
		
		String query = "SELECT * FROM farming_perks WHERE owner_uuid =?";
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			
			return rs.next();
			
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		}
		
		return false;
		
	}
	
	public FarmingPerks getFarmingPerks(Connection conn, String uuid, boolean forceClose) {
		
		String query = "SELECT * FROM farming_perks WHERE owner_uuid = ?";
		
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(query);
			
			stmt.setString(1, uuid);
			
		
			
			ResultSet rs = stmt.executeQuery();
			
            if (rs.next()) {
                return new FarmingPerks(
                    rs.getInt("perk_1"), rs.getInt("perk_1_opt"), rs.getInt("perk_1_opt_2"), rs.getInt("perk_1_selected"),
                    rs.getInt("perk_2"), rs.getInt("perk_2_opt"), rs.getInt("perk_2_opt_2"), rs.getInt("perk_2_selected"),
                    rs.getInt("perk_3"), rs.getInt("perk_3_opt"), rs.getInt("perk_3_opt_2"), rs.getInt("perk_3_selected"),
                rs.getInt("farming_level"));
            }
            
            if(forceClose) conn.close(); // return to cp
			
		} catch (SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
			
		}
		
		return null;
		
	}
	
	public HashMap<String, StarPlayer> getAllStarPlayers(Connection conn, Core plugin, boolean logging) {
		
		HashMap<String, StarPlayer> players = new HashMap<>();
		
		try {
				
			String sql_statement = "SELECT uuid, name, experience, starcoins, settings FROM players";
				
			PreparedStatement stmt = conn.prepareStatement(sql_statement);
				
			ResultSet rs = stmt.executeQuery();
				
			while(rs.next()) {
				String uuid = rs.getString("uuid");
				String name = rs.getString("name");
				int experience = rs.getInt("experience");
				int starcoins = rs.getInt("starcoins");
				
				JSONObject parsedSettings = StringFormatting.parseJSONString(rs.getString("settings"));
				
				StarPlayerSettings settings;
				if(parsedSettings == null) {
					settings = new StarPlayerSettings(true, new JSONObject("{\"scoreboard\": \"enabled\"}")); // default value
				} else {
					
					String scoreboardToggledParse = parsedSettings.getString("scoreboard");
					boolean scoreboardToggle = (scoreboardToggledParse.equalsIgnoreCase("enabled")) ? true : false;
					
					// set the settings value
					settings = new StarPlayerSettings(scoreboardToggle, parsedSettings);
				}
				
				FarmingPerks loadedPerks = this.getFarmingPerks(conn, uuid, false);
				StarPlayer loadedPlayer = new StarPlayer(uuid, name, experience, starcoins, settings, loadedPerks);
				
				plugin.getMemoryStore().addStarPlayer(loadedPlayer, uuid);
				
				
				if(logging) {
					Bukkit.getLogger().log(Level.INFO, GeneralLogging.VERBOSE_LOAD.getLog() + uuid);
				}

			}
			
			plugin.getCropManager().loadAllCrops(logging); // call it here as this relies on the player memory store being established
			
			// return conn to cp
			conn.close();
			
			
		} catch(SQLException e) {
				
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		}
		

		return players;
		
	}
	
	public StarPlayer getExistingPlayer(Player p, Connection conn, boolean forceClose) {
		
		StarPlayer player = null; // will return null if a player wasn't found
		String sql_statement = "SELECT * FROM players WHERE uuid = ?";
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql_statement);
			
			stmt.setString(1, p.getUniqueId().toString());
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				String uuid = rs.getString("uuid");
				String name = rs.getString("name");
				int xp = rs.getInt("experience");
				int starcoins = rs.getInt("starcoins");
				
				JSONObject parsedSettings = StringFormatting.parseJSONString(rs.getString("settings"));
				
				StarPlayerSettings settings;
				if(parsedSettings == null) {
					settings = new StarPlayerSettings(true, new JSONObject("{\"scoreboard\": \"enabled\"}")); // default value
				} else {
					
					String scoreboardToggledParse = parsedSettings.getString("scoreboard");
					boolean scoreboardToggle = (scoreboardToggledParse.equalsIgnoreCase("enabled")) ? true : false;
					
					// set the settings value
					settings = new StarPlayerSettings(scoreboardToggle, parsedSettings);
				}
				
				FarmingPerks perks = this.getFarmingPerks(conn, uuid, false);
				
				player = new StarPlayer(uuid, name, xp, starcoins, settings, perks);
			}
			
			if(forceClose) { conn.close(); } // return back to cp
			
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
		}
		
		return player;
		
	}
	

}
