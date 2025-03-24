package me.csdad.starfarming.Commands.Profile.Perks;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PerksTabCompleter implements TabCompleter {
	
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> completions = null;
		
		if(args.length == 1) {
			
			completions = Arrays.asList("farming");
			
		} 
		
		
		
		return completions;
	}

}
