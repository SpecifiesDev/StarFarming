package me.csdad.starfarming.DataStructures;

import org.bukkit.block.Block;

import me.csdad.starfarming.DataStructures.Players.StarPlayer;

public class CropInfo {
	
	// the starplayer
	private StarPlayer player;
	
	// the actual crop
	private StarCrop crop;
	
	// the block to query the planted crop for its growth time left
	private Block block;
	
	public CropInfo(StarPlayer player, StarCrop crop, Block block) {
		
		this.player = player;
		this.crop = crop;
		this.block = block;
		
	}
	
	/**
	 * Method to get the starplayer
	 * @return player
	 */
	public StarPlayer getPlayer() {
		return this.player;
	}
	
	/**
	 * Method to get the crop
	 * @return crop
	 */
	public StarCrop getCrop() {
		return this.crop;
	}
	
	/**
	 * Method to get the planted block for further querying
	 * @return block
	 */
	public Block getPlantedBlock() {
		return this.block;
	}

}
