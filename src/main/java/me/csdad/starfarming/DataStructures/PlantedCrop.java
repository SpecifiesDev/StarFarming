package me.csdad.starfarming.DataStructures;

import org.bukkit.Material;
import org.bukkit.block.Block;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;

public class PlantedCrop {
	
	// instance of the main class
	private Core plugin;
	
	// the player that planted the crop
	/**
	 * Self note for v2.0.5 planned features.
	 * Will be adding a team functionality. Current thoughts to achieving this:
	 * - add inTeam and Team value to player database
	 * - add team database
	 * - when a player tries to harvest, a check will be ran checking if the harvester is in the team with the planter
	 * - additional features planned like shared xp, team level, team perks, and more.
	 * 
	 * Also, if you're refering to gh/SpecDev/SF, you'll note that in previous versions this used a Player object.
	 * This is being changed to support functionality for retroactive serialization and deserialization of crops. 
	 * This will allow for crops to be rendered and de-rendered on server start/stop or reload. Impossible with a Player object,
	 * as most players will be offline at the time of startup or reload. OfflinePlayer is also deprecated and will lose support
	 * in later versions of spigot's API.
	 */
	private StarPlayer owner;
	
	
	
	// the block of the crop
	private Block cropBlock;
	
	// the block below the crop (field or dirt)
	private Block field;
	
	// the actual data of the planted crop so we can pulled info like seeds, growth time and more
	private StarCrop crop;
	
	// this is the time in a unix ts format that the crop was planted in
	private long plantedTime;
	
	public PlantedCrop(StarPlayer owner, Block cropBlock, StarCrop crop, Block field, long plantedTime) {
		
		this.owner = owner;
		this.cropBlock = cropBlock;
		this.crop = crop;
		this.field = field;
		this.plantedTime = plantedTime;
		
		this.plugin = Core.getInstance();
		
	}
	
	/**
	 * Method to retrieve if a planted crop is ready for harvest
	 * @return boolean
	 */
	public boolean canHarvest() {
		
		
		return (getTimeElapsed(System.currentTimeMillis()) >= crop.getGrowthTime());
		
	}
	
	/**
	 * Method used to return a formatted string of time left until the crop reaches maturity
	 * @return Formatted string
	 */
	public String getTimeLeftFormatted() {
		
	    long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
	    long plantedTime = this.plantedTime / 1000; // Convert to seconds
	    long growthDuration = crop.getGrowthTime(); // In seconds
		
	    int timeLeft =  (int) ((plantedTime + growthDuration) - currentTime);
	    
		int hours = timeLeft / 3600;
		int minutes = (timeLeft % 3600) / 60;
		int seconds = timeLeft % 60;
		
		return "&e" + (hours >= 10 ? hours : "0" + hours) + "&7:&e" + (minutes >= 10 ? minutes : "0" + minutes) + "&7:&e" + (seconds >= 10 ? seconds : "0" + seconds); 
		
	}
	
	/**
	 * Method used to harvest a crop.
	 * Deletes the crop's top block from the world, and removes the crop from the memory store
	 */
	public void harvestCrop() {
		this.plugin.getMemoryStore().getPlantedCrops().remove(this.cropBlock);
		this.cropBlock.setType(Material.AIR);
	}
	
	/**
	 * Method to get the owner of the crop
	 * @return owner
	 */
	public StarPlayer getOwner() {
		return this.owner;
	}
	
	/**
	 * Method to return the crop block of the planted crop
	 * @return cropBlock
	 */
	public Block getCropBlock() {
		return this.cropBlock;
	}
	
	/**
	 * Method to get the planted crop's registry for crop information
	 * @return crop
	 */
	public StarCrop getCrop() {
		return this.crop;
	}
	
	/**
	 * Method to return the block below the planted crop
	 * @return field
	 */
	public Block getField() {
		return this.field;
	}
	
	/**
	 * Method to get the planted time of the crop
	 * @return plantedTime
	 */
	public long getPlantedTime() {
		return this.plantedTime;
	}
	
	
	private long getTimeElapsed(long now) {
		return (now - this.plantedTime) / 1000;
	}

}
