package me.csdad.starfarming.Commands.Experience;

import me.csdad.starfarming.Core;

public class ExperienceFormatting {
	
	
	/**
	 * Method to get the required experience for a level
	 * Uses a logistic decay function to limit the rate
	 * @param level The target level
	 * @return adjustedXp
	 */
	public static int getExperienceForLevel(int level) {
		
		// get the main instance in order to pull configuration values
		Core plugin = Core.getInstance();
		
		double base = plugin.getConfig().getDouble("general.experience.base"); // base xp required for level 0
		double roi = plugin.getConfig().getDouble("general.experience.roi"); // exponential function's rate of increase
		
		// if the requested level is 0, just return the floor int
		if(level == 0) return (int) base;
		
		// calculate the value for the given level
		double rawValue = base * Math.pow(roi, level);
		

		
		// floor and return
		return (int) rawValue;
	}

	/**
	 * Method to retrieve a level given experience.
	 * Uses a BST to find the level, as its hard to do accurately via algebra due to the
	 * amount of flooring that occurs from A->B
	 * @param experience The experience to query
	 * @return level
	 */
	public static int getLevelFromExperience(int experience) {
		
		Core plugin = Core.getInstance();
		
		double base = plugin.getConfig().getDouble("general.experience.base"); // base xp required for level 0
		
		// edge case
		if(experience < base) {
			return 0;
		}
		
		// use a binary search tree to find the level
		// faster than iterating, has a TC of O(logn)
		int low = 0, high = 100;
		while(low < high) {
			int mid = (low + high + 1) / 2;
			int requiredExperience = getExperienceForLevel(mid);
			
			if(requiredExperience <= experience) low = mid; // it's above, move up
			else high = mid - 1; // move down
			// repeat until the level is found
		}
		
		return low;
		
	}
}
