package me.csdad.StarFarming.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Utility.ItemFactory;
import me.csdad.StarFarming.Utility.MessageConstants;

public class SellAll implements CommandExecutor {
	
	// store an instance of our main class
	private Main plugin;
	
	public SellAll() {
		this.plugin = Main.getInstance();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// as this is an inventory command, sender must be a player
		if(sender instanceof Player) {
			
			// grab an instancce of our player
			Player p = (Player) sender;
			
			// create a blank inventory
			Inventory inv = Bukkit.createInventory(null, 54, this.plugin.color("&6Harvest Shop"));
			
			// create a new item factory
			ItemFactory factory = new ItemFactory(Material.WHITE_STAINED_GLASS_PANE, 1);

			// set a blank name
			factory.setDisplayName("&7");
			
			// fill the bottom row with our filler
			for(int i = 45; i < 54; i++) {
				inv.setItem(i,  factory.getItem());
			}
			
			// set itemtype to hopper for the sell button
			factory.setType(Material.HOPPER);
			
			// set the display name to indicate this is the button
			factory.setDisplayName("&b&lSell All");
			
			// set our button item and open the inventory for the player
			inv.setItem(49, factory.getItem());
			p.openInventory(inv);
			
		} else {
			sender.sendMessage(this.plugin.color(MessageConstants.MUST_BE_PLAYER));
		}
		
		
		return true;
	}

}
