package me.csdad.starfarming.Events.CropEvents;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.CropInfo;
import me.csdad.starfarming.DataStructures.PlantedCrop;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.PlayerMessaging;
import me.csdad.starfarming.StarEvents.ExperienceGainEvent;
import me.csdad.starfarming.StarEvents.StarCropHarvestEvent;
import me.csdad.starfarming.Utility.ItemFactory;
import me.csdad.starfarming.Utility.StringFormatting;

public class PlayerHarvestEvent implements Listener {
	
	
	// store an instance of our main class
	private Core plugin;
	
	public PlayerHarvestEvent() {
		
		this.plugin = Core.getInstance();
	}
	
	
	@EventHandler
	public void harvest(BlockBreakEvent e) { 
		
		// retrieve the player and block objects
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		// if the block isn't a planted crop, skip
		if(!this.plugin.getMemoryStore().getPlantedCrops().containsKey(b)) return;
		
		// grab the cached crop
		PlantedCrop crop = this.plugin.getMemoryStore().getPlantedCrop(b);
		
		// grab the cached star player
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
		
		// if the player harvesting does not owner the crop, alert them and cancel the event
		if(!(player.getUUID().equalsIgnoreCase(crop.getOwner().getUUID())))  {
			p.sendMessage(this.plugin.color(PlayerMessaging.PLANTED_CROP_NO_OWNERSHIP.getMessage()));
			e.setCancelled(true);
			return;
		}
		
		if(!crop.canHarvest()) {
			
			e.setCancelled(true);
			
			Inventory cropInformationInventory = Bukkit.createInventory(null, 45, this.plugin.color("&aHarvest Info"));
			
			CropInfo information = new CropInfo(player, crop.getCrop(), b);
			
			this.plugin.getMemoryStore().addCropInfo(p, information); // place the player into the memory store for updates
			
			ItemFactory factory = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE, 1);
			factory.setDisplayName("&8&m");
			
			for(int i = 0; i < 45; i++) {
				cropInformationInventory.setItem(i, factory.getItem());
			}
			
			
			// time until harvest clock item
			factory.setType(Material.CLOCK);
			factory.setDisplayName("&cTime Until Harvest&7:");
			factory.setLore(crop.getTimeLeftFormatted());
			cropInformationInventory.setItem(19, factory.getItem());
			
			
			// general crop info like growable seasons
			factory.flush(crop.getCrop().getProduct(1).getType(), 1);
			factory.setDisplayName(crop.getCrop().getName());
			
			ArrayList<String> seasons = crop.getCrop().getGrowableSeasons();
			
			String seasonsLore = "";
			
			for(String season : seasons) {
				seasonsLore += "&8- &7" + lowerCasePartially(season) + "~"; // set the end of the season to lower case
			}
			factory.setLore("&6&lGrows In&8:~" + seasonsLore);
			cropInformationInventory.setItem(22, factory.getItem());
			
			// expected profits, base to max spread
			factory.flush(Material.SUNFLOWER, 1);
			factory.setDisplayName("&e&lExpected Profits&7");
			
			int sell = crop.getCrop().getSellPrice();
			int base = crop.getCrop().getBase();
			int spread = crop.getCrop().getSpread();
			
			factory.setLore("&bProduce Range&7:~&7" + base + "&8-&7" + spread + "~&bValue&7:~&7" + sell * base + "&8-&7" + sell * spread);
			cropInformationInventory.setItem(25, factory.getItem());
			
			
			
			
			p.openInventory(cropInformationInventory);
			
			return;
		}
		
