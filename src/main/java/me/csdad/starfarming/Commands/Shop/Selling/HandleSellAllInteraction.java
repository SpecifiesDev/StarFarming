package me.csdad.starfarming.Commands.Shop.Selling;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.CommandReturn;
import me.csdad.starfarming.Utility.StringFormatting;
import net.md_5.bungee.api.ChatColor;

public class HandleSellAllInteraction implements Listener {
	
	private Core plugin;
	public HandleSellAllInteraction() {
		this.plugin = Core.getInstance();
	}
	
	@EventHandler
	public void sell(InventoryClickEvent e) {
		
		// make sure the entity is a player
		if(!(e.getWhoClicked() instanceof Player)) return;
		
		// make sure the clicked item is valid
		if(e.getCurrentItem() == null) return;
		
		// make sure we're in the proper inventory
		if(!(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("harvest shop"))) return;
		
		// grab the necessary info from the event
		Player p = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		
		// no action
		if(clicked.getType() == Material.WHITE_STAINED_GLASS_PANE) {
			e.setCancelled(true);
			return;
		}
		
		// sell
		if(clicked.getType() == Material.HOPPER) {
			
			// coins generated this harvest
			int generatedCoins = 0;
			
			// clicked inv
			Inventory inv = e.getClickedInventory();
			
			// cancel the event
			e.setCancelled(true);
			
			// grab our star player
			StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());	
			
			// null check
			if(player == null) return;
			
			// list of unsellable items
			ArrayList<ItemStack> unableToSell = new ArrayList<>();
			
			// loop over sellable slots
			for(int i = 0; i < 45; i++) {
				
				// current object
				ItemStack current = inv.getItem(i);
				
				if(current == null || current.getType() == Material.AIR) continue; // nothing here
				
				// if the item has no meta, or lore, its not sellable
				if(current.getItemMeta() == null || current.getItemMeta().getLore() == null) {
					unableToSell.add(current);
					inv.setItem(i, new ItemStack(Material.AIR)); // remove the item to prevent dupes
					continue;
				}
				
				// grab the objects lore
				List<String> lore = current.getItemMeta().getLore();
				
				// attempt to grab the value from the lore
				String val = ChatColor.stripColor((String) lore.get(0)).split("Value: ")[1];
				
				// null check
				if(val == null || val.isEmpty()) {
					unableToSell.add(current);
					current.setType(Material.AIR);
					continue;
				}
				
				// could've used this in SetExperience & GiveSeeds, but its already done so
				if(!(val.chars().allMatch(Character::isDigit))) {
					
					unableToSell.add(current);
					inv.setItem(i, new ItemStack(Material.AIR));
					continue; // will catch items that contain lore but isnt an integer
					
				}
				
				// convert the value into a parsed integer
				int value = Integer.parseInt(val);
				
				// get the total value based on itemstack size
				int price = current.getAmount() * value;
				
				generatedCoins += price;
				
				// nullify the item
				inv.setItem(i, new ItemStack(Material.AIR));
				
			}
			
			// if there were items not able to sell, let's go through and add back
			if(unableToSell.size() > 0) {
				unableToSell
				.forEach(item -> p.getInventory().addItem(new ItemStack[] {item}));
			}
			
			// close inv
			p.closeInventory();
			
			// finally alert the player.
			if(generatedCoins > 0) {
				
				player.addStarCoins(generatedCoins);
				
				String formattedMoney = StringFormatting.formatNumber(generatedCoins).replaceAll(",", "&7,&6");
				
				p.sendMessage(this.plugin.color(CommandReturn.SELLALL_SUCCESS.format("%coins%", formattedMoney)));
				
				return;
			}
			
			// alert them if nothing was sellable
			p.sendMessage(this.plugin.color(CommandReturn.SELLALL_FAIL.getMessage()));
		}
	}
	
	@EventHandler
	public void handleItemsExisting(InventoryCloseEvent e) {
		
		if(!(e.getPlayer() instanceof Player)) return;
		
		Player p = (Player) e.getPlayer();
		
		if(!(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("harvest shop"))) return;
		
		
		Inventory inv = e.getInventory();
		
		ArrayList<ItemStack> returnToInv = new ArrayList<>();
		
		// get all of the items
		for(int i = 0; i < 45; i++) {
			// current object
			ItemStack current = inv.getItem(i);
			
			if(current == null || current.getType() == Material.AIR) continue;
			
			returnToInv.add(current);
		}
		
		returnToInv
		.forEach(item -> p.getInventory().addItem(new ItemStack[] {item}));
		
	}

}
