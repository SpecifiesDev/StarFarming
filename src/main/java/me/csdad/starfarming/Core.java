package me.csdad.starfarming;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.csdad.starfarming.Commands.Admin.Experience.SetExperience;
import me.csdad.starfarming.Commands.Admin.Experience.SetExperienceTabCompleter;
import me.csdad.starfarming.Commands.Admin.Seeds.GiveSeedCommand;
import me.csdad.starfarming.Commands.Admin.Seeds.GiveSeedTabCompleter;
import me.csdad.starfarming.Commands.Experience.HandleLevelInteraction;
import me.csdad.starfarming.Commands.Experience.LevelCommand;
import me.csdad.starfarming.Commands.Experience.LevelTabCompleter;
import me.csdad.starfarming.Commands.Profile.HandleProfileInteraction;
import me.csdad.starfarming.Commands.Profile.HandleSettingsInteraction;
import me.csdad.starfarming.Commands.Profile.ProfileCommand;
import me.csdad.starfarming.Commands.Shop.Selling.HandleSellAllInteraction;
import me.csdad.starfarming.Commands.Shop.Selling.SellAll;
import me.csdad.starfarming.DataStructures.MemoryStore;
import me.csdad.starfarming.DataStructures.StarCrop;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Databasing.DatabaseManager;
import me.csdad.starfarming.Databasing.PlayerManager;
import me.csdad.starfarming.Databasing.PersistentCrops.PersistentCropManager;
import me.csdad.starfarming.Errors.DatabaseLogging;
import me.csdad.starfarming.Errors.GeneralLogging;
import me.csdad.starfarming.Events.PlayerDataManagement;
import me.csdad.starfarming.Events.CropEvents.PlayerHarvestEvent;
import me.csdad.starfarming.Events.CropEvents.PlayerPlantEvent;
import me.csdad.starfarming.Events.CropEvents.ProvideSeeds;
import me.csdad.starfarming.Utility.SeasonManager;
import net.md_5.bungee.api.ChatColor;

public class Core extends JavaPlugin {
	
	
	private static Core instance;
	
	// database manager in order to establish connection and handle a conn pool for performance
	private DatabaseManager manager;
	
	// memory store to handle all of our in memory data
	private MemoryStore memstore;
	
	// season manager to handle seasons and scoreboard runnables
	private SeasonManager seasonManager;
	
	// crop persistence manager
	private PersistentCropManager saveCrops;
	
	// indicate if logging is set to be verbose
	private boolean verbose_logging;
	
	
	public void onEnable() {
		instance = this;
		
		// load default config
		saveDefaultConfig();
		
		// load the verbose logging setting
		verbose_logging = this.getConfig().getBoolean("logging.verbose-info");
		
		// init our database manager
		manager = new DatabaseManager();
		memstore = new MemoryStore();
		// setup our persistence manager and load saved crops back into memory
		saveCrops = new PersistentCropManager();
		
		// try to set the initial season, if an error is received disable this plugin
		try {
			memstore.setSeason(this.getConfig().getString("general.seasons.start-season"));
		} catch(Exception err) {
			err.printStackTrace();
			Bukkit.getLogger().log(Level.SEVERE, GeneralLogging.DISABLING_PLUGIN.getLog());
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		// next, we should re-serialize all planted objects from the database back into the memory store. 
		
		// setup default tables if they don't exist, will also call load players into memory store once the tables are created on first startup
		setupDefaultDatabaseTables();
		
		
		// load the crops in config into the registry
		loadCropRegistry();
		

		
		// register commands and events
		registerCommands();
		registerEvents();
		
		// setup season runnables
		seasonManager = new SeasonManager();
		
		seasonManager.start();


	}
	
	public void onDisable() {
		

		
		// serialize and save planted crops into the database
		saveCrops.saveAllCrops();
		
		// stop all season management
		seasonManager.stop();
		
		// save player data
		saveMemoryStore();
		
		
		this.memstore.clearStore();
		
		
		instance = null;
	}
	

	
	public static Core getInstance() {
		return instance;
	}
	
	public String color(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}
	
	public MemoryStore getMemoryStore() {
		return this.memstore;
	}
	
	public Connection getNewConnection() throws SQLException {
		return this.manager.getConnection();
	}
	
	public PersistentCropManager getCropManager() {
		return this.saveCrops;
	}
	
	/**
	 * private functions
	 * 
	 */
	
	private void registerCommands() {
		
		// register the levels command and its tabcompleter
		getCommand("level").setExecutor(new LevelCommand());
		getCommand("level").setTabCompleter(new LevelTabCompleter());
		
		// register the giveseed command and its tabcompleter
		getCommand("giveseed").setExecutor(new GiveSeedCommand());
		getCommand("giveseed").setTabCompleter(new GiveSeedTabCompleter());;
		
		// register the setexp command and its tabcompleter
		getCommand("setexperience").setExecutor(new SetExperience());
		getCommand("setexperience").setTabCompleter(new SetExperienceTabCompleter());
		
		// register sellall, no need for TC as there are no args
		getCommand("sellall").setExecutor(new SellAll());
		
		// register profile, no tc as no args
		getCommand("profile").setExecutor(new ProfileCommand());
	}
	
	private void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		
		// player database event management
		pm.registerEvents(new PlayerDataManagement(), this);
		
		// crop events
		pm.registerEvents(new ProvideSeeds(), this);
		pm.registerEvents(new PlayerPlantEvent(), this);
		pm.registerEvents(new PlayerHarvestEvent(), this);
		
		// level command events
		pm.registerEvents(new HandleLevelInteraction(), this);
		
		// sellall command events
		pm.registerEvents(new HandleSellAllInteraction(), this);
		
		// profile click events
		pm.registerEvents(new HandleProfileInteraction(), this);
		
		// register settings click events
		pm.registerEvents(new HandleSettingsInteraction(), this);
		
		
		// testing registration
		// pm.registerEvents(new TestCustomEvents(), this);
		
	}
	
