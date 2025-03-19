package me.csdad.starfarming.StarEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.csdad.starfarming.DataStructures.Players.StarPlayer;

public class PlayerLevelUpEvent extends Event {
	
	// handlers for the event
	private static final HandlerList HANDLERS = new HandlerList();
	
	
	// all data that will pass through event
	private final StarPlayer player;
	private final String type;
	
	
	public PlayerLevelUpEvent(StarPlayer player, String type) {
		this.player = player;
		this.type = type;
		
	}
	
	public StarPlayer getStarPlayer() {
		return this.player;
	}
	
	public String getLevelType() {
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
