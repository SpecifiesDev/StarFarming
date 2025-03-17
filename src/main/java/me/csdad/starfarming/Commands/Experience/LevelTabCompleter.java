package me.csdad.starfarming.Commands.Experience;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class LevelTabCompleter implements TabCompleter {
	
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		// create a new list to store completion suggestions
		List<String> completions = null;
		
		// if args length is 1, suggest the two paramters permitted
		if(args.length == 1) {
			
			completions = new ArrayList<>();
			List<String> options = List.of("required", "view"); // our two options
			StringUtil.copyPartialMatches(args[0], options, completions); // bukkit api helper that helps filter out which completion is closer
			
		} else if(args.length == 2) {
			
			if(args[0].equalsIgnoreCase("required")) { // suggest applicable values for required
				
				
				List<Integer> integerSorting = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10); // loop over and pad zeros
				// this fixes the lexicographical sorting issue of "10" coming before "2".
				
				completions = integerSorting.stream()
						.map(num -> String.format("%02d", num)) // pad into 2 digit format
						.collect(Collectors.toList());
				
			} else if(args[0].equalsIgnoreCase("view")) { // suggest applicable values for view
				
				completions = Arrays.asList(new String[] {"farming"});
				
			}
			
		}
		
		// return the suggestions, sorted
		return completions;
		
	}

}
