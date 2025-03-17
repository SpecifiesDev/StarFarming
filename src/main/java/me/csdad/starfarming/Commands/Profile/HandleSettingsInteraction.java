package me.csdad.starfarming.Commands.Profile;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Utility.ItemFactory;
import net.md_5.bungee.api.ChatColor;

public class HandleSettingsInteraction implements Listener {
	
	private Core plugin;
	public HandleSettingsInteraction() {
		this.plugin = Core.getInstance();
	}
	
	@EventHandler
	public void settings(InventoryClickEvent e) {
		
		// has to be a player
		if(!(e.getWhoClicked() instanceof Player)) return;
		
		// make sure this is the profile inventory
		if(!(ChatColor.stripColor(e.getView().getTitle()).toLowerCase().contains("settings"))) return;
		
		// if the item was null
		if(e.getCurrentItem() == null) {
			e.setCancelled(true);
			return;
		}
		
		// if inventory is null
		if(e.getView().getTopInventory() == null) {
			e.setCancelled(true);
			return;
		}
		
		
		// get and cast our necessary values
		Player p = (Player) e.getWhoClicked();
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
		
		Inventory inv = e.getView().getTopInventory();
		
		ItemStack current = e.getCurrentItem();
		
		// null check
		if(player == null) {
			e.setCancelled(true);
			return; // we have to do this or else players oculd take items out.
		}
		
		
		// scoreboard setting
		if(e.getSlot() == 10) {
			
			boolean newSetting = !player.getSettings().getScoreboardToggled();
			
			ItemFactory editItem = new ItemFactory(current);
			
			// scoreboardtoggle, using a ternary exp for shorthand
			editItem.setType((newSetting) ? Material.LIME_DYE : Material.GRAY_DYE);
			
			// same thing for the title
			editItem.setDisplayName("&bScoreboard&8: " + ((newSetting) ? "&a&lEnabled" : "&c&lDisabled"));
			
			// set the actual setting
			player.getSettings().setScoreboardToggled(newSetting);
			
			// play some sound
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
			
			// update the item
			inv.setItem(10, editItem.getItem());
			

			
		}
		
		e.setCancelled(true);
		
	}
	
}
