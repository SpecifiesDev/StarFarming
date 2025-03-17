package me.csdad.starfarming.Events.CropEvents;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.PlantedCrop;
import me.csdad.starfarming.DataStructures.StarCrop;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.PlayerMessaging;
import net.md_5.bungee.api.ChatColor;

public class PlayerPlantEvent implements Listener {
	
	// instance of our main class
	private Core plugin;
	
	public PlayerPlantEvent() { this.plugin = Core.getInstance(); }
	
	
	@EventHandler
	public void plantEvent(BlockPlaceEvent e) {
		
		// grab our player and placed block
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		// let's just check if this block is already planted. to prevent any weird issues
		if(this.plugin.getMemoryStore().getPlantedCrop(b) != null) return;
		
		// grab the block (field) under the actual block
		Block under = e.getBlockAgainst();
		
		// now let's try and get our Crop object against the item that was planted with
		
		// first, let's grab the actual item
		ItemStack plantedHand = e.getItemInHand();
		
		// the key to query the memory store with
		String key = ChatColor.stripColor(plantedHand.getItemMeta().getDisplayName()).toLowerCase();
		
		
		StarCrop retrievedCrop = this.plugin.getMemoryStore().getCrop(key);
		
		// if the retrieved crop is null, return to prevent issues
		if(retrievedCrop == null) return;
		
		
		// due to the logic of our planting system, the crop HAS to be a spigot Ageable.
		boolean isAgeable = b.getBlockData() instanceof org.bukkit.block.data.Ageable;
		if(!isAgeable) return;

		
		// if the crop isn't able to grow inside of the current season, prevent the planting
		if(!retrievedCrop.getGrowableSeasons().contains(this.plugin.getMemoryStore().getSeason())) {
			p.sendMessage(this.plugin.color(PlayerMessaging.WRONG_SEASON.getMessage()));
			e.setCancelled(true);
			return;
		}
		
		
		// ensure that the planted crop has water next to it.
		if(under.getRelative(BlockFace.EAST).getType() != Material.WATER && under.getRelative(BlockFace.WEST).getType() != Material.WATER && under.getRelative(BlockFace.NORTH).getType() != Material.WATER && under.getRelative(BlockFace.SOUTH).getType() != Material.WATER) {
			p.sendMessage(this.plugin.color(PlayerMessaging.PLANTED_CROP_NO_WATER.getMessage()));
			e.setCancelled(true);
			return;
		}
		
		// retrieve the star player from memory
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
		
		// one last null check to prevent errors down the road
		if(player == null) return;
		
		
		// every check passed. block place will go through, now place the new crop into memory
		PlantedCrop planted = new PlantedCrop(player, b, retrievedCrop, under, System.currentTimeMillis());
		this.plugin.getMemoryStore().addPlantedCrop(b, planted);
	}

}
