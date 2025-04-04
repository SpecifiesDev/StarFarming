package me.csdad.starfarming.Errors;

import me.csdad.starfarming.Core;

public enum PlayerMessaging {
	
	
	WRONG_SEASON("&cSorry&7! &cThis crop will not grow in the current season&7."),
	PLANTED_CROP_NO_WATER("&cIt would appear there are no water sources next to the field&7."),
	PLANTED_CROP_NO_OWNERSHIP("&cYou did not plant this crop. You may not harvest it&7."),
	
	SENDER_IS_CONSOLE_ONLY_PLAYER("&cSorry&7, &cyou must be a player to use this command&7."),
	
	PLAYER_LEVELUP_FARMING("&aYou have leveled up to level &6%level% &ain the &2farming &acategory&7."),
	
	NEW_PERK_FARMING_T1("&aYou&7'&ave unlocked new &7Tier One &aperks for &2farming&7."),
	NEW_PERK_FARMING_T2("&aYou&7'&ave unlocked new &eTier Two &aperks for &2farming&7."),
	NEW_PERK_FARMING_T3("&aYou&7'&ave unlocked new &6Tier Three &aperks for &2farming&7.");
	
	public final String message;
	
	PlayerMessaging(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		
		String prefix = Core.getInstance().getConfig().getString("logging.ingame-prefix");
		return prefix + " " + this.message;
	}
	
	// use variable args to make this easier
	public String format(String... args) {
		
		String prefix = Core.getInstance().getConfig().getString("logging.ingame-prefix");
		return prefix + " " + message.replaceAll(args[0], args[1]);
	}

}
