package me.csdad.starfarming.StarEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.csdad.starfarming.DataStructures.Players.StarPlayer;

public class ExperienceGainEvent extends Event {
	
	// handlers for the event
	private static final HandlerList HANDLERS = new HandlerList();
	
	
	// all data that will pass through event
	private final StarPlayer player;
	private final String type;
	
	private int xpBefore;
	private int xpAfter;
	
	
	public ExperienceGainEvent(StarPlayer player, String type, int xpBefore, int xpAfter) {
		this.player = player;
		this.type = type;
		this.xpBefore = xpBefore;
		this.xpAfter = xpAfter;
		
	}
	
	public StarPlayer getStarPlayer() {
		return this.player;
	}
	
	public String getXpType() {
		return this.type;
	}
	
	public int before() {
		return this.xpBefore;
	}
	
	public int after() {
		return this.xpAfter;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
