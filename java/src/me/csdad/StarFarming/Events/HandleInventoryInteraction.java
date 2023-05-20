package me.csdad.StarFarming.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Experience.StarPlayer;
import me.csdad.StarFarming.Utility.MessageConstants;
import me.csdad.StarFarming.Utility.StringFormatting;

/**
 * Class used to handle the interaction between StarFarming's internal inventories
 * @author speci
 *
 */
public class HandleInventoryInteraction implements Listener {
	
	// store an instance of our main class
	private Main plugin;
	
	public HandleInventoryInteraction() {
		this.plugin = Main.getInstance();
	}
	
	// handler to just cancel interaction in levels inv
	@EventHandler
	public void handleLevels(InventoryClickEvent e) {
		if(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("levels")) {
			e.setCancelled(true);
		}
	}
	
	// handler to handle sell inventory
	@EventHandler
	public void handleSell(InventoryClickEvent e) {
		
		// if the entity clicking isn't player, return
		if(!(e.getWhoClicked() instanceof Player)) return;
		
		// if the clicked item is null, return
		if(e.getCurrentItem() == null) return;
		
		// check if we're in the proper inventory for this event
		if(!(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("Harvest Shop"))) return;
		
		// grab an object of the player
		Player p = (Player) e.getWhoClicked();
		
		// grab an object of our clicked item
		ItemStack clicked = e.getCurrentItem();
		
		// if the clicked item was a glass pane, we return
		if(clicked.getType() == Material.WHITE_STAINED_GLASS_PANE) {
			e.setCancelled(true);
		} 
		// if the player clicks on the hopper, begin our sell logic
		else if(clicked.getType() == Material.HOPPER) {
			
			// store an int of the amount of coins we've generated from this harvest
			int generatedCoin = 0;
			
			// grab an object of the clicked inventory
			Inventory inv = e.getClickedInventory();
			
			// go ahead and cancel the click event
			e.setCancelled(true);
			
			// grab an instance of our player's data
			StarPlayer player = this.plugin.getStarPlayer(p);
			
			// create an arraylist containing all unsellable items to put back into the player's inv
			ArrayList<ItemStack> noSell = new ArrayList<>();
			
			// now we're going to loop through the first 45 slots of our saved inventory
			
			for(int i = 0; i < 45; i++) {
				
				// grab an object of the item we're at
				ItemStack current = inv.getItem(i);
				
				// ensure the item null or air
				if(current != null && !(current.getType() == Material.AIR)) {
					
					// if the item doesn't have a meta, or lore value we automatically know it isn't able to be sold
					if(current.getItemMeta() == null || current.getItemMeta().getLore() == null) {
						noSell.add(current);
						inv.setItem(i, new ItemStack(Material.AIR));
					} else {
						
						// grab an object of the item's lore
						List<String> lore = current.getItemMeta().getLore();
						
						// now we're going to grab a "perceived" numeric value
						String val = ChatColor.stripColor((String) lore.get(0)).split("Value: ")[1];
						
						// check if the value grabbed is null, if it is assume this item can't be sold
						if(val == null) {
							noSell.add(current);
							inv.setItem(i, new ItemStack(Material.AIR));
						}
						// now we want to check if the grabbed value is a digit. if it isn't, this item is proper
						else if(val.chars().allMatch(Character::isDigit)) {
							
							// convert our val to an integer
							int converted = Integer.parseInt(val);
							
							// add a total based on itemstack size
							int total = current.getAmount() * converted;
							
							// add the generated coin from this stack to the final number
							generatedCoin += total;
							
							// nullify the item to prevent dupes
							inv.setItem(i, new ItemStack(Material.AIR));
							
						}
						
						
					}
					
					
					
				}
				
				
			}
			
			// check if there were items that could not be sold, if so place them back in player's inv
			if(noSell.size() > 0) {
				for(ItemStack stack : noSell) {
					p.getInventory().addItem(new ItemStack[] { stack });
				}
			}
			
			// finally we send appropriate messages for how many coins the player earned
			if(generatedCoin > 0) {
				player.addStarCoins(generatedCoin);
				
				// format it so that commana are appropriately placed
				String formattedMoney = StringFormatting.formatNumber(generatedCoin).replaceAll(",", "&8,&6");
				
				p.sendMessage(this.plugin.color("&aYou've sold your produce for &6" + formattedMoney + " &acoins&7."));
			} else {
				p.sendMessage(this.plugin.color(MessageConstants.SELL_SOME_ITEMS_NO_VALUE));
			}
			
			
		}
		
		
		 
	}

}
