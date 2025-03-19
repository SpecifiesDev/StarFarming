package me.csdad.starfarming.Commands.Admin.Experience;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.CommandReturn;
import me.csdad.starfarming.Errors.Permissions;
import me.csdad.starfarming.StarEvents.ExperienceGainEvent;
import me.csdad.starfarming.Utility.StringFormatting;

public class SetExperience implements CommandExecutor {
	
	private Core plugin;
	
	public SetExperience() {
		this.plugin = Core.getInstance();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// no need to player check as there's no need to cast
		
		// check arguments
		if(!(args.length == 3)) {
			sender.sendMessage(this.plugin.color(CommandReturn.ADMIN_SETEXP_INVALID_ARGS.getMessage()));
			return true;
		}
		
		// check permission
		if(!(sender.hasPermission(Permissions.SETEXP.getPermission()))) {
			sender.sendMessage(this.plugin.color(CommandReturn.NO_PERMISSION.getMessage()));
			return true;
		}
		
		// let's declare applicable actions
		List<String> actions = Arrays.asList("add", "remove", "set");
		
		// let's begin parsing our args
		String action = args[0];
		String playerName = args[1];
		
		// make sure we can convert args[2] into an int
		int amount;
		if(!(args[2].replaceAll(" ", "").matches("-?\\d+"))) {

			sender.sendMessage(this.plugin.color(CommandReturn.INVALID_AMOUNT.getMessage()));
			return true;
		} 
		
		// parse our amount 
		amount = Integer.parseInt(args[2]);
		
		// validate that the action is allowed
		if(!(actions.contains(action.toLowerCase()))) {
			
			sender.sendMessage(this.plugin.color(CommandReturn.ADMIN_SETEXP_INVALID_ACTION.getMessage()));
			return true;
			
		}
		
		// try and retrieve the player's object from the cache
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayerByName(playerName);
		
		// validate it
		if(player == null) {
			
			sender.sendMessage(this.plugin.color(CommandReturn.ADMIN_SETEXP_INVALID_TARGET.format("%player%", playerName)));
			return true;
			
		}
		
		int xpBefore = player.getExperience();
		
		// we can pre-format the xp string as we are using a generic string for every one
		String formattedMessaging = CommandReturn.ADMIN_SETEXP_GENERIC
				.format("%experience%", StringFormatting.formatNumber(amount))
				.replaceAll(",", "&7,&3");
		

		
		
		
		// everything is valid, we can perform individual actions
		switch(action.toLowerCase()) {
		    case "add": {
		    	player.addExperience(amount);
		    	sender.sendMessage(this.plugin.color(formattedMessaging)
		    			.replaceAll("%action%", "added") // individual verbage for the action
		    			.replaceAll("%actionverb%", "to")
		    			.replaceAll("%player%", playerName));
		    	
		    	
		    	break;
		    }
		    case "remove": {
		    	player.substractExperience(amount);
		    	sender.sendMessage(this.plugin.color(formattedMessaging)
		    			.replaceAll("%action%", "removed")
		    			.replaceAll("%actionverb%", "from")
		    			.replaceAll("%player%", playerName));
		    	break;
		    }
		    case "set": {
		    	player.setExperience(amount);
		    	sender.sendMessage(this.plugin.color(formattedMessaging)
		    			.replaceAll("%action%", "set")
		    			.replaceAll("%actionverb%", "for")
		    			.replaceAll("%player%", playerName));
		    	break; // no need for a default case, as we explitcitly check the action above
		    }
		    
		    
		}
		
		
		int xpAfter = player.getExperience();
		ExperienceGainEvent xpEvent = new ExperienceGainEvent(player, "farming", xpBefore, xpAfter);
		
		Bukkit.getPluginManager().callEvent(xpEvent);
		
		
		return true;
	}

}
