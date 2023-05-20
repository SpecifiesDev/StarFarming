package me.csdad.StarFarming.Experience;

import me.csdad.StarFarming.Main;

/**
 * Utility class to use our formulas to calcualte xp required for certain levels
 * @author speci
 *
 */
public class ExperienceFormatting {
	

	/**
	 * Method used to get the required xp for an inputted level using the pluin's experience formula
	 * @param level Level to get the xp for
	 * @return Amount of xp required for level
	 */
	public static int getRequiredXpForLevel(int level) {
		
		// grab an instance of our main class to pull config
		Main plugin = Main.getInstance();
		
		// set a retainer value
		int value = 0;
		
		// grab our base-xp and rate of increase
		double base = plugin.getConfig().getDouble("base-xp");
		double roi = plugin.getConfig().getDouble("roi");
		
		// if the player level is 0, we just return the xp required for the base level
		if(level == 0) {
			value = (int) base;
		}
		// else we're going to exponentially increase xp based on formula inputs of roi, level, and base. 
		// refer to wiki on how this formula works
		else {
			double power = Math.pow(roi, level);
			value = (int) (base * power);
		}
		
		// return our retainer
		return value;
		
	}
	
	/**
	 * Method to get a level from a given xp value
	 * @param xp Experience value
	 * @return Related level to the given xp value
	 */
	public static int getLevelFromXp(int xp) {
		
		// create a retainer value
		int level = 0;
		
		// as of writing this, there are only 10 intended levels for the 
		// resource, so we just loop 10 times. I may in the future change
		// this to be up to the server owner, but will have to adjust xp formulas
		// if I do so
		for(int i = 0; i  <= 10;) {
			
			// grab a value required amount of xp for the level we're at in the loop
			int comparator = getRequiredXpForLevel(i);
			
			// if the player xp's is >= required we add to the level value
			if(xp >= comparator) {
				level++;
				i++;
			}
			
		}
		
		// once the loop is done, we have the level that a player is at given an inputted xp value.
		
		return level;
		
		
	}

}
