package me.csdad.starfarming.Commands.Experience;

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
import me.csdad.starfarming.Utility.StringFormatting;

public class LevelCommand implements CommandExecutor {
	
	// store an instance of our main class
	private Core plugin;
	
	public LevelCommand() { 
		this.plugin = Core.getInstance();
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// player only command, as guis are associated
		if(!(sender instanceof Player)) {
			sender.sendMessage(this.plugin.color(CommandReturn.SENDER_CONSOLE.getMessage()));
			return true;
		}
		
		// convert sender to a player object
		Player p = (Player) sender;
		
		// check if player has perms
		if(!(p.hasPermission(Permissions.LEVELS_ALL.getPermission()))) {
			p.sendMessage(this.plugin.color(CommandReturn.NO_PERMISSION.getMessage()));
			return true;
		}
		
		if(args.length == 0) {
			p.sendMessage(this.plugin.color(CommandReturn.LEVELS_INVALID_ARGS.getMessage()));
			return true;
		}
		
		// requiredxp handler
		if(args[0].equalsIgnoreCase("required")) {
			
			if(args.length != 2) {
				p.sendMessage(this.plugin.color(CommandReturn.LEVELS_INVALID_ARGS_REQUIREDXP.getMessage()));
				return true;
			}
			
			// make sure that the argument is a number, otherwise prevent this from being executed
			try {
				int level = Integer.parseInt(args[1]);
				
				if(level > 10) {
					p.sendMessage(this.plugin.color(CommandReturn.LEVELS_REQUIREDXP_GREATER_THAN_10.getMessage()));
					return true;
				}
				
				int requiredXP = ExperienceFormatting.getExperienceForLevel(level); // get the required xp
				
				String formattedExperience = StringFormatting.formatNumber(requiredXP).replace(",", "&7,&3"); // grey out commas for design
				
				
				p.sendMessage(this.plugin.color(this.plugin.getConfig().getString("logging.ingame-prefix") + " &bThe experience requirement for level &6" + level + " &bis &3" + formattedExperience + " &bexperience&7."));
			} catch(NumberFormatException e) {
				p.sendMessage(this.plugin.color(CommandReturn.LEVELS_REQUIREDXP_NFE.getMessage()));
			}
			
		} else if(args[0].equalsIgnoreCase("view")) { // level view handler
			
			List<String> allowList = Arrays.asList(new String[] {"farming"});
			
			// check if the args are met
			if(args.length != 2) {
				p.sendMessage(this.plugin.color(CommandReturn.LEVELS_INVALID_ARGS_VIEW.getMessage()));
				return true;
			}
			
			String query = args[1];
			
			// make sure the query is acceptable
			if(!(allowList.contains(query.toLowerCase()))) {
				p.sendMessage(this.plugin.color(CommandReturn.LEVELS_INVALID_VIEW_QUERY.getMessage()));
				return true;
			}
			
			// get the star player object from memory
			StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
			// create a blank inventory to construct
			Inventory inventory = Bukkit.createInventory(null, 54, this.plugin.color(this.plugin.getConfig().getString("logging.ingame-prefix") + " &8- &cLevels &8- &a" + StringFormatting.setFirstUppercase(args[1])));
			
			// create an item factory for populating the inventory
			ItemFactory factory = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE, 1);
			
			// set a blank name
			factory.setDisplayName("&8&m");
			
			// blanket fill the inventory
			for(int i = 0; i < 54; i++) inventory.setItem(i, factory.getItem());
			
			// an array of all the slots we want to fill
			int[] slots = {1, 3, 5, 7, 11, 15, 22, 30, 32, 40 };
			
			// loop over all 10 levels
			for(int j = 1; j<= 10; j++) {
				
				// these if statements set the material based on the difficulty of the level
				if(j <= 4) factory.setType(Material.GREEN_STAINED_GLASS_PANE);
				else if(j > 4 && j < 8) factory.setType(Material.YELLOW_STAINED_GLASS_PANE);
				else factory.setType(Material.RED_STAINED_GLASS_PANE);
				
				// set the display name to the level
				factory.setDisplayName("&cLevel &7- &c" + j);
				
				// get the xp required for that level
				int xpRequired = ExperienceFormatting.getExperienceForLevel(j);
				
				int xp = 0;
				
				if(query.equalsIgnoreCase("farming")) xp = player.getExperience(); // this functionality is for later features
				
				// if the player has completed level, set a generic message
				if(xp >= xpRequired) {
					factory.setLore("&c&lLevel Completed");
				} else {
					// display progress
					String formattedCurrentXP = StringFormatting.formatNumber(xp).replaceAll(",", "&7,&3");
					String formattedNeeded = StringFormatting.formatNumber(xpRequired).replaceAll(",", "&7,&3");
					
					factory.setLore("&cExperience~" + "&3" + formattedCurrentXP + "&7/&3" + formattedNeeded);
				}
				
				// set current item
				inventory.setItem(slots[j - 1], factory.getItem());
				
			}
			
			// open the constructed inventory
			p.openInventory(inventory);
			
			
			
		} else { // catch case
			
			p.sendMessage(this.plugin.color(CommandReturn.LEVELS_INVALID_ARGS.getMessage()));
			
		}
		
		return true;
		
	}
	

	

}
