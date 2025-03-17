package me.csdad.starfarming.Commands.Admin.Experience;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.csdad.starfarming.Core;

public class SetExperienceTabCompleter implements TabCompleter {
	
	private Core plugin;
	
	public SetExperienceTabCompleter() {
		this.plugin = Core.getInstance();
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> completions = null;
		
		if(args.length == 1) {
			
			completions = Arrays.asList("add", "remove", "set");
			
		} else if(args.length == 2) {
			
			String search = args[1].toLowerCase();
			
			List<String> starPlayers = this.plugin.getMemoryStore().getAllStarPlayers().values()
					.stream()
					.map(player -> player.getName())
					.toList();
			
			if(search.isEmpty()) {
				completions = starPlayers
						.stream() // restream
						.limit(10) // limit to 10
						.toList(); // convert back to list
						
			} else {
				
				completions = starPlayers
						.stream() // stream
						.filter(name -> name.toLowerCase().startsWith(search)) // filter by predicate
						.limit(10) // limit to 10
						.toList(); // convert back to list
				
			}
			
		} else if(args.length == 3) {
			
			// suggest a generic number
			completions = Arrays.asList("100");
			
		} else completions = null; // re-nullify completions
		
		
		
		return completions;
	}

}
