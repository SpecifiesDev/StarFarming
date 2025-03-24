package me.csdad.starfarming.Commands.Profile.Perks;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.CommandReturn;
import me.csdad.starfarming.Errors.Permissions;
import me.csdad.starfarming.Errors.Perks.PerkDescriptions;
import me.csdad.starfarming.Utility.ItemFactory;

public class PerksCommand implements CommandExecutor {
	
	// instance of our main class
	private Core plugin;
	
	public PerksCommand() {
		this.plugin = Core.getInstance();
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// check if sender is player
		if(!(sender instanceof Player)) {
			sender.sendMessage(this.plugin.color(CommandReturn.SENDER_CONSOLE.getMessage()));
			return true;
		}
		
		// cast
		Player p = (Player) sender;
		
		// validate perms
		if(!(p.hasPermission(Permissions.PERKS.getPermission()))) {
			p.sendMessage(this.plugin.color(CommandReturn.NO_PERMISSION.getMessage()));
			return true;
		}
		
		// get a starplayer from the sender
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
		
		// null check
		if(player == null) {
			p.sendMessage(this.plugin.color(CommandReturn.STARPLAYER_DNE.getMessage()));
			return true;
		}
		
		// validate args
		if(!(args.length == 1)) {
			p.sendMessage(this.plugin.color(CommandReturn.PERKS_INVALID_USAGE.getMessage()));
			return true;
		}
		
		// get arg
		String type = args[0].toLowerCase();
		
		// valid types
		List<String> validPerks = Arrays.asList("farming");
		
		// validate type is acceptable
		if(!(validPerks.contains(type))) {
			p.sendMessage(this.plugin.color(CommandReturn.PERKS_INVALID_TYPE.getMessage()));
			return true;
		}
		
		// everything is true, let's begin constructing perk inventory perk by perk
		
		handlePerkCreation(type, p, player);
		
		return true;
	}
	
	/**
	 * Method to get the perk string for a tier
	 * @param tier The tier
	 * @param selectedIndex The selected tier that the player has selected
	 * @return Raw string
	 */
	private String getPerkStringForTier(int tier, int selectedIndex) {
		
	    switch (tier) {
        case 1:
            return selectedIndex == 1 ? PerkDescriptions.TIER1_GREEN_THUMB.getDescription()
                 : selectedIndex == 2 ? PerkDescriptions.TIER1_HEIRLOOM.getDescription()
                 : selectedIndex == 3 ? PerkDescriptions.TIER1_EFFICIENT_FARMING .getDescription()
                 : "ERR_{invalid_desc}";
        case 2:
            return selectedIndex == 1 ? PerkDescriptions.TIER2_DOCTOR_FARM.getDescription()
                 : selectedIndex == 2 ? PerkDescriptions.TIER2_HANDYMAN.getDescription()
                 : selectedIndex == 3 ? PerkDescriptions.TIER2_POPOP.getDescription()
                 : "ERR_{invalid_desc}";
        case 3:
            return selectedIndex == 1 ? PerkDescriptions.TIER3_RMF.getDescription()
                 : selectedIndex == 2 ? PerkDescriptions.TIER3_WINTER_SOLDIER.getDescription()
                 : selectedIndex == 3 ? PerkDescriptions.TIER3_INSIDER_TRADING.getDescription()
                 : "ERR_{invalid_desc}";
        default:
            return "ERR_{invalid_desc}";
    }
		
	}
	private void handlePerkCreation(String type, Player p, StarPlayer player) {
		
		Inventory inv = Bukkit.createInventory(null, 45, this.plugin.color(this.plugin.getConfig().getString("logging.ingame-prefix") + " &8- &2Farming Perks"));
		
		ItemFactory factory = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE, 1);
		factory.setDisplayName("&8&m");
		for(int i = 0; i < 45; i++) inv.setItem(i, factory.getItem());
		
		switch(type) {
			case "farming": {
				
				// level for further logic
				int level = player.getFarmingPerks().getFarmingLevel();
				
				// farming specific perks
				boolean hasSelectedPerk1 = player.getFarmingPerks().getPerkSelected(1);
				boolean hasSelectedPerk2 = player.getFarmingPerks().getPerkSelected(2);
				boolean hasSelectedPerk3 = player.getFarmingPerks().getPerkSelected(3);
				
				
				
				// now let's determine if player has met the level perks
				boolean hasLevelPerk1 = level >= 3;
				boolean hasLevelPerk2 = level >= 5;
				boolean hasLevelPerk3 = level == 10;
				
				// now with all of our values we can establish the 3 materials for each item
				Material material1 = !hasLevelPerk1 ? Material.BEDROCK : (hasSelectedPerk1 ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA);
				Material material2 = !hasLevelPerk2 ? Material.BEDROCK : (hasSelectedPerk2 ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA);
				Material material3 = !hasLevelPerk3 ? Material.BEDROCK : (hasSelectedPerk3 ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA);
				
				// now we can parse display names.
				String displayName1 = material1 == Material.BEDROCK ? "&c&lNot Unlocked" : (material1 == Material.GREEN_TERRACOTTA ? "&a&lPerk Selected" : "&4&lPerk Not Selected");
				String displayName2 = material2 == Material.BEDROCK ? "&c&lNot Unlocked" : (material2 == Material.GREEN_TERRACOTTA ? "&a&lPerk Selected" : "&4&lPerk Not Selected");
				String displayName3 = material3 == Material.BEDROCK ? "&c&lNot Unlocked" : (material3 == Material.GREEN_TERRACOTTA ? "&a&lPerk Selected" : "&4&lPerk Not Selected");
				
				String lore1 = material1 == Material.BEDROCK
				        ? "&7You will unlock this tier at level &63"   // Tier 1 lore
				        : (material1 == Material.RED_TERRACOTTA
				            ? "&7Click this to select from a list of perks for this tier."
				            : getPerkStringForTier(1, player.getFarmingPerks().getSelectedPerk(1)));
				
				

				String lore2 = material2 == Material.BEDROCK
				        ? "&7You will unlock this tier at level &65"   // Tier 2 lore
				        : (material2 == Material.RED_TERRACOTTA
				            ? "&7Click this to select from a list of perks for this tier."
				            : getPerkStringForTier(2, player.getFarmingPerks().getSelectedPerk(2)));

				String lore3 = material3 == Material.BEDROCK
				        ? "&7You will unlock this tier at level &610"  // Tier 3 lore
				        : (material3 == Material.RED_TERRACOTTA
				            ? "&7Click this to select from a list of perks for this tier."
				            : getPerkStringForTier(3, player.getFarmingPerks().getSelectedPerk(3)));
				
				// get the first item
				factory.flush(material1, 1);				
				factory.setDisplayName(displayName1);
				factory.setLore(lore1);
				ItemStack tier1 = factory.getItem();
				
			
				
				// second
				factory.flush(material2, 1);
				factory.setDisplayName(displayName2);
				factory.setLore(lore2);
				ItemStack tier2 = factory.getItem();
				
				factory.flush(material3, 1);
				factory.setDisplayName(displayName3);
				factory.setLore(lore3);
				ItemStack tier3 = factory.getItem();
				
				inv.setItem(20, tier1);
				inv.setItem(22, tier2);
				inv.setItem(24, tier3);
				
				p.openInventory(inv);
				
			}
		}
	}

}
