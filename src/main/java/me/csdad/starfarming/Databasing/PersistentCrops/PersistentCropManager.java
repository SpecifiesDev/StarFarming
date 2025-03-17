package me.csdad.starfarming.Databasing.PersistentCrops;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.DataStructures.PlantedCrop;
import me.csdad.starfarming.DataStructures.StarCrop;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.DatabaseLogging;
import net.md_5.bungee.api.ChatColor;

public class PersistentCropManager {


    // instance of our main class
    private Core plugin;
    public PersistentCropManager() {
        this.plugin = Core.getInstance();
    }

    public void saveAllCrops() {

        try {

            // get a new connection from the connection pool
            Connection conn = plugin.getNewConnection();

            // construct a new statement
            String sql_statement = "INSERT INTO planted_crops (owner_uuid, crop_x, crop_y, crop_z, world_name, crop_id, planted_time) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(crop_x, crop_y, crop_z, world_name) DO UPDATE SET crop_id = excluded.crop_id, planted_time = excluded.planted_time";
            // ^ if block persists in memory, based on our logic it shouldn't be there so we will force update the crop with a new id.

            // create a preparedstatement
            PreparedStatement stmt = conn.prepareStatement(sql_statement);

            // loop over the crop cache
            for (Map.Entry <Block, PlantedCrop> entry: plugin.getMemoryStore().getPlantedCrops().entrySet()) {

                PlantedCrop crop = entry.getValue();
                Block block = crop.getCropBlock();

                // update all of the ? values with their corresponding data
                stmt.setString(1, crop.getOwner().getUUID().toString()); // player's uuid, how we will do a join statement to serialize the starplayer as well
                stmt.setInt(2, block.getX()); // x coord
                stmt.setInt(3, block.getY()); // y coord
                stmt.setInt(4, block.getZ()); // z coord
                stmt.setString(5, block.getWorld().getName()); // world name
                stmt.setString(6, ChatColor.stripColor(plugin.color(crop.getCrop().getName()))); // crop's internal id
                stmt.setString(7, Long.toString(crop.getPlantedTime())); // the time that the crop was planted (unix ts)

                stmt.addBatch(); // add to batch to be executed once done
            }

            stmt.executeBatch();
            
            conn.close(); // return conn to cp

        } catch (SQLException err) {

            Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
            err.printStackTrace();

        }
    }
    
    public void loadAllCrops(boolean verbose_logging) {
    	
    	try {
    		
    		// get a connection from the conn pool
    		Connection conn = this.plugin.getNewConnection();
    		
    		String query = "SELECT owner_uuid, crop_x, crop_y, crop_z, world_name, crop_id, planted_time FROM planted_crops";
    		String delete = "DELETE FROM planted_crops WHERE owner_uuid = ?";
    		
    		PreparedStatement queryStmt = conn.prepareStatement(query);
    		
    		ResultSet rs = queryStmt.executeQuery();
    		
    		while(rs.next()) {
    			PreparedStatement deleteStmt = conn.prepareStatement(delete);
    			
    			// retrieve all of the necessary values from the query
    			String ownerUUID = rs.getString("owner_uuid"); // the uuid of the crop owner
    			int x = rs.getInt("crop_x"); // x coord
    			int y = rs.getInt("crop_y"); // y coord
    			int z = rs.getInt("crop_z"); // z coord
    			String worldName = rs.getString("world_name"); // the world name where the crop exists
    			String cropId = rs.getString("crop_id"); // the id of the crop in order to get a StarCrop object
    			long plantedTime = rs.getLong("planted_time"); // the time the crop was planted
    			
    			World world = Bukkit.getWorld(worldName);
    			if(world == null) {
    				// this isn't a valid block, delete and cont
    				deleteCrop(deleteStmt, ownerUUID);
    				continue;
    				
    			}
    			
    			Block cropBlock = world.getBlockAt(x, y, z);
    			Block field = cropBlock.getRelative(BlockFace.DOWN);
    			
    			BlockData data = cropBlock.getBlockData();
    			
 
    			
    			if(!(data instanceof org.bukkit.block.data.Ageable)) {
    				// crop must be valid and also be an ageable
    				cropBlock.setType(Material.AIR);
    				deleteCrop(deleteStmt, ownerUUID);
    				continue;
    			}
    			
    			StarCrop crop = this.plugin.getMemoryStore().getCrop(cropId.toLowerCase());
    			StarPlayer player = this.plugin.getMemoryStore().getStarPlayer(ownerUUID);
    			
    			

    			
    			if(crop != null && player != null) {
    				
    				
    				PlantedCrop plantedCrop = new PlantedCrop(player, cropBlock, crop, field, plantedTime);
    				this.plugin.getMemoryStore().addPlantedCrop(cropBlock, plantedCrop); // if all checks pass, this block is valid and can be added back into memory
    				
    				if(verbose_logging) Bukkit.getLogger().info("Crop at location [" + x + ", " + y + ", " + z + "] has been loaded back into memory.");
    				
    			} // otherwise just delete the crop
    			else {
    				cropBlock.setType(Material.AIR);
    				deleteCrop(deleteStmt, ownerUUID);
    			}
    			
    			// why are we force deleting if a failure occurs? Simple.
    			// this is designed to be one-cycle persistent. On reload or close, its saved, and on enable, it's re-rendered. If it can't be 
    			// re-rendered for whatever purpose it needs to be deleted to prevent issues.
    			
    			
    		}
    		
    		
    	} catch(SQLException err) {
    		
    		err.printStackTrace();
    		Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
    		
    	}
    	
    }
    
    private void deleteCrop(PreparedStatement stmt, String uuid) throws SQLException {
    	
    	stmt.setString(1, uuid);
    	
    	stmt.executeUpdate();
    	

    	
    }


}