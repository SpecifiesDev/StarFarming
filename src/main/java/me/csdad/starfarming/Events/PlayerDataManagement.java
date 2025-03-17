package me.csdad.starfarming.Events;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.DataStructures.Players.StarPlayerSettings;
import me.csdad.starfarming.Databasing.PlayerManager;
import me.csdad.starfarming.Errors.DatabaseLogging;

public class PlayerDataManagement implements Listener {
	
	
	private Core plugin;
	
	public PlayerDataManagement() {
		this.plugin = Core.getInstance();
	}
	
	
	// handle adding player to mem store on join
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		Connection conn;
		
		try {
			
			// retrieve a new conn from the pool
			conn = this.plugin.getNewConnection();
			
			
			// create a new player management object to handle player database functions
			PlayerManager manager = new PlayerManager();
			
			boolean isOldPlayer = manager.checkIfExists(e.getPlayer(), conn, false);
			
			// extract data
			Player p = e.getPlayer();
			String uuid = p.getUniqueId().toString();
			String name = p.getName();
			
			if(!isOldPlayer) {
				
				manager.addNewPlayer(p, conn, false);
				
				StarPlayer player = new StarPlayer(uuid, name, 0, 0, new StarPlayerSettings(true, new JSONObject("{\"scoreboard\": \"enabled\"}")));
				
				plugin.getMemoryStore().addStarPlayer(player, uuid); // no need to check because if the player is new there's no feasible way for them to be in the memorystore already
				
			} else {
				
				// we'll add a catch functionality just in case for whatever reason the player wasn't rendered appropriately.
				// in general, this will never be executed as we load ALL players into the memory store on startup, but just in case.
				if(!this.plugin.getMemoryStore().getAllStarPlayers().containsKey(p.getUniqueId().toString())) {
					
					StarPlayer player = manager.getExistingPlayer(e.getPlayer(), conn, false);
					
					// let's just run a check on the player's name to update the internal name in our database if player has changed name
					// this will get used in any active memory storage usage, as well as get internally re-stored later on
					if(!name.equalsIgnoreCase(player.getName())) {
						player.setName(e.getPlayer().getName());
					}
					
					plugin.getMemoryStore().addStarPlayer(player, uuid);
					
				}
				
				
				
			}
			
			
			conn.close();
			
		} catch(SQLException err) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			err.printStackTrace();
			
		}
		
		
		
		
	}
	
	
	// handle all deletions of player from memorystore
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
		
		handlePlayerLeave(e.getPlayer());
		
		
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		
		handlePlayerLeave(e.getPlayer());
		
	}
	
	
	
	// helper for handling leave event
	private void handlePlayerLeave(Player p) {
		
		try {
			
			Connection conn = plugin.getNewConnection();
			
			StarPlayer player = plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
			
			player.save(conn);
			
			plugin.getMemoryStore().removeStarPlayer(p.getUniqueId().toString());
			
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		}
		
	}

}
