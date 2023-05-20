package me.csdad.StarFarming;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.csdad.StarFarming.Commands.GiveSeed;
import me.csdad.StarFarming.Commands.Levels;
import me.csdad.StarFarming.Commands.SellAll;
import me.csdad.StarFarming.Crops.Crop;
import me.csdad.StarFarming.Crops.PlantedCrop;
import me.csdad.StarFarming.Events.HandleInventoryInteraction;
import me.csdad.StarFarming.Events.PlayerHarvestEvent;
import me.csdad.StarFarming.Events.PlayerPlantEvent;
import me.csdad.StarFarming.Events.ProvideSeeds;
import me.csdad.StarFarming.Experience.HandleDataCache;
import me.csdad.StarFarming.Experience.StarPlayer;
import me.csdad.StarFarming.Utility.SeasonManager;

public class Main extends JavaPlugin {
	
	// store an instance of main plugin here to access in external classes
	private static Main instance;
	
	// hashmaps to store varying data from registered crops to on the fly player data
	private HashMap<Block, PlantedCrop> plantedCrops = new HashMap<>();
	private HashMap<String, Crop> registeredCrops = new HashMap<>();
	private HashMap<Player, StarPlayer> playerData = new HashMap<>();
	
	// store a string of our season to display, by default starts at spring
	private String season = "SPRING";
	
	// store an instance of our seasonmanager
	private SeasonManager seasonManager;
	
	
	public void onEnable() {
		// instate our main instance
		instance = this;
		saveDefaultConfig();
		
		
		
		// register commands, crops, etcetera to stack
		registerCommands();
		registerEvents();
		registerCrops();
		startManagers();
		
	}
	
	public void onDisable() {
		
		// nullify our localized instance. Should always be declared last
		instance = null;
	}
	
	
	/**
	 * Method to return an instance of this class to pull pertinent data
	 * @return instance of this class
	 */
	public static Main getInstance() {
		return instance;
	}
	
	/**
	 * Method to return a list of all planted crops on the server
	 * @return All planted crops
	 */
	public HashMap<Block, PlantedCrop> getPlantedCrops() {
		return this.plantedCrops;
	}
	
	/**
	 * Method to return a list of all registered crops
	 * @return All registered crops
	 */
	public HashMap<String, Crop> getRegisteredCrops() {
		return this.registeredCrops;
	}
	
	/**
	 * Method to return a list of all online players.
	 * @return All online players.
	 */
	public HashMap<Player, StarPlayer> getStarPlayers() {
		return this.playerData;
	}
	
	
	/**
	 * Method to get an individual star player
	 * @param p The player to query
	 * @return StarPlayer object of the player's data
	 */
	public StarPlayer getStarPlayer(Player p) {
		return this.playerData.get(p);
	}
	
	/**
	 * Method to add an individual player to our cache
	 * @param p The bukkit player object of the target
	 * @param starplayer Our starplayer object of the target
	 */
	public void addStarPlayer(Player p, StarPlayer starplayer) {
		this.playerData.put(p, starplayer);
	}
	
	/**
	 * Method to remove an individual player from our cache
	 * @param p The player to remove
	 */
	public void removeStarPlayer(Player p) {
		this.playerData.remove(p);
	}
	
	/**
	 * Method used to get the current season the server tick is on
	 * @return season
	 */
	public String getSeason() {
		return this.season;
	}
	
	/**
	 * Method used to set a new season in the server's memory
	 * @param season to set
	 */
	public void setSeason(String season) {
		this.season = season;
	}
	
	/**
	 * Method to format color inside of strings
	 * @param m Message to format
	 * @return colorized message
	 */
	public String color(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}
	
	
	/**
	 *  PRIVATE METHODS
	 */
	
	private void registerEvents() {
		
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new HandleDataCache(), this);
		pm.registerEvents(new PlayerHarvestEvent(), this);
		pm.registerEvents(new ProvideSeeds(), this);
		pm.registerEvents(new PlayerPlantEvent(), this);
		pm.registerEvents(new HandleInventoryInteraction(),  this);
		
	}
	
	private void registerCommands() {
		
		getCommand("giveseed").setExecutor(new GiveSeed());
		getCommand("levels").setExecutor(new Levels());
		getCommand("sellall").setExecutor(new SellAll());
		
	}
	
	/**
	 * Method used to start up both our season manager and scoreboard manager.
	 */
	private void startManagers() {
		
		// init our seasonmanager
		this.seasonManager = new SeasonManager();
		
		// begin season count down
		this.seasonManager.startSeasonCycle();
		
		// check if server owner wants a scoreboard manager to loop
		// if so, begin the loop
		if(this.getConfig().getBoolean("toggle-scoreboard")) {
			this.seasonManager.startScoreBoardCycle();
		}
		
		
	}
	
	/**
	 * Method to loop over the config and register crops to our stack.
	 */
	private void registerCrops() {
		
		// we begin from our beginning string section
		for(String key : this.getConfig().getConfigurationSection("crops").getKeys(false)) {
			
			// let's just store a config value every loop for ease
			FileConfiguration config = this.getConfig();
			
			// just a list of all of our values we're pulling from config. For more info
			// on what lines mean refer to Crop.class
			String name = config.getString("crops." + key + ".name");
			String seedName = config.getString("crops." + key + ".seed_name");
			String material = config.getString("crops." + key + ".material");
			int growthTime = config.getInt("crops." + key + ".growth_time");
			int spread = config.getInt("crops." + key + ".spread");
			int base = config.getInt("crops." + key + ".base");
			Material productMaterial = Material.getMaterial(config.getString("crops." + key + ".product.material"));
			String productName = config.getString("crops." + key + ".product.name");
			int productPrice = config.getInt("crops." + key + ".product.sell_price");
			int xpPer = config.getInt("crops." + key + ".xp_per");
			double dropChance = getConfig().getDouble("crops." + key + ".drop_chance");
			ArrayList<String> seasons = new ArrayList<>(config.getStringList("crops." + key + ".seasons"));
			
			if(spread < base) {
				System.out.println("[StarFarming] Crop " + key + " has an invalid spread configuration. Spread needs to be higher than the base value. Crop will not be registered.");
			} else if(Material.getMaterial(material) == null || productMaterial == null) {
				System.out.println("[StarFarming] Crop " + key + " has a null material type configured. The crop will not be registered.");
			} else {
				// create a new crop object of our loaded crop
				Crop toRegister = new Crop(name, seedName, material, growthTime, spread, base, seasons, productMaterial, productName, productPrice, xpPer, dropChance);
				
				// place our registered crop into memory
				this.registeredCrops.put(ChatColor.stripColor(color(name)), toRegister);
				
				// send a message stating crop was registered.
				System.out.println("[StarFarming] Registered the Crop " + key + " successfully.");
			}
			
		}
		
	}
	
	

}
