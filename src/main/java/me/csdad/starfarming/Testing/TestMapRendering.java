package me.csdad.starfarming.Testing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.Mapping.ImageRenderer;


public class TestMapRendering implements CommandExecutor {
	
	private Core plugin;
	
	public TestMapRendering() {
		this.plugin = Core.getInstance();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			// we will just render to a 9 square grid for testing purposes
			try {
				BufferedImage image = ImageIO.read(new File(plugin.getDataFolder(), "/test.png"));
				
				// each section of the image will be set to 128x128
				int sectionSize = 128;
				
				// create a 3x3 inventory to show to the player
				Inventory inventory = Bukkit.createInventory(null, 9, "Image Testing");
				
				
				// loop through the 3x3 grid
				for(int row = 0; row < 3; row++) {
					for(int col = 0; col < 3; col++) {
						// determine the starting x/y cords
						int x = col * sectionSize;
						int y = row * sectionSize;
						
						// get the section
						BufferedImage section = image.getSubimage(x, y, sectionSize, sectionSize);
						
						
						
						// create a new map for this section
						MapView mapView = Bukkit.createMap(player.getWorld());
						mapView.getRenderers().clear();
						mapView.addRenderer(new ImageRenderer(section));
						mapView.setTrackingPosition(false);
						mapView.setUnlimitedTracking(false);
						mapView.setScale(MapView.Scale.FARTHEST);
						
						// create a map item to place in inv
						ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
						MapMeta meta = (MapMeta) mapItem.getItemMeta();
						
						meta.setMapView(mapView);
						mapItem.setItemMeta(meta);
						
						// place in the appropriate grid slot
						inventory.setItem(row * 3 + col, mapItem);
						
						
					}
				}
				
				player.openInventory(inventory);
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return true;
	}
	


}
