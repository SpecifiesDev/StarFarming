package me.csdad.starfarming.Mapping;

import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageRenderer extends MapRenderer {
	
	private final BufferedImage image;
	private boolean rendered = false; // prevent re-rendering the map every tick, as we only need to render once.
	
	public ImageRenderer(BufferedImage image) {
		this.image = image;
	}
	
	
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		
		if(!rendered && image != null) {
			
			canvas.drawImage(0, 0, MapPalette.resizeImage(image));
			
			//rendered = true;
			
		}
		
	}

}
