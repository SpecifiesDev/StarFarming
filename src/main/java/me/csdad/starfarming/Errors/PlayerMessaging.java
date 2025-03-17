package me.csdad.starfarming.Errors;

import me.csdad.starfarming.Core;

public enum PlayerMessaging {
	
	
	WRONG_SEASON("&cSorry&7! &cThis crop will not grow in the current season&7."),
	PLANTED_CROP_NO_WATER("&cIt would appear there are no water sources next to the field&7."),
	PLANTED_CROP_NO_OWNERSHIP("&cYou did not plant this crop. You may not harvest it&7."),
	
	SENDER_IS_CONSOLE_ONLY_PLAYER("&cSorry&7, &cyou must be a player to use this command&7.");
	
	public final String message;
	
	PlayerMessaging(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		
		String prefix = Core.getInstance().getConfig().getString("logging.ingame-prefix");
		return prefix + " " + this.message;
	}

}
