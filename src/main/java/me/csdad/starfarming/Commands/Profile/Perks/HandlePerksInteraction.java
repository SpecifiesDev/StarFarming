package me.csdad.starfarming.Commands.Profile.Perks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.Utility.ItemFactory;
import net.md_5.bungee.api.ChatColor;

public class HandlePerksInteraction implements Listener {
	
	private Core plugin;
	
	
	public HandlePerksInteraction() {
		this.plugin = Core.getInstance();
	}
	
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if(!(e.getWhoClicked() instanceof Player)) return;
		
		// must be in the perks inventory
		if(!(ChatColor.stripColor(e.getView().getTitle())).toLowerCase().contains("farming perks")) return;
		
		if(e.getCurrentItem() == null) return;
		
		// get the item stack
		ItemStack item = e.getCurrentItem();
		
		
		
		if(item.getType() == Material.RED_TERRACOTTA) {
			
			// prevent this from working if player clicks a RT in their inv
			if(item.getItemMeta() == null) return;
			
			// get the clicked slot for tiers
			int slot = e.getSlot();
			
			int tier = (slot == 20) ? 1 : (slot == 22) ? 2 : (slot == 24) ? 3 : -1;
			
			// not a tier
			if(tier == -1) return;
			
			// get the tier color
			String tierColor = (tier == 1) ? " &7 " : (tier == 2) ? " &e " : " &6 ";
			
			// begin constructing a new inventory
			Inventory inv = Bukkit.createInventory(null, 45, this.plugin.color(this.plugin.getConfig().getString("logging.ingame-prefix") + " &8-" + tierColor + tier + " &ePerk Selection"));
			
			// item factory
			ItemFactory factory = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE, 1);
			factory.setDisplayName("&8&m");
			
			
			
		}
		
	}

}
