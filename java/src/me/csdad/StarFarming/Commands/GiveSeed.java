package me.csdad.StarFarming.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Crops.Crop;
import me.csdad.StarFarming.Utility.MessageConstants;

public class GiveSeed implements CommandExecutor {
	
	// store an instance of our main class
	private Main plugin;
	
	public GiveSeed() {
		this.plugin = Main.getInstance();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// first we ensure that the appropriate args exist
		if(args.length == 3) {
			
			
			// we'll do the logic if the sender is a player first
			if(sender instanceof Player) {
				
				// grab an object of the executing player
				Player p = (Player) sender;
				
				// now we ensure the player has permission to run this command
				if(p.hasPermission("starfarming.give.seed")) {
					
					// format our seed arg to replace _ with spacing
					String seed = args[1].replace("_", " ");
					
					// grab an object of our target player to give the seed
					Player target = Bukkit.getPlayerExact(args[0]);
					
					// grab the amount of seeds to give (max 64)
					int amount = Integer.parseInt(args[3]);
					if(amount > 64) amount = 64;
					
					// if the target is null, they're not online
					if(!(target == null)) {
						
						// try and grab a crop from registry with given name
						Crop targetCrop = this.plugin.getRegisteredCrops().get(seed);
						
						// if the crop is null, it doesn't exist or was inputted improperly
						if(!(targetCrop == null)) {
							
							// grab the seed from the crop and set the proper amount
							ItemStack crop = targetCrop.getSeed();
							
							// set the amount
							crop.setAmount(amount);
							
							// add the crop to the player's inv and alert them
							target.getInventory().addItem(new ItemStack[] {crop});
							target.sendMessage(this.plugin.color("&aYou&7'&ave been given the crop " + targetCrop.getName() + "&7."));
							
						} else {
							p.sendMessage(MessageConstants.GIVE_SEED_INVALID_REGISTER);
						}
						
						
					} else {
						p.sendMessage(this.plugin.color(MessageConstants.GIVE_SEED_OFFLINE_PLAYER));
					}
					
				} else {
					p.sendMessage(this.plugin.color(MessageConstants.INVALID_PERMISSION));
				}
				
				
				
				
			} else {
				
				// this chunk is the same as above apart from perm check / conversion
				String seed = args[1].replace("_", " ");
				Player target = Bukkit.getPlayerExact(args[0]);
				int amount = Integer.parseInt(args[2]);
				if(amount > 64) amount = 64;
				
				if(!(target == null)) {
					
					Crop targetCrop = this.plugin.getRegisteredCrops().get(seed);
					
					if(!(targetCrop == null)) {
						
						ItemStack crop = targetCrop.getSeed();
						crop.setAmount(amount);
						target.getInventory().addItem(new ItemStack[] {crop});
						target.sendMessage(this.plugin.color("&aYou&7'&ave been given the crop " + targetCrop.getName() + "&7."));
						
					} else {
						sender.sendMessage(this.plugin.color(MessageConstants.GIVE_SEED_INVALID_REGISTER));
					}
					
				} else {
					sender.sendMessage(this.plugin.color(MessageConstants.GIVE_SEED_OFFLINE_PLAYER));
				}
				
			}
			
			
		} else {
			sender.sendMessage(this.plugin.color("&7/&cgiveseed &7<&cplayer&7> <&cseed&7> <&camount&7>"));
		}
		
		return true;
		
	}

}
