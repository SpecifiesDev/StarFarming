package me.csdad.starfarming.Events.CropEvents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.StarCrop;

/**
 * This event allows for players to break certain blocks in the world in order to retrieve seeds.
 */
public class ProvideSeeds implements Listener {
	
	// store an instance of the main class
	private Core plugin;
	
	private List<Material> allowedMats; 
	
	public ProvideSeeds() {
		
		this.plugin = Core.getInstance();
		
		this.allowedMats = Arrays.asList(new Material[] {Material.TALL_GRASS, Material.SHORT_GRASS});
		
	}
	
	@EventHandler
	public void breakEvent(BlockBreakEvent e) {
		
		// grab the broken block and player
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		// check if the block is in the allowed mats list
		if(this.allowedMats.contains(b.getType())) {
			
			// create a new random generator
			Random generator = new Random();
			
			// to add another element of randomness, let's generate a number from 0-1 and only continue if it is greater than .5
			if(generator.nextDouble() < .5) return;
			
			// grab all crops from registry, convert it to a list
			HashMap<String, StarCrop> registry = this.plugin.getMemoryStore().getCrops();
			Object[] pool = registry.values().toArray();
			
			// select a random crop from the pool
			Object randomValue = pool[generator.nextInt(pool.length)];
			
			// add an extra check to ensure we're not trying to perform functions on an item that isnt a crop
			if(!(randomValue instanceof StarCrop)) return;
			
			// convert
			StarCrop crop = (StarCrop) randomValue;
			
			// get the drop chance of the crop's seed
			double chance = crop.getDropChance();
			
			// generate a random double
			double randomDouble = generator.nextDouble();
			
			// if the number is <= chance give the player the seed
			if(randomDouble <= chance) {
				p.getInventory().addItem(new ItemStack[] { crop.getSeed() });
			}
			
		}
		
	}

}
