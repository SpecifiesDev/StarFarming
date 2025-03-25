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
import me.csdad.starfarming.Errors.Perks.PerkDescriptions;
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
		
		
		// cast player
		Player p = (Player) e.getWhoClicked();
		
		
		if(item.getType() == Material.RED_TERRACOTTA) {
			
			// prevent this from working if player clicks a RT in their inv
			if(item.getItemMeta() == null) return;
			
			// get the clicked slot for tiers
			int slot = e.getSlot();
			
			int tier = (slot == 20) ? 1 : (slot == 22) ? 2 : (slot == 24) ? 3 : -1;
			
			// not a tier
			if(tier == -1) return;
			
			// get the tier color
			String tierColor = (tier == 1) ? "&7 " : (tier == 2) ? "&e " : "&6 ";
			
			// begin constructing a new inventory
			Inventory inv = Bukkit.createInventory(null, 45, this.plugin.color(this.plugin.getConfig().getString("logging.ingame-prefix") + " &8-" +  tierColor + "Tier " + tier + " &ePerks"));
			
			// item factory
			ItemFactory factory = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE, 1);
			factory.setDisplayName("&8&m");
			for(int i = 0; i < 45; i++) inv.setItem(i, factory.getItem());
			
			// get the item lore based on the clicked tier
			String item1Lore = (tier == 1) ? PerkDescriptions.TIER1_GREEN_THUMB.getDescription() :
                (tier == 2) ? PerkDescriptions.TIER2_DOCTOR_FARM.getDescription() :
                PerkDescriptions.TIER3_RMF.getDescription();

			String item2Lore = (tier == 1) ? PerkDescriptions.TIER1_HEIRLOOM.getDescription() :
			                (tier == 2) ? PerkDescriptions.TIER2_HANDYMAN.getDescription() :
			                PerkDescriptions.TIER3_WINTER_SOLDIER.getDescription();
			
			String item3Lore = (tier == 1) ? PerkDescriptions.TIER1_EFFICIENT_FARMING.getDescription() :
			                (tier == 2) ? PerkDescriptions.TIER2_POPOP.getDescription() :
			                PerkDescriptions.TIER3_INSIDER_TRADING.getDescription();
			
			// store in an array for nested looping
			String[] itemLores = {item1Lore, item2Lore, item3Lore};
			
			// and the name
			String item1Name = (tier == 1) ? PerkDescriptions.TIER1_GREEN_THUMB.getTitle() :
                (tier == 2) ? PerkDescriptions.TIER2_DOCTOR_FARM.getTitle() :
                PerkDescriptions.TIER3_RMF.getTitle();
			
			String item2Name = (tier == 1) ? PerkDescriptions.TIER1_HEIRLOOM.getTitle() :
			                (tier == 2) ? PerkDescriptions.TIER2_HANDYMAN.getTitle() :
			                PerkDescriptions.TIER3_WINTER_SOLDIER.getTitle();
			
			String item3Name = (tier == 1) ? PerkDescriptions.TIER1_EFFICIENT_FARMING.getTitle() :
			                (tier == 2) ? PerkDescriptions.TIER2_POPOP.getTitle() :
			                PerkDescriptions.TIER3_INSIDER_TRADING.getTitle();
			
			// same thing
			String[] itemNames = {item1Name, item2Name, item3Name};
			
			// slots for the items
			int[] slots = {20, 22, 24};
			// loop, flush, set, and put in inventory
			
			for(int i = 0; i < 3; i++) {
				factory.flush(Material.BAMBOO_SIGN, 1);
				factory.setDisplayName(itemNames[i]);
				factory.setLore(itemLores[i]);
				inv.setItem(slots[i], factory.getItem());
			}
			
			factory.flush(Material.ARROW, 1);
			
			factory.setDisplayName("&4&lBack");
			
			inv.setItem(40, factory.getItem());
			
			p.openInventory(inv);
		}
		
	}

}
