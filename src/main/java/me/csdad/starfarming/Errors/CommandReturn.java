package me.csdad.starfarming.Errors;

import me.csdad.starfarming.Core;

public enum CommandReturn {
	
	LEVELS_INVALID_ARGS("&cUsage&8: &7/&clevel &8<&bview&7/&brequired> &8<&btype&7/&blevel&8>"),
	LEVELS_INVALID_ARGS_REQUIREDXP("&cUsage&8: &7/&clevel required &8<&blevel&8>"),
	
	LEVELS_REQUIREDXP_NFE("&cInvalid level query&7. &cPlease try again with a number&7."),
	LEVELS_REQUIREDXP_GREATER_THAN_10("&cInvalid level query&7. Enter a number from &a1&8-&a10&7."),
	
	LEVELS_INVALID_ARGS_VIEW("&cUsage&8: &7/&clevel view &8<&btype&8>"),
	LEVELS_INVALID_VIEW_QUERY("&cInvalid type inputted&7. &cRefer to the tab completion for acceptable types&7."),
	
	NO_PERMISSION("&cYou do not have permission to utilize this command&7."),
	INVALID_AMOUNT("&cInvalid amount inputted&7. &cAmount must be a number&7."),
	SENDER_CONSOLE("&cSorry&7, &cyou must be a player to use this command&7."),
	
	ADMIN_GIVESEED_INVALID_ARGS("&cUsage&8: &7/&cgiveseed &8<&bplayer&8> <&bseed&8> <&bamount&8>"),
	ADMIN_GIVESEED_INVALID_SEED("&cSorry&7,&c the inputted seed is not registered into the crop registry&7."),
	ADMIN_GIVESEED_OFFLINE_TARGET("&cSorry&7, &cthe target &6%player% &cmust be online&7."),
	ADMIN_GIVESEED_TARGET_RECEIVE("&aYou&7'&ave been given the crop &6%crop%&7."),
	
	ADMIN_SETEXP_INVALID_ARGS("&cUsage&8: &7/&csetexperience &8<&baction&8> <&bplayer&8> <&bamount&8>"),
	ADMIN_SETEXP_INVALID_ACTION("&cYou&7'&cve inputted an invalid action&7. Refer to the tab completion for acceptable actions&7."),
	ADMIN_SETEXP_INVALID_TARGET("&cThe player &6%player% &cwas not found in the database&7."),
	ADMIN_SETEXP_GENERIC("&aYou have %action% &3%experience% &aexperience &a%actionverb% the player &6%player%&7."),
	
	SELLALL_SUCCESS("&aYou have sold your items for a total of &6%coins% &acoins&7."),
	SELLALL_FAIL("&cNone of your inputed items had sell value&7."),
	
	STARPLAYER_DNE("&cSorry&7, we were unable to pull up the player data for player &6%player%&7.");
	
	
	public final String message;
	
	CommandReturn(String message) {
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
