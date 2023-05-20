package me.csdad.StarFarming.Experience;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.csdad.StarFarming.Main;

/**
 * This class is a general listener class to handle a player's data 
 * inside of our internal cache. Designed to prevent issues where a player
 * isn't online yet their data still persists.
 * @author speci
 *
 */
public class HandleDataCache implements Listener {
	
	// grab an instance of our main class.
	private Main plugin;
	
	public HandleDataCache() {
		this.plugin = Main.getInstance();
	}
	
	// Event to add a player's data to our cache when they join
	@EventHandler
	public void join(PlayerJoinEvent e) {
		
		// Grab the player from the event
		Player p = e.getPlayer();
		
		// add them to our cache
		this.plugin.addStarPlayer(p, new StarPlayer(p));
	}
	
	// Event to remove a player's data from our cache when they leave
	@EventHandler
	public void leave(PlayerQuitEvent e) {
		
		// Grab the player from the event
		Player p = e.getPlayer();
		
		// i may eventually add a temporary log system that will alert a
		// player if their data didn't save for whatever reason, and provide
		// an error code for admins to pinpoint what happened
		try {
			// first we try and save the data to their localized file
			this.plugin.getStarPlayer(p).save();
			
			// now we remove them from cache
			this.plugin.removeStarPlayer(p);
			
		} catch(Exception err) {
			
			// for now just throw the error out in console
			err.printStackTrace();
			
		}
		
	}
	
	// Same as the former method, just meant to blanket cover
	@EventHandler
	public void kick(PlayerKickEvent e) {
		
		Player p = e.getPlayer();
		
		try {
			
			this.plugin.getStarPlayer(p).save();
			this.plugin.removeStarPlayer(p);
			
		} catch(Exception err) {
			
			err.printStackTrace();
			
		}
		
	}

}
