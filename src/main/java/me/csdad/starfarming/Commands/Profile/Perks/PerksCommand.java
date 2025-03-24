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

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.CommandReturn;
import me.csdad.starfarming.Errors.Permissions;
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
		
		return true;
	}
	
	private void handlePerkCreation(String type, Player p, StarPlayer player) {
		
		Inventory inv = Bukkit.createInventory(null, 45, this.plugin.color(this.plugin.getConfig().getString("logging.ingame-prefix") + " &8- &3Farming &6Perks"));
		
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
				
			}
		}
	}

}
