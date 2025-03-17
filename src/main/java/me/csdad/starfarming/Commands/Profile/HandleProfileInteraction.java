package me.csdad.starfarming.Commands.Profile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

public class HandleProfileInteraction implements Listener {
	
	private Core plugin;
	
	public HandleProfileInteraction() {
		
		this.plugin = Core.getInstance();
		
	}
	
	
	@EventHandler
	public void profile(InventoryClickEvent e) {
		
		// has to be a player
		if(!(e.getWhoClicked() instanceof Player)) return;
		
		// make sure this is the profile inventory
		if(!(ChatColor.stripColor(e.getView().getTitle()).toLowerCase().contains("profile"))) return;
		
		// if the item was null
		if(e.getCurrentItem() == null) {
			e.setCancelled(true);
			return;
		}
		
	
		
		// get and cast our necessary values
		Player p = (Player) e.getWhoClicked();
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
		
		ItemStack current = e.getCurrentItem();
		
		// null check
		if(player == null) {
			e.setCancelled(true);
			return; // we have to do this or else players oculd take items out.
		}
		
		if(current.getType() == Material.COMPARATOR) {
			
			String prefix = this.plugin.getConfig().getString("logging.ingame-prefix");
			// open a settings inventory
			Inventory settingsInventory = Bukkit.createInventory(p, 45, this.plugin.color(prefix + " &8- &cSettings &8- &e" + p.getName()));
			
			
			ItemFactory factory = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE, 1);
			factory.setDisplayName("&8&m");
			
			for(int i = 0; i < 45; i++) settingsInventory.setItem(i, factory.getItem());
			
			// now we populate any settings
			
			boolean scoreboardToggle = player.getSettings().getScoreboardToggled();
			
			// scoreboardtoggle, using a ternary exp for shorthand
			factory.setType((scoreboardToggle) ? Material.LIME_DYE : Material.GRAY_DYE);
			
			// same thing for the title
			factory.setDisplayName("&bScoreboard&8: " + ((scoreboardToggle) ? "&a&lEnabled" : "&c&lDisabled"));
			
			// add an information lore
			factory.setLore("&7This setting toggles the in-game scoreboard&8.");
			
			// populate this setting
			settingsInventory.setItem(10, factory.getItem());
			
			p.openInventory(settingsInventory);
			
		}
		
		e.setCancelled(true);
		
		
	}
	

	
	

}
