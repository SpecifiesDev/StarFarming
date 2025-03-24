package me.csdad.starfarming.DataStructures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.GeneralLogging;


public class MemoryStore {
	
	private HashMap<String, StarPlayer> playerData = new HashMap<>();
	
	// crops, and plants
	private HashMap<Block, PlantedCrop> plantedCrops = new HashMap<>();
	
	// registered crops (loaded from config)
	private HashMap<String, StarCrop> registeredCrops = new HashMap<>();
	
	// players inside of the crop info screen, used to iterate mainly the time left
	private HashMap<Player, CropInfo> cropInfoCache = new HashMap<>();
	
	// CALL AFTER SAVING CACHES
	public void clearStore() {
		
		// we can just reset playerData, plantedCrops, registeredCrops (planted crops will eventually need to be serializable)
		playerData = new HashMap<>();
		plantedCrops = new HashMap<>();
		registeredCrops = new HashMap<>();
		
		// for the crop information screen cache, we need to loop over remaining players online and take them out of the inventory.
		// this is because our event that prevents interaction relies on the player being in the memory store, an edge case
		// that would be impossible after a reload
		for(Map.Entry<Player, CropInfo> entry : cropInfoCache.entrySet()) {
			
			entry.getKey().closeInventory();
			
		}
		
		cropInfoCache = new HashMap<>();
		
	}
	
	// current season
	private String season = "SPRING";
	
	/**
	 * Method to set the hashmap of players
	 * @param players The bnew hashmap
	 */
	public void setPlayers(HashMap<String, StarPlayer> players) {
		this.playerData = players;
	}
	
	/**
	 * Method to add a player and crop information object to the memory store
	 * @param p Player object to add - key
	 * @param crop Crop info object to add - value
	 */
	public void addCropInfo(Player p, CropInfo crop) {
		this.cropInfoCache.put(p, crop);
	}
	
	/**
	 * Method to get the crop info for update purposes
	 * @param p Player to query
	 * @return cropInfo
	 */
	public CropInfo getCropInfo(Player p) {
		return this.cropInfoCache.get(p);
	}
	
	/**
	 * Method to remove a player from the crop info cache
	 * @param p Player to remove
	 */
	public void removeCropInfo(Player p) {
		this.cropInfoCache.remove(p);
	}
	
	/**
	 * Method to retrieve all crop info
	 * @return cropInfoCache
	 */
	public HashMap<Player, CropInfo> getAllCropInfo() {
		return this.cropInfoCache;
	}
	
	/**
	 * Method to add a star player to the player memory store.
	 * @param p StarPlayer object to add - value
	 * @param uuid String object to add - key
	 */
	public void addStarPlayer(StarPlayer p, String uuid) {
		this.playerData.put(uuid, p);
	}
	
	/**
	 * Method to get a star player object by name
	 * @param name The name of the player
	 * @return StarPlayer OR null value
	 */
	public StarPlayer getStarPlayerByName(String name) {
		return this.playerData.values().stream() // stream the data set
				.filter(player -> player.getName().equalsIgnoreCase(name)) // predicate that input = name
				.findFirst() // return the object if found
				.orElse(null); // or else return null
	}
	
	/**
	 * Method to get a star player from the memory store.
	 * @param uuid String object to query - key
	 * @return StarPlayer OR null value
	 */
	public StarPlayer getStarPlayer(String uuid) {
		return this.playerData.get(uuid);
	}
	
	/**
	 * Method to get all star players
	 * @return HashMap<String, StarPlayer>
	 */
	public HashMap<String, StarPlayer> getAllStarPlayers() {
		return this.playerData;
	}
	
	/**
	 * Method to remove a star player from the memory store
	 * @param uuid String object of the player to remove
	 */
	public void removeStarPlayer(String uuid) {
		this.playerData.remove(uuid);
	}
	
	/**
	 * Method to get all planted crops 
	 * @return HashMap<Block, PlantedCrop>
	 */
	public HashMap<Block, PlantedCrop> getPlantedCrops() {
		return this.plantedCrops;
	}
	
	/**
	 * Method to get a planted crop from the memory store
	 * @param block Block object to query - key 
	 * @return PlantedCrop OR null
	 */
	public PlantedCrop getPlantedCrop(Block block) {
		return this.plantedCrops.get(block);
	}
	
	/**
	 * Method to remove a planted crop from the memory store
	 * @param block Block object of the crop to remove
	 */
	public void removePlantedCrop(Block block) {
		this.plantedCrops.remove(block);
	}
	
	/**
	 * Method to add a planted crop to the memory store
	 * @param block Block object to add - key
	 * @param crop PlantedCrop object to add - value
	 */
	public void addPlantedCrop(Block block, PlantedCrop crop) {
		this.plantedCrops.put(block, crop);
	}
	
	/**
	 * Method to retrieve a crop from the registry
	 * @param cropId The ID of the registered crop
	 * @return StarCrop OR null
	 */
	public StarCrop getCrop(String cropId) {
		return this.registeredCrops.get(cropId);
	}
	
	/**
	 * Method to add a crop to the registry
	 * (Note that there is no removal method. This is a static dataset loaded on startup)
	 * @param cropId String object of the crop in the registry - key
	 * @param crop StarCrop object of the crop - value
	 */
	public void addCrop(String cropId, StarCrop crop) {
		this.registeredCrops.put(cropId, crop);
	}
	
	/**
	 * Method to retrieve all crops from the registry
	 * @return HashMap<String, StarCrop>
	 */
	public HashMap<String, StarCrop> getCrops() {
		return this.registeredCrops;
	}
	
	/**
	 * Method to set the season
	 * @param season String object to set the season too
	 * @throws IllegalArgumentException if the configured season is not in the allowed list of seasons.
	 */
	public void setSeason(String season) throws IllegalArgumentException {
		
		List<String> allowList =  Arrays.asList(new String[]{ "SPRING", "SUMMER", "FALL", "WINTER"});
		
		
		if(!allowList.contains(season)) {
			// we do this to prevent an invalid season from being configured, which will cause later errors in the plugin
			// if we're expecting this allowlist but receive an invalid value
			throw new IllegalArgumentException(GeneralLogging.INVALID_SEASON.toString());
		}
		

		this.season = season;
	}
	
	/**
	 * Method to retrieve the current season
	 * @return season
	 */
	public String getSeason() {
		return this.season;
	}
	

}
