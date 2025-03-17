package me.csdad.starfarming.Testing;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.csdad.starfarming.StarEvents.ExperienceGainEvent;
import me.csdad.starfarming.StarEvents.StarCropHarvestEvent;

public class TestCustomEvents implements Listener {
	
	@EventHandler
	public void testXPGain(ExperienceGainEvent e) {
		
		System.out.println(e.getXpGained());
		System.out.println(e.getStarPlayer().getExperience());
		System.out.println(e.getBukkitPlayer().getName());
		
	}
	
	@EventHandler
	public void testHarvest(StarCropHarvestEvent e) {
		
		System.out.println(e.getCrop().getName());
		System.out.println(e.getStarPlayer().getName());
		System.out.println(e.getBukkitPlayer().getUniqueId().toString());
		
	}

}
