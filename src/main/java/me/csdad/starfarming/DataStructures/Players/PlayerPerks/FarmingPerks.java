package me.csdad.starfarming.DataStructures.Players.PlayerPerks;

import java.util.HashMap;
import java.util.Map;

public class FarmingPerks extends Perks {
	
	// Maps to store perk options and selections
	private final Map<Integer, Integer[]> perkOptions = new HashMap<>();
	private final Map<Integer, Integer> perkSelection = new HashMap<>();
	
	private int level;
	
	public FarmingPerks(
		int perk1, int perk1Opt, int perk1Opt2, int perk1Selected,
		int perk2, int perk2Opt, int perk2Opt2, int perk2Selected,
		int perk3, int perk3Opt, int perk3Opt2, int perk3Selected, int level) {
		
		// place the options
		perkOptions.put(1, new Integer[] {perk1, perk1Opt, perk1Opt2});
		perkOptions.put(2, new Integer[] {perk2, perk2Opt, perk2Opt2});
		perkOptions.put(3, new Integer[] {perk3, perk3Opt, perk3Opt2});
		
		perkSelection.put(1, perk1Selected);
		perkSelection.put(2, perk2Selected);
		perkSelection.put(3, perk3Selected);
		
		this.level = level;
		
	}
	
	@Override
	public boolean getPerkSelected(int perk) throws InvalidPerkRange {
		
		if(perk > 3 || perk < 1) {
			throw new InvalidPerkRange("Perk is out of range");
		}
		
		return (perkSelection.get(perk) == 1) ? true : false;
		
	}
	
	@Override
	public int getSelectedPerk(int perk) throws InvalidPerkRange {

		if(perk > 3 || perk < 1) {
			throw new InvalidPerkRange("Perk is out of range");
		}
		
		Integer[] list = perkOptions.get(perk);
		
		int count = 1;
		
		for(int i = 0; i < list.length; i++) {
			
			if(list[i] == 1) {
				break;
			}
			count++;
		}
		
		return count;
		
		
		
	}
	
	
	public int getFarmingLevel(int level) {
		return this.level;
	}
	
	public void incrementLevel() {
		this.level++;
	}
	
	public void decrementLevel() {
		this.level--;
	}
	
	
	
}
