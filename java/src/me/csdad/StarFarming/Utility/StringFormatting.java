package me.csdad.StarFarming.Utility;

import java.text.DecimalFormat;

public class StringFormatting {
	
	/**
	 * Utility method to format a given amount of seconds into HH:MM:SS
	 * @param secs amount of seconds to format
	 * @return formatted string
	 */
	public static String formatSeconds(int secs) {
	    int hours = secs / 3600;
	    int secondsLeft = secs - hours * 3600;
	    int minutes = secondsLeft / 60;
	    int seconds = secondsLeft - minutes * 60;
	    String formattedTime = "&c";
	    if (hours > 9 && hours < 25) {
	      formattedTime = formattedTime + formattedTime + "&7:&c";
	    } else {
	      formattedTime = formattedTime + "&c0";
	      formattedTime = formattedTime + formattedTime + "&7:&c";
	    } 
	    if (minutes > 9 && minutes < 61) {
	      formattedTime = formattedTime + formattedTime + "&7:&c";
	    } else {
	      formattedTime = formattedTime + "&c0";
	      formattedTime = formattedTime + formattedTime + "&7:&c";
	    } 
	    if (seconds > 9 && seconds < 61) {
	      formattedTime = formattedTime + formattedTime;
	    } else {
	      formattedTime = formattedTime + "&c0";
	      formattedTime = formattedTime + formattedTime;
	    } 
	    return formattedTime;
	}
	
	/**
	 * Utility method to format a number in the format ###,###
	 * @param format number to format
	 * @return formatted number
	 */
	public static String formatNumber(int format) {
		
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(format);
		
	}

}
