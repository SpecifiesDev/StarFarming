package me.csdad.starfarming.Databasing.PersistentCrops;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;


public class BlockSerializer {
	
	
	/**
	 * Method to serialize a block's data into a readable string to then be stored inside of a database.
	 * @param b Block to serialize
	 * @return String formatted world,x,y,z
	 */
	public static String serializeBlock(Block b) {
		return b.getWorld().getName() + "," + b.getX() + "," + b.getY() + "," + b.getZ();
	}
	
	/**
	 * Method to de-serialize a stored block's data back into a block object
	 * @param String data string to de-serialize
	 * @return de-serialized string
	 */
	public static Block deserializeBlock(String data) {
		
		String[] dataChunks = data.split(",");
		
		// make sure there are 4 data points to query
		if(dataChunks.length != 4) return null;
		
		// get the world
		World world = Bukkit.getWorld(dataChunks[0]);
		
		// return null if world is null
		if(world == null) return null;
		
		return world.getBlockAt(Integer.parseInt(dataChunks[1]), Integer.parseInt(dataChunks[2]), Integer.parseInt(dataChunks[3])); // will return null if not found
	}

}
