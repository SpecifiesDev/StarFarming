package me.csdad.StarFarming.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Experience.ExperienceFormatting;
import me.csdad.StarFarming.Experience.StarPlayer;
import me.csdad.StarFarming.Utility.ItemFactory;
import me.csdad.StarFarming.Utility.MessageConstants;
import me.csdad.StarFarming.Utility.StringFormatting;

public class Levels implements CommandExecutor {
	
	// store an instance of our main class
	private Main plugin;
	
	public Levels() {
		this.plugin = Main.getInstance();
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// ensure sender is a player, if not prevent command
		if(sender instanceof Player) {
			
			// grab an instance of our player, and their data
			Player p = (Player) sender;
			StarPlayer player = this.plugin.getStarPlayer(p);
			
			// create a blank inventory to construct
			Inventory inv = Bukkit.createInventory(null, 54, this.plugin.color("&cLevels"));
			
			// create an item factory to make this easier
			ItemFactory factory = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE, 1);
			
			// set a blank name
			factory.setDisplayName("&8");
			
			// just blanket fill the inventory, we'll make a pattern using an array
			for(int i = 0; i < 54; i++) inv.setItem(i, factory.getItem());
			
			// here's an array of all the slots we want level panes to be in
			int[] slots = { 1, 3, 5, 7, 11, 15, 22, 30, 32, 40 };
			
			// now we're going to loop over all 10 levels
			for(int j = 1; j <= 10; j++) {
				
				// if we're 1-4 we're going to be in the green zone
				// yellow for 5-7
				// and red for 8-10
				if(j <= 4) {
					factory.setType(Material.GREEN_STAINED_GLASS_PANE);
				} else if(j > 4 && j < 8) {
					factory.setType(Material.YELLOW_STAINED_GLASS_PANE);
				} else {
					factory.setType(Material.YELLOW_STAINED_GLASS_PANE);
				}
				
				// now we're going to set our display name to the level
				factory.setDisplayName("&cLevel &7- &c" + j);
				
				// retrieve the xp required for that level
				int xp = ExperienceFormatting.getRequiredXpForLevel(j);
				
				// if the player has completed the level, set it to a generic msg
				if(player.getFarming() >= xp) {
					factory.setLore("&c&lLevel Completed");
				} 
				// format a new message for the progress
				else {
					String formattedCurrent = StringFormatting.formatNumber(player.getFarming()).replaceAll(",", "&8,&7");
					String formattedEnd = StringFormatting.formatNumber(xp).replaceAll(",", "&7,&3");
					factory.setLore("&cExperience~" + formattedCurrent + "&7/&3" + formattedEnd);
				}
				
				// set the appropriate item
				inv.setItem(slots[j - 1], factory.getItem());
				
				
			}
			
			
			
		} else {
			sender.sendMessage(plugin.color(MessageConstants.MUST_BE_PLAYER));
		}
		
		
		return true;
	}
	
	

}
