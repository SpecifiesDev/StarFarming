package me.csdad.starfarming.Utility;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class StringFormatting {
	
	
	public static String formatNumber(int format) {
		
		return new DecimalFormat("#,###").format(format);
		
	}
	
	public static String setFirstUppercase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	/**
	 * Method to generate a percentage bar for experience
	 * @param needed The amount of xp needed for next level
	 * @param progress The progress towards the next level
	 * @param barLength The length of the bar
	 * @return
	 */
	public static String createPercentageBar(double needed, double progress, int barLength, char barChar) {
		
		int percent = (int) Math.round((progress / needed) * 100);
		
		
		StringBuilder sb = new StringBuilder();
		
		int filledLength = Math.max(1, (int) Math.round((percent / 100.0) * barLength));
		
	
		
		for(int i = 0; i < barLength; i++) {
			
			if(i < filledLength) {
				sb.append(ChatColor.translateAlternateColorCodes('&', "&a&l" + barChar));
			} else {
				sb.append(ChatColor.translateAlternateColorCodes('&', "&7&l" + barChar));
			}
			
		}
		
		return sb.toString();
	}
	
	public static void sendExperienceActionBar(int experience, Player p, String type) {
		
		String construct = ChatColor.translateAlternateColorCodes('&', "&8+&3 " + experience + " " + type + " XP");
		
		TextComponent message = new TextComponent(construct);
		
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);

	}
	
	public static JSONObject parseJSONString(String jsonString) {
		
		try {
			return new JSONObject(jsonString);
		} catch(JSONException e) {
			return null;
		}
		
	}
	
	public static String formatSeconds(int totalSeconds) {
		
	    int hours = totalSeconds / 3600;
	    int minutes = (totalSeconds % 3600) / 60;
	    int seconds = totalSeconds % 60;

	    // Format hours, minutes, and seconds with leading zeros if needed
	    String formattedHours = (hours < 10) ? "0" + hours : String.valueOf(hours);
	    String formattedMinutes = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
	    String formattedSeconds = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);

	    // Return the formatted string with color codes
	    return "&c" + formattedHours + "&7:&c" + formattedMinutes + "&7:&c" + formattedSeconds;
	}
	
		

}
