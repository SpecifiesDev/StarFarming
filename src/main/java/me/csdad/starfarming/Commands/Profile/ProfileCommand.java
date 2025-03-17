package me.csdad.starfarming.Commands.Profile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.Commands.Experience.ExperienceFormatting;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.CommandReturn;
import me.csdad.starfarming.Errors.Permissions;
import me.csdad.starfarming.Utility.ItemFactory;
import me.csdad.starfarming.Utility.SkullFactory;
import me.csdad.starfarming.Utility.StringFormatting;

public class ProfileCommand implements CommandExecutor {
	
	private Core plugin;
	
	public ProfileCommand() {
		this.plugin = Core.getInstance();
	}

	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// player only command as we're using guis
		if(!(sender instanceof Player)) {
			sender.sendMessage(this.plugin.color(CommandReturn.SENDER_CONSOLE.getMessage()));
			return true;
		}
		
		// get our player
		Player p = (Player) sender;
		
		// check perm
		if(!(p.hasPermission(Permissions.PROFILE.getPermission()))) {
			
			p.sendMessage(this.plugin.color(CommandReturn.NO_PERMISSION.getMessage()));
			return true;
			
		}
		
		// try and get the starplayer
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
		
		// null check
		if(player == null) {
			p.sendMessage(this.plugin.color(CommandReturn.STARPLAYER_DNE.format("%player%", p.getName())));
			return true;
		}
		
		// get our prefix
		String prefix = this.plugin.getConfig().getString("logging.ingame-prefix");
		
		// let's begin constructing a new inventory
		Inventory profileInventory = Bukkit.createInventory(null, 45, this.plugin.color(prefix + " &8- &bProfile &8- &e" + p.getName()));
		
		// create an item factory
		ItemFactory factory = new ItemFactory(Material.STONE, 1);
		
		// blank name
		factory.setDisplayName("&8&m");
		
		// want to try a new gui design with the glass, may or may not implement it longterm
		for(int i = 0; i < 45; i++) {
			if(i % 2 == 0) factory.setType(Material.GRAY_STAINED_GLASS_PANE); // even slots
			else factory.setType(Material.WHITE_STAINED_GLASS_PANE); // odd
			profileInventory.setItem(i, factory.getItem());
		}
		
		// let's get a skull factory
		SkullFactory playerSkullFactory = new SkullFactory(p);
		
		playerSkullFactory.setDisplayName("&3General Information &8- &e" + p.getName());
		
		// get and format the xp
		String farmingExperience = StringFormatting.formatNumber(player.getExperience()).replaceAll(",", "&7,&3");
		// get the level
		int level = ExperienceFormatting.getLevelFromExperience(player.getExperience());
		
		// we do this to add a progress bar, indicating how close the player is to the next level
		int nextLevel = level + 1;
		String progressBar = StringFormatting.createPercentageBar(ExperienceFormatting.getExperienceForLevel(nextLevel), player.getExperience(), 5, '=');
		
		playerSkullFactory.setLore("&aFarming &bExperience&8: &3" + farmingExperience + "~&aFarming &bLevel&8: &3" + level + " &7[" + progressBar + "&7] &3" + nextLevel);
		
		// let's set the item
		profileInventory.setItem(22, playerSkullFactory.getSkull());
		
		// let's begin constructing the balance information
		factory.setType(Material.SUNFLOWER);
		
		factory.setDisplayName("&e&lBalance&8:");
		
		String balance = StringFormatting.formatNumber(player.getStarCoins()).replaceAll(",", "&7,&6");
		
		factory.setLore("&eStarCoins&8: &6" + balance);
		
		// set the item
		profileInventory.setItem(24, factory.getItem());
		
		// finally we can do the settings item, which will open another inventory on click
		factory.flush(Material.COMPARATOR, 1); // flush the lore as well
		
		factory.setDisplayName("&c&lPlayer Settings");
		
		
		profileInventory.setItem(20, factory.getItem());
		
		p.openInventory(profileInventory);
		
		return true;
	}
}
