package me.csdad.StarFarming.Events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Crops.Crop;
import me.csdad.StarFarming.Crops.PlantedCrop;
import me.csdad.StarFarming.Utility.MessageConstants;

/**
 * Listener designed to facilitate the player's planting of custom crops.
 * @author speci
 *
 */
public class PlayerPlantEvent implements Listener {
	
	// store an instance of our main class
	private Main plugin;
	
	public PlayerPlantEvent() {
		this.plugin = Main.getInstance();
	}
	
	@EventHandler
	public void plant(BlockPlaceEvent e) {
		
		// grab an instance of our player and block
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		// Grab the block under the planted crop
		Block under = e.getBlockAgainst();
		
		// grab an instance of the crop being planted
		Crop planting = this.plugin.getRegisteredCrops().get(ChatColor.stripColor(e.getItemInHand().getItemMeta().getDisplayName()));
		
		// if the crop is registered, skip
		if(planting == null) return;
		
		// if the crop trying to be planted isn't an ageable in spigot logic, return
		if(!(b.getBlockData() instanceof org.bukkit.block.data.Ageable)) return;
		
		// if the crop can't grow in the current season, return
		if(!planting.getGrowableSeasons().contains(this.plugin.getSeason())) {
			p.sendMessage(this.plugin.color(MessageConstants.PLANTED_CROP_WRONG_SEASON));
			e.setCancelled(true);
			return;
		}
		
		// ensure that the planted crop has water surrounding it directly.
	    if (under.getRelative(BlockFace.EAST).getType() != Material.WATER && under.getRelative(BlockFace.WEST).getType() != Material.WATER && under.getRelative(BlockFace.NORTH).getType() != Material.WATER && under.getRelative(BlockFace.SOUTH).getType() != Material.WATER) {
	        p.sendMessage(this.plugin.color(MessageConstants.PLANTED_CROP_NO_WATER));
	        e.setCancelled(true);
	        return;
	    }
	    
	    // every check is passed, put the planted crop in memory
	    PlantedCrop planted = new PlantedCrop(p, b, planting, under, System.currentTimeMillis());
	    this.plugin.getPlantedCrops().put(b, planted);
		
		
		
		
	}

}
