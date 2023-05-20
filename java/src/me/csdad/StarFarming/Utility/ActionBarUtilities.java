package me.csdad.StarFarming.Utility;

import org.bukkit.ChatColor;

/**
 * General class used to store methods used for formatting strings for ABars or
 * abar related commands
 * @author speci
 *
 */
public class ActionBarUtilities {
	
	/**
	 * Method to return a percentage bar string given an end number and progress number.
	 * @param endProgress The end number of the bar
	 * @param currentProgress The progress of the bar
	 * @param barLength How many characters inside of the bar
	 * @return constructed String
	 */
    public static String createPercentageBar(
        double endProgress, double currentProgress, int barLength) {
        double progress = currentProgress / endProgress;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < barLength * progress) {
                sb.append(ChatColor.translateAlternateColorCodes('&', "&a&l:"));
            } else {
                sb.append(ChatColor.translateAlternateColorCodes('&', "&7&l:"));
            }
        }
        return sb.toString();
    }
}
