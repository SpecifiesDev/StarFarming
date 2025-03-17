package me.csdad.starfarming.Commands.Experience;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.md_5.bungee.api.ChatColor;

public class HandleLevelInteraction implements Listener {

	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if(e.getView() == null) return;
		
		if(ChatColor.stripColor(e.getView().getTitle()).toLowerCase().contains("level")) {
			e.setCancelled(true); // cancel if they are in the level inventory
		}
		
	}

}
