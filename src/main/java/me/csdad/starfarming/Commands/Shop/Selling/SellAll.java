package me.csdad.starfarming.Commands.Shop.Selling;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.Errors.CommandReturn;
import me.csdad.starfarming.Errors.Permissions;
import me.csdad.starfarming.Utility.ItemFactory;

public class SellAll implements CommandExecutor {
	
	private Core plugin;
	public SellAll() {
		this.plugin = Core.getInstance();
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(this.plugin.color(CommandReturn.SENDER_CONSOLE.getMessage()));
			return true;
		}
		
		// get our player
		Player p = (Player) sender;
		
		// perm check
		if(!(p.hasPermission(Permissions.SELLALL.getPermission()))) {
			sender.sendMessage(this.plugin.color(CommandReturn.NO_PERMISSION.getMessage()));
			return true;
		}
		
		// create a blank inventory and item factory
		Inventory inv = Bukkit.createInventory(null, 54, this.plugin.color("&6Harvest Shop"));
		ItemFactory factory = new ItemFactory(Material.WHITE_STAINED_GLASS_PANE, 1);
		
		// set a blank name
		factory.setDisplayName("&7&m");

		for(int i = 45; i < 54; i++) {inv.setItem(i, factory.getItem());} // populate bottom row
		
		// create the sell item
		factory.setType(Material.HOPPER);
		factory.setDisplayName("&b&lSell All");
		
		// set our button item and open the inv for the player
		inv.setItem(49, factory.getItem());
		p.openInventory(inv);
		
		return true;
	}

}