		// PC has a built in method, but it doesnt account for the need of variability so we implement a custom helper here
		harvestCrop(crop, b, p, player);
		
	}
	
	/**
	 * Method to harvest a crop with the functionality intended with this class
	 * @param crop PlantedCrop to harvest
	 * @param b The block location of the planted crop
	 * @param p The owner of the crop
	 */
	private void harvestCrop(PlantedCrop crop, Block b, Player p, StarPlayer player) {
		
		int randomProduce = getProduceAmount(crop.getCrop().getSpread(), crop.getCrop().getBase());
		
		ItemStack product = crop.getCrop().getProduct(randomProduce); // end product with the generated produce
		ItemStack seed = crop.getCrop().getSeed(); // seed
		
		// let's add a factor of variability to give a chance of getting two seeds
		Random generator = new Random();
		double generated = generator.nextDouble();
		
		if(generated > .98) seed.setAmount(2); // 2% chance to get 2 seeds.
		
		p.getInventory().addItem(new ItemStack[] {product, seed});
		
		int xpGained = crop.getCrop().getXp();
		
		StringFormatting.sendExperienceActionBar(xpGained, p, "Farming");
		

		
		if(!(b.getType() == Material.AIR)) b.setType(Material.AIR); // if the player is breaking, it will be air. otherwise we must break it.
		this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString()).addExperience(xpGained); // add the xp to the player
		this.plugin.getMemoryStore().removePlantedCrop(b); // remove
		
		// send down bukkit events so listeners can do something else later on
		StarCropHarvestEvent harvestEvent = new StarCropHarvestEvent(player, p, crop.getCrop());
		ExperienceGainEvent experienceEvent = new ExperienceGainEvent(player, p, xpGained, "farming");
		
		// call the events
		Bukkit.getPluginManager().callEvent(harvestEvent);
		Bukkit.getPluginManager().callEvent(experienceEvent);
		
	}
	
	// there are going to be a lot of events for the functionality of this whole feature so, i will break each one down with their purpose
	
	// this chain of events will handle the player leaving and existing in the memory store, as well as closing the inventory, and moving.
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		
		removePlayerCache(p);
		
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		
		Player p = e.getPlayer();
		
		removePlayerCache(p);
		
	}
	
	@EventHandler
	public void onInvLeave(InventoryCloseEvent e) {
		
		if(!(e.getPlayer() instanceof Player)) return;
		
		Player p = (Player) e.getPlayer();
		
		removePlayerCache(p);
		
	}
	
	
	@EventHandler
	public void inventoryHarvest(InventoryClickEvent e) {
		
		if(!(e.getWhoClicked() instanceof Player)) return;
		
		Player p = (Player) e.getWhoClicked();
		
		StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
		
		if(e.getCurrentItem() == null) return;
		
		if(player == null) return;
		
		if(this.plugin.getMemoryStore().getCropInfo(p) == null) return;
		
		ItemStack clicked = e.getCurrentItem();
		
		e.setCancelled(true);
		
		if(clicked.getType() == Material.CLOCK) {
			
			Block worldCrop = this.plugin.getMemoryStore().getCropInfo(p).getPlantedBlock();
			PlantedCrop crop = this.plugin.getMemoryStore().getPlantedCrop(worldCrop);
			
			if(!(crop.canHarvest())) return;
			
			this.plugin.getMemoryStore().removeCropInfo(p); // remove the player as its going to take them out of this screen
			p.closeInventory(); // close the player's inventory
			harvestCrop(crop, crop.getCropBlock(), p, player); // harvest the crop
			
			
			
			
			
		}
		
	}
	
	// don't allow field or block below to be broken if a planted crop exists above.
	@EventHandler
	public void checkIfField(BlockBreakEvent e) {
		
		Block target = e.getBlock().getRelative(BlockFace.UP);
		
		if(this.plugin.getMemoryStore().getPlantedCrops().containsKey(target)) {
			e.setCancelled(true);
		}
		
	}
	
	// prevent environmental destruction of crops
	public void preventBlockFromTo(BlockFromToEvent e) {
		
		if(this.plugin.getMemoryStore().getPlantedCrops().containsKey(e.getBlock())) {
			
			if(e.getBlock().getType() == Material.WATER || e.getBlock().getType() == Material.LAVA) {
				
				e.setCancelled(true);
				
				
			}
			
		}
		
	}
	
	// prevent trampling of crops
	public void preventTrample(PlayerInteractEvent e) {
		
		if(e.getAction() == Action.PHYSICAL) {
			
			Block interacted = e.getClickedBlock();
			
			if(interacted == null) return;
			
			Block above = interacted.getRelative(BlockFace.UP);
			
			if(above == null) return;
			
			if(this.plugin.getMemoryStore().getPlantedCrops().containsKey(above)) {
				
				e.setCancelled(true);
				
			}
			
		}
		
	}
	
	// prevent fading of a field if it has a planted crop
	public void preventFade(BlockFadeEvent e) {
		
		Block fade = e.getBlock();
		
		if(fade == null) return;
		
		Block above = fade.getRelative(BlockFace.UP);
		
		if(this.plugin.getMemoryStore().getPlantedCrops().containsKey(above) && e.getNewState().getBlock().getType() == Material.DIRT) {
			e.setCancelled(true);
		}
		
	}
	
	private String lowerCasePartially(String s) {
		return s.charAt(0) + s.substring(1).toLowerCase();
	}
	
	private void removePlayerCache(Player p) {
		
		if(this.plugin.getMemoryStore().getCropInfo(p) != null) {
			this.plugin.getMemoryStore().removeCropInfo(p);
		}
		
	}
	
	private int getProduceAmount(int spread, int base) {
		return (int) (Math.random() * (spread - base) + base);
		
	}

}
