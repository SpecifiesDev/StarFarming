package me.csdad.StarFarming.Crops;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.csdad.StarFarming.Main;

public class PlantedCrop {
	
	// instance of our main class
	private Main plugin;
	
	// the player that planted the crop
	// side note: maybe create some type of "team" system in the future
	// where players can join teams, allowing them to harvest each other's crops
	private Player owner;
	
	// the block of the actual crop 
	private Block cropBlock;
	
	// the block below the crop (field or dirt)
	private Block field;
	
	// the actual data of the planted crop so we can pull information like seeds, growth time, etcetera
	private Crop crop;
	
	// this is the time in a unix ts format that the crop was planted in. we use this to determine the harvestability 
	// of a crop 
	private long plantedTime;
	
	public PlantedCrop(Player owner, Block cropBlock, Crop crop, Block field, long plantedTime) {
		this.owner = owner;
		this.cropBlock = cropBlock;
		this.crop = crop;
		this.field = field;
		this.plantedTime = plantedTime;
		
		this.plugin = Main.getInstance();
	}
	
	/**
	 * Method used to tell external classes whether a crop meets the criteria to be harvested.
	 * @return boolean can harvest / not
	 */
	public boolean canHarvest() {
		
		if(getTimeElapsed(System.currentTimeMillis()) >= crop.getGrowthTime()) {
			return true;
		} else return false;
		
	}
	
	/**
	 * Method used to return a formatted string of time left until crop reaches maturity
	 * @return Formatted string
	 */
	public String getTimeLeftFormatted() {
		int timeLeft = (int) (crop.getGrowthTime() - (System.currentTimeMillis()));
		int hours = timeLeft / 3600;
		int secondsLeft = timeLeft - hours * 3600;
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft - minutes * 60;
		
		return "&e" + hours + " &ahours&7, &e" + minutes + " &aminutes&7, &aand &e" + seconds + " &aseconds left&7.";
		
	}
	
	/**
	 * Method used to harvest a crop, deleting the block from the world and removing cached data of it
	 */
	public void harvestCrop() {
		this.plugin.getPlantedCrops().remove(this.cropBlock);
		this.cropBlock.setType(Material.AIR);
	}
	
	public Player getOwner() {
		return this.owner;
	}
	
	public Block getCropBlock() {
		return this.cropBlock;
	}
	
	public Crop getCrop() {
		return this.crop;
	}
	
	public Block getField() {
		return this.field;
	}
	
	public long getPlantedTime() {
		return plantedTime;
	}
	
	private long getTimeElapsed(long now) {
		return (now - this.plantedTime) * 1000;
	}


}