	private void info(String m) {
		Bukkit.getLogger().info(m);
	}
	
	private void loadPlayersIntoMemoryStore(SQLException err) {
		
		info("Loading all existing database players into memory store...");
		
		
		if(verbose_logging) {
			info("Verbose logging is enabled in the config...");
		} else {
			info("Verbose logging is disabled in the config... Skipping logging individual events");
		}

			
	
		// get a new player manager to handle getting the data
		PlayerManager pm = new PlayerManager();
		
		// get a new conn from the cp
		try {
			Connection conn = this.getNewConnection();
			
			HashMap<String, StarPlayer> loadedPlayers = pm.getAllStarPlayers(conn, this, verbose_logging);
			
			this.memstore.setPlayers(loadedPlayers);
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		}
			

	
	}
	
	private void saveMemoryStore() {
		
		info("Saving all player data in memory store back into data base...");
		
		for(Map.Entry<String, StarPlayer> entry : this.memstore.getAllStarPlayers().entrySet()) {
			
			String uuid = entry.getKey();
			StarPlayer player = entry.getValue();
			
			// save
			try {
				
				Connection conn = this.manager.getConnection();
				player.save(conn);
				
				
				if(verbose_logging) {
					info("Saved player data for player " + uuid + " into the database.");
				}
				
				conn.close(); // return to pool and reiterate until all memory is saved back into database
				
			} catch(SQLException e) {
				
				e.printStackTrace(); // no need for verbose logging
				
			}
			
		}
		
		info("Saving complete.");
		info("Clearing memory store");
		this.memstore.clearStore();
		
	}
	
	private void setupDefaultDatabaseTables() {
		
		// do this inside of a separate thread
		new BukkitRunnable() {
			@Override
			public void run() {
				
				Connection conn;
				
				try {
					
					conn = manager.getConnection();
					
					// statement to create the basic players table
					PreparedStatement players = conn.prepareStatement("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT, experience INT, starcoins INT, settings TEXT)");
					
					players.execute();
					
					PreparedStatement persistentCrops = conn.prepareStatement("CREATE TABLE IF NOT EXISTS planted_crops("
						+ "owner_uuid TEXT, "	
						+ "crop_x INT, crop_y INT, crop_z INT, world_name TEXT,"
						+ "crop_id TEXT, planted_time BIGINT,"
						+ "PRIMARY KEY(crop_x, crop_y, crop_z, world_name),"
						+ "FOREIGN KEY(owner_uuid) REFERENCES players(uuid))"
						);
					
					
					persistentCrops.execute();
					
					PreparedStatement farmingPerks = conn.prepareStatement("CREATE TABLE IF NOT EXISTS farming_perks("
							+ "owner_uuid TEXT, farming_level INT"
							+ "perk_1 INT, perk_1_opt INT, perk_1_opt_2 INT, perk_1_selected INT"
							+ "perk_2 INT, perk_2_opt INT, perk_2_opt_2 INT, perk_2_selected INT"
							+ "perk_3 INT, perk_3_opt INT, perk_3_opt_3 INT, perk_3_selected INT)"
							);
					
					farmingPerks.execute();
					
					// release the conn back to the pool`
					conn.close();
					
				} catch(SQLException e) {
					Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
					e.printStackTrace();
				}
				
				loadPlayersIntoMemoryStore(null);
			}
		}.runTaskAsynchronously(this);
		
	}
	
	private void loadCropRegistry() {
		
		if(verbose_logging) {
			info("Verbose logging is enabled. Verbosely logging crop registry loading.");
		}
		
		
		// loop through the string section
		for(String key: this.getConfig().getConfigurationSection("crops").getKeys(false)) {
			
			FileConfiguration config = this.getConfig(); // shorthand
			
			String base = "crops." + key; // shorthand
			String product = base + ".product"; // shorthand
			
			// retrieve the pertinent values from each crop section
			String name = config.getString(base + ".name");
			String seedName = config.getString(base + ".seed_name");
			Material material = Material.getMaterial(config.getString(base + ".material"));
			
			int growthTime = config.getInt(base + ".growth_time");
			int spread = config.getInt(base + ".spread");
			int baseDrop = config.getInt(base + ".base");
			
			Material productMaterial = Material.getMaterial(config.getString(product + ".material"));
			String productName = config.getString(product + ".name");
			
			int productPrice = config.getInt(product + ".sell_price");
			int xpPer = config.getInt(base + ".xp_per");
			
			double dropChance = config.getDouble(base + ".drop_chance");
			
			ArrayList<String> seasons = new ArrayList<>(config.getStringList(base + ".seasons"));
			
			if(spread < baseDrop) {
				if(verbose_logging) {
					info("Crop " + key + " has an invalid spread configuration. Spread must be higher than the base value. Crop will not be added to the registry");
				}
			} else if(material == null || productMaterial == null) {
				if(verbose_logging) {
					info("Crop " + key + " has an invalid material configuration. Crop will not be added to the registry.");
				}
			} else {
				
				StarCrop toRegister = new StarCrop(name, seedName, material, growthTime, spread, baseDrop, productMaterial, productName, productPrice, xpPer, dropChance, seasons);
				
				this.memstore.addCrop(ChatColor.stripColor(color(name.toLowerCase())), toRegister);
				
				if(verbose_logging) {
					info("The system has successfully registered the crop " + key + " to the crop registry.");
				}
				
			}
			
		}
		
		
	}

}
