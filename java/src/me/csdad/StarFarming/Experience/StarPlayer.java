package me.csdad.StarFarming.Experience;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.csdad.StarFarming.Main;

/**
 * Class containing information on a "StarPlayer" including xp, balance, and more.
 * @author speci
 *
 */

public class StarPlayer {
	
	// instance of our main class
    private Main plugin;

    // path to the player's saved file
    private File path;

    // a config instance to access / write data
    private YamlConfiguration config;

    // integer contaning amount of experience player has
    private int farming;

    // integer containing balance of player
    private int starcoins;

    public StarPlayer(Player p) {
        this.plugin = Main.getInstance();
        this.path = new File(this.plugin.getDataFolder(), File.separator + "playerdata" + File.separator + p.getUniqueId().toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.path);
        
        // if the player is new, create a blank save
        if (!this.path.exists()) {
            this.config.set("farming", Integer.valueOf(0));
            this.config.set("starcoins", Integer.valueOf(0));
            try {
                this.config.save(this.path);
                this.farming = 0;
                this.starcoins = 0;
            } catch (Exception err) {
                err.printStackTrace();
            }
        } 
        // else store their data in cache
        else {
            this.farming = this.config.getInt("farming");
            this.starcoins = this.config.getInt("starcoins");
        }
    }
    
    /**
     * Method to save a player's data to their localized yaml file
     * @throws Exception Any issue with saving player's data
     */
    public void save() throws Exception {
        this.config.set("farming", Integer.valueOf(this.farming));
        this.config.set("starcoins", Integer.valueOf(this.starcoins));
        this.config.save(this.path);
    }

    /**
     * Method to get a player's farming XP
     * @return xp
     */
    public int getFarming() {
        return this.farming;
    }

    /**
     * Method to add farming xp to a player's xp bank
     * @param xp Amount to add
     */
    public void addFarming(int xp) {
        this.farming += xp;
    }
    
    /**
     * Method to add coins to a player's coin bank
     * @param coins Coins to add
     */
    public void addStarCoins(int coins) {
        this.starcoins += coins;
    }
    
    /**
     * Method to get a player's coin balance
     * @return balance
     */
    public int getStarCoins() {
        return this.starcoins;
    }
}