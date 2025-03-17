package me.csdad.starfarming.StarEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.csdad.starfarming.DataStructures.StarCrop;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;

public class StarCropHarvestEvent extends Event {
	
	// handlers for the event
	private static final HandlerList HANDLERS = new HandlerList();
	
	
	// all data that will pass through event
	private final StarPlayer player;
	private final Player bukkitPlayer;
	private final StarCrop crop;
	
	
	public StarCropHarvestEvent(StarPlayer player, Player bukkitPlayer, StarCrop crop) {
		this.player = player;
		this.bukkitPlayer = bukkitPlayer;
		this.crop = crop;
	}
	
	public StarPlayer getStarPlayer() {
		return this.player;
	}
	
	public Player getBukkitPlayer() {
		return this.bukkitPlayer;
	}
	
	public StarCrop getCrop() {
		return this.crop;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
