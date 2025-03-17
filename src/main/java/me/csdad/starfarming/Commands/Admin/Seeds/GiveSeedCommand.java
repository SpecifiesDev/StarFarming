package me.csdad.starfarming.Commands.Admin.Seeds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.StarCrop;
import me.csdad.starfarming.Errors.CommandReturn;
import me.csdad.starfarming.Errors.Permissions;

public class GiveSeedCommand implements CommandExecutor {
	
	private Core plugin;
	
	public GiveSeedCommand() {
		this.plugin = Core.getInstance();
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// no need to type check sender, as there's no need for a player object
		
		// make sure argument requirements are met
		if(!(args.length == 3)) {
			sender.sendMessage(this.plugin.color(CommandReturn.ADMIN_GIVESEED_INVALID_ARGS.getMessage()));
			return true;
		}
		
		// make sure the sender meets the permission requirements
		if(!(sender.hasPermission(Permissions.GIVESEED.getPermission()))) {
			sender.sendMessage(this.plugin.color(CommandReturn.NO_PERMISSION.getMessage()));
			return true;
		}
		
		// let's parse the necessary information
		String seed;
		Player target;
		int amount;
		
		// get the seed
		seed = args[1].replace("_", " "); // remove the _ from the middle
		
		// player, must be online
		target = Bukkit.getPlayerExact(args[0]);
		
		// perform validation
		
		// validate the the amount matches a number pattern
		// initially attempted via a try/catch but some numbers were being caught when they weren't supposed to.
		if(!(args[2].replaceAll(" ", "").matches("-?\\d+"))) {

			sender.sendMessage(this.plugin.color(CommandReturn.INVALID_AMOUNT.getMessage()));
			return true;
		} 
		
		amount = Integer.parseInt(args[2]);
		amount = (amount > 64) ? 64 : amount;
		
		// get the crop from the inputed string 
		StarCrop crop = this.plugin.getMemoryStore().getCrop(seed);
		
		
		// validate the crop
		if(crop == null) {
			sender.sendMessage(this.plugin.color(CommandReturn.ADMIN_GIVESEED_INVALID_SEED.getMessage()));
			return true;
		}
		
		// validate the target player is online
		if(target == null || !(target instanceof Player)) {
			sender.sendMessage(this.plugin.color(CommandReturn.ADMIN_GIVESEED_OFFLINE_TARGET.format("%player%", args[0])));
			return true;
		}
		
		
		// now that all valid checks have passed, let's grab the actual seed
		ItemStack seedStack = crop.getSeed();
		seedStack.setAmount(amount); // update the amount
		
		// add the item to the target's inventory
		target.getInventory().addItem(new ItemStack[] {seedStack});
		
		// notify them
		target.sendMessage(this.plugin.color(CommandReturn.ADMIN_GIVESEED_TARGET_RECEIVE.format("%crop%", crop.getName())));
		
		return true;
	}

}
