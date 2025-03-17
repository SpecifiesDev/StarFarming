package me.csdad.starfarming.DataStructures.Players;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.csdad.starfarming.Errors.DatabaseLogging;

public class StarPlayer {
	
	private final String uuid;
	private String name;
	private int experience;
	private int starcoins;
	private StarPlayerSettings settings;
	
	public StarPlayer(String uuid, String name, int experience, int starcoins, StarPlayerSettings settings) {
		this.uuid = uuid;
		this.name = name;
		this.experience = experience;
		this.starcoins = starcoins;
		this.settings = settings;
	}
	
	public StarPlayerSettings getSettings() {
		return this.settings;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getExperience() {
		return experience;
	}
	
	public void setExperience(int newXP) {
		this.experience = newXP;
	}
	
	public void addExperience(int addition) {
		this.experience = this.experience + addition;
	}
	
	public void substractExperience(int subtract) {
		this.experience = this.experience - subtract;
	}
	
	public int getStarCoins() {
		return starcoins;
	}
	
	public void setStarCoins(int newSC) {
		this.starcoins = newSC;
	}
	
	public void addStarCoins(int addition) {
		this.starcoins = this.starcoins + addition;
	}
	
	public void save(Connection conn) {
		
		String sql_statement = "UPDATE players SET name = ?, experience = ?, starcoins = ?, settings = ? WHERE uuid = ?";
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql_statement);
			
			
			// add params
			stmt.setString(1, name);
			stmt.setInt(2, experience);
			stmt.setInt(3, starcoins);
			stmt.setString(4, this.settings.serialize());
			stmt.setString(5, this.uuid);
			
			stmt.executeUpdate();
			
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.toString());
			e.printStackTrace();
			
		}
		
	}
}
