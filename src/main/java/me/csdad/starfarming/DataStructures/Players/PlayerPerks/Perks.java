package me.csdad.starfarming.DataStructures.Players.PlayerPerks;

public abstract class Perks {
	
	// abstract method to check if a perk is selected
	public abstract boolean getPerkSelected(int perk);
	
	// abstract method to get the selected perk as a string
	public abstract int getSelectedPerk(int perk);

	// method to get a selected perk for an inputted tier
	public abstract int getSelectedPerkForTier(int tier);
	
	// method to set a new perk for tier
	public abstract void setSelectedPerkForTier(int tier, int selectedPerk);
}
