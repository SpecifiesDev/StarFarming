package me.csdad.starfarming.StarEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.csdad.starfarming.DataStructures.Players.StarPlayer;

public class ExperienceGainEvent extends Event {
	
	// handlers for the event
	private static final HandlerList HANDLERS = new HandlerList();
	
	
	// all data that will pass through event
	private final int xpGained;
	private final Player bukkitPlayer;
	private final StarPlayer player;
	private final String type;
	
	
	public ExperienceGainEvent(StarPlayer player, Player bukkitPlayer, int xpGained, String type) {
		this.player = player;
		this.bukkitPlayer = bukkitPlayer;
		this.xpGained = xpGained;
		this.type = type;
		
	}
	
	public StarPlayer getStarPlayer() {
		return this.player;
	}
	
	public Player getBukkitPlayer() {
		return this.bukkitPlayer;
	}
	
	public int getXpGained() {
		return this.xpGained;
	}
	
	public String getXpType() {
		return this.type;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
