package me.csdad.starfarming.Events.LevelEvents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.Commands.Experience.ExperienceFormatting;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;
import me.csdad.starfarming.Errors.PlayerMessaging;
import me.csdad.starfarming.StarEvents.ExperienceGainEvent;
import me.csdad.starfarming.StarEvents.PlayerLevelUpEvent;

public class PlayerExperienceGain implements Listener {
	
	// instance of main class
	private Core plugin;
	
	public PlayerExperienceGain() {
	
		this.plugin = Core.getInstance();
		
	}

	
	/**
	 * This method handles player experience gain, from all sources including setexperience command
	 */
	@EventHandler
	public void xpgain(ExperienceGainEvent e) {
		
		int levelBefore = ExperienceFormatting.getLevelFromExperience(e.before());
		int levelAfter = ExperienceFormatting.getLevelFromExperience(e.after());
		
		

		
		if(levelBefore < levelAfter) {
			
			e.getStarPlayer().getFarmingPerks().setFarmingLevel(levelAfter);
			
			PlayerLevelUpEvent LevelUp = new PlayerLevelUpEvent(e.getStarPlayer(), "farming");
			Bukkit.getPluginManager().callEvent(LevelUp);
			
		} else if(levelAfter < levelBefore) {
			
			// set the internal level down
			e.getStarPlayer().getFarmingPerks().setFarmingLevel(levelAfter);
			
			// no need to call level up is this really is only an edge case for the /setexp command.
			
		}
				
	}
	
	/*
	 * This method will handle messaging a player that they've leveled up
	 */
	@EventHandler
	public void onlineLevelMessage(PlayerLevelUpEvent e) {
		
		StarPlayer player = e.getStarPlayer();
		String type = e.getLevelType();
		
		// try to get an online player
		Player p = Bukkit.getPlayerExact(player.getName());
		
		// if the retrieved player is null, don't continue
		if(p == null) return;
		
		
		
		if(type.equals("farming") ) {
			
			p.sendMessage(this.plugin.color(PlayerMessaging.PLAYER_LEVELUP_FARMING.format("%level%", Integer.toString(player.getFarmingPerks().getFarmingLevel()))));
			
			
			int level = player.getFarmingPerks().getFarmingLevel();
			
			if(level == 3 && level < 5) {	
				
				p.sendMessage(this.plugin.color(PlayerMessaging.NEW_PERK_FARMING_T1.getMessage()));
				
			} else if(level == 5 && level < 10) {
				
				p.sendMessage(this.plugin.color(PlayerMessaging.NEW_PERK_FARMING_T2.getMessage()));
				
			} else if(level == 10) {
				
				p.sendMessage(this.plugin.color(PlayerMessaging.NEW_PERK_FARMING_T3.getMessage()));
				
			}
			
		}
		
		
	}
	
	
	

}
