package me.csdad.StarFarming.Events;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Crops.Crop;

/**
 * This event is designed to facilitate the obtainment of actual crop seeds
 * @author speci
 *
 */
public class ProvideSeeds implements Listener {
	
	// store an instance of our main class
	private Main plugin;
	
	public ProvideSeeds() {
		this.plugin = Main.getInstance();
	}
	
	
	
	@EventHandler
	public void breakEvent(BlockBreakEvent e) {
		
		// grab our block and player
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		// check if the type is grass
		if(b.getType() == Material.GRASS) {
			
			// create a new random generator
			Random generator = new Random();
			
			// grab a list of all of our registered crops;
			HashMap<String, Crop> loadedCrops = this.plugin.getRegisteredCrops();
			
			// covert our crops to an array
			Object[] pool = loadedCrops.values().toArray();
			
			// select a random crop from our pool
			Object randomValue = pool[generator.nextInt(pool.length)];
			
			// just add a check to ensure the value is a crop
			if(!(randomValue instanceof Crop)) return;
			
			// convert our object to a crop
			Crop crop = (Crop) randomValue;
			
			// get the drop chance of the crop's seed
			double chance = crop.getDropChance();
			
			// generate a random double
			double randomDouble = generator.nextDouble();
			
			// if the number is <= chance, give the player a seed 
			if(randomDouble <= chance) {
				p.getInventory().addItem(new ItemStack[] { crop.getSeed() });
			}
			
			
		}
		
	}
	

}
