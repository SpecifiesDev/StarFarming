package me.csdad.starfarming.Errors.Perks;

public enum PerkDescriptions {
    
    TIER1_GREEN_THUMB("&2&lGreen Thumb~&7Receive &620 &2Farming Fortune&7."),
    TIER1_HEIRLOOM("&4&lHeirloom Oriented~&7Players have a chance to receive more seeds from harvesting grass.~ ~&8Scales with Farming Level."),
    TIER1_EFFICIENT_FARMING("&3&lEfficient Farmer~&7Players receive &320 &2Farming Wisdom&7."),
    
    TIER2_DOCTOR_FARM("&eDoctor&7.&8.&1. &3Farm&7?~&7Receive &a+ &75 points in &aGreen Thumb&7."),
    TIER2_HANDYMAN("&4&lWIP &bHandy Man~&2Farming &7gear receives a 10% stat buff&7."),
    TIER2_POPOP("&3Perks &7on &6Perks &7on &e&lPERKS~&7You may select an additional perk for &6Tier 3&7."),
    
    TIER3_RMF("&6Rich man&7'&6s &4Fervor~&7For every digit in the total amount of money you've made, gain &65 &2Farming Fortune&7."),
    TIER3_WINTER_SOLDIER("&b&lWinter Soldier~&7You may now plant crops that normally are unable to grow inside of winter.~&7Remove &8&l&nmost&7 planting restrictions.~ ~&8Crops that meet this criteria grow &3&l50&7% &8faster and give more &3experience&7."),
    TIER3_INSIDER_TRADING("&4&lWIP &7Insider Trading~&7You negotiate a backroom deal to gain access to a special gear shop&7.");

    public final String description;

    PerkDescriptions(String description) {
        this.description = description;
    }

    public String getTitle() {
        return this.description.split("~")[0]; // Get the title part before '~'
    }

    public String getDescription() {
        return this.description.contains("~") ? this.description.split("~", 2)[1] : ""; // Get the description part after '~'
    }
}
