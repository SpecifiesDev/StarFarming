package me.csdad.StarFarming.Events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Crops.PlantedCrop;
import me.csdad.StarFarming.Experience.StarPlayer;
import me.csdad.StarFarming.Utility.MessageConstants;

/**
 * Listener class that handles logic when a player attempts to harvest
 * a custom crop.
 * @author speci
 *
 */
public class PlayerHarvestEvent implements Listener {
	
	// store an instance of our main class
	private Main plugin;
	
	public PlayerHarvestEvent() {
		this.plugin = Main.getInstance();
	}
	
	// this is the main harvest event handler
	@EventHandler
	public void harvest(BlockBreakEvent e) {
		
		// first let's grab an instance of the player and the block broken
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		// now we check if the broken block is a crop inside of our cache
		if(this.plugin.getPlantedCrops().containsKey(b)) {
			
			// if it's cached, grab the cached crop
			PlantedCrop crop = this.plugin.getPlantedCrops().get(b);
			
			// check if the player harvesting owns the crop
			// if the do not, alert them and return
			if(p != crop.getOwner()) {
				p.sendMessage(this.plugin.color(MessageConstants.PLANTED_CROP_NO_OWNERSHIP));
				e.setCancelled(true);
				return;
			}
			
			// now check if the crop is harvestable, if not alert and return
			if(!crop.canHarvest()) {
				p.sendMessage(this.plugin.color(MessageConstants.PLANTED_CROP_STILL_GROWING));
				e.setCancelled(true);
				return;
			}
			
			// generate a random amount of produce
			int randomProduce = getProduceAmount(crop.getCrop().getSpread(), crop.getCrop().getBase());
			
			// here's where things get a little trick. For some reason, the only way to do this
			// is to create a "faux" itemstack and loop over the player's inventory in a page like manner
			// especially if the harvest is a high amount.
			
			// so first, we create a faux product item AND a seed
			ItemStack product = crop.getCrop().getProduct(1);
			ItemStack seed = crop.getCrop().getSeed();
			
			// now with seeds, we can simply add an arrayed item stack to their inv.
			p.getInventory().addItem(new ItemStack[] { seed });
			
			// now we're essentially looping over slots and attempting to find existing stacks of our produce
			// this is to prevent a weird bug where a generated produce stack will fill 1x1 instead as intended
			// so, if I have 257 harvested product, this loop will apply 4 stacks and leave a remainder of 1.
			// if ever needed we can easily modulo this based on stack size.
			for(int i = 0; i < p.getInventory().getSize(); i++) {
				
				// we essentially try and detect if there's already a produce item in the inventory
				// if not the algorithm will just apply it to the closest applicable slot
				ItemStack current = p.getInventory().getItem(i);
				
				if(current != null && current.getType() != Material.AIR && current.getItemMeta() != null) {
					
					// let's strip our product of color for comparators
					// side note(placed into testing notes) but we should test this against
					// interactions with vanilla names. it may be where we have to compare NBT data
					// or some other identifier that can't be modified in normal mc interactions
		            String rawNameAt = ChatColor.stripColor(current.getItemMeta().getDisplayName());
		            String rawProductName = ChatColor.stripColor(product.getItemMeta().getDisplayName());
					
					// check if the item we're at is equal to the our produce
		            if(rawNameAt.equalsIgnoreCase(rawProductName)) {
		            	
		            	// let's calculate how much we would have if we added our harvested produce to the exisitng stack
		            	int amountAfter = current.getAmount() + randomProduce;
		            	
		            	// now if our amount after would be les than 64 we can simply edit the stacks value and reapply
		            	// it to our inventory
		            	if(amountAfter < 64) {
		            		current.setAmount(amountAfter);
		            		p.getInventory().setItem(i, current);
		            		
		            		// now we zero out the produce
		            		randomProduce = 0;
		            	} 
		            	// if the amount after is greater we create a loop in
		            	// which we determine how much we need to actually place
		            	else {
		            		
		            		int additive = 0;
		            		
		            		for(int j = current.getAmount(); j <= 64;) {
		            			
		            			additive++;
		            			j++;
		            			
		            		}
		            		
		            		// now we add the calculated number to the new stack
		            		current.setAmount(current.getAmount() + additive);
		            		
		            		p.getInventory().setItem(i, current);
		            		
		            		// remove the product added from our overall produce
		            		randomProduce -= additive;
		            		
		            	}
		            	
		            }
		            
		            // if there's no produce left to add, break the loop
		            if(randomProduce <= 0) {
		            	break;
		            }
		            
				}
				
			}
			
			// if there's a remainder (as long as there's an empty slot) add it to the player's inv.
			if(randomProduce > 0 && !(p.getInventory().firstEmpty() == -1)) {
				product.setAmount(randomProduce);
				p.getInventory().addItem(new ItemStack[] { product });
			}
			
			// now we give the player the configured xp they're supposed to get for harvest crops
			int xp = crop.getCrop().getXp();
			StarPlayer player = this.plugin.getStarPlayer(p);
			player.addFarming(xp);
			
			// finally harvest the crop
			crop.harvestCrop();
			
		}
		
		
	}
	
	/**
	 * This method was designed to prevent players from breaking the field of planted crops
	 * It was mainly intended to fix issues with holographic displays persisting even though
	 * the field was broken. I think it's still a nice feature, so will keep it.
	 */
	@EventHandler
	public void checkIfField(BlockBreakEvent e) {
		
		// grab the block above the broken block
		Block target = e.getBlock().getRelative(BlockFace.UP);
		
		// if it's a planted block, prevent this event
		if(this.plugin.getPlantedCrops().containsKey(target)) {
			e.setCancelled(true);
		}
		
	}
	
	/**
	 * These next few listeners are designed to prevent environment destruction of planted crops.
	 */
	
	public void preventBlockFromTo(BlockFromToEvent e) {
		
		// if the block is lava or water
		if(e.getBlock().getType() == Material.WATER || e.getBlock().getType() == Material.LAVA) {
			
			// check if the block is a planted crop, if it is cancel event
			if(this.plugin.getPlantedCrops().containsKey(e.getToBlock())) {
				e.setCancelled(true);
			}
			
		}
		
	}
	
	// these listeners prevent fading & trampling
	
	public void pereventTrample(PlayerInteractEvent e) {
		
		// check if the action was physical
		if(e.getAction() == Action.PHYSICAL) {
			
			// grab the block interacted with
			Block b = e.getClickedBlock();
			
			// if the block is null, return
			if(b == null) return;
			
			// if the block is farmland, cancel
			if(b.getType() == Material.FARMLAND) {
				e.setCancelled(true);
			}
			
			
		}
	}
	
	public void preventFade(BlockFadeEvent e) {
		
		if(e.getNewState().getBlock().getType() == Material.DIRT) {
			e.setCancelled(true);
		}
		
	}
	
	
	/**
	 * Local random number generator to determine produce yield
	 * @param spread spread of the produce
	 * @param base base harvest amount
	 * @return a random amount of produce
	 */
	private int getProduceAmount(int spread, int base) {
		int yield = (int) (Math.random() * (spread - base) + base);
		return yield;
	}
	

}
