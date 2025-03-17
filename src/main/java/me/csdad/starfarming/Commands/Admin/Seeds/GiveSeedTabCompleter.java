package me.csdad.starfarming.Commands.Admin.Seeds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.csdad.starfarming.Core;

public class GiveSeedTabCompleter implements TabCompleter {
	
	
	private Core plugin;
	public GiveSeedTabCompleter() {
		this.plugin = Core.getInstance();
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> completions = null;
		
		if(args.length == 1) {
			
			String searchPlayer = args[0].toLowerCase();
			
			List<String> searchedPlayers = new ArrayList<>();
			
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			
			players.stream() // break list into stream
			.filter(player -> player.getName().startsWith(searchPlayer)) // filter on the predicate that the players name starts with the search
			.forEach(player -> searchedPlayers.add(player.getName())); // loop over the filtered list and add their names to a buffer list
			
			// set our return variable to the filtered list
			completions = searchedPlayers;
			
		} else if(args.length == 2) {
			
			String search = args[1].toLowerCase();
			
			// get all registered seeds to suggest, setting it to lower case, and replacing any spaces with _
			List<String> allSeeds = this.plugin.getMemoryStore().getCrops().keySet()
					.stream()
					.map(seed -> seed.replaceAll(" ", "_").toLowerCase())
					.toList();
			
			// just return the list if nothing is searched
			if(args[1].isEmpty()) {
				completions = allSeeds;
			} else {
				
				// otherwise perform the same search functionality as the first args
				completions = allSeeds.stream()
						.filter(seed -> seed.startsWith(search))
						.collect(Collectors.toList());
			}
		
			
		} else if(args.length == 3) {
			
			// just populate a generic "amount" indicating that the last arg is an amount and is required
			completions = Arrays.asList("1");
			
		} else {
			completions = Arrays.asList(""); // depopulate
		}
		
		return completions;
		
	}
	
}
