package me.csdad.StarFarming.Utility;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.csdad.StarFarming.Main;
import me.csdad.StarFarming.Crops.PlantedCrop;
import me.csdad.StarFarming.Experience.ExperienceFormatting;
import me.csdad.StarFarming.Experience.StarPlayer;

/**
 * Utility class used to manage season ticks
 * Also dually functions to serve as the server's scoreboard broadcast
 * @author speci
 *
 */
public class SeasonManager {

    // instance of our main class
    private Main plugin;

    // store and instance of both our scoreboard & season cycles to disable them when needed
    private BukkitRunnable seasonCycle;
    private BukkitRunnable scoreboardCycle;

    // grab the amount of minutes a season is supposed to last from config, multiply by 60
    // to convert to seconds
    int secondRotation = this.plugin.getConfig().getInt("season-cycle") * 60;

    /**
     * Method to begin the season countdown, creates a new runnable that runs every
     * 20 in-game ticks
     */
    public void startSeasonCycle() {

        this.seasonCycle = new BukkitRunnable() {
        	
            public void run() {
            	
            	// first remove a second from the time left
                SeasonManager.this.secondRotation--;
                
                // check if the time is complete
                // if so, set appropriate season, loop over planted crops
                // to wilt them, and finally set the internal season data
                
                // this new method should optimize this process significantly
                // the old method checked for wilted crops every 100 ticks, whereas
                // this one is only going to check once every season cycle.
                if (SeasonManager.this.secondRotation <= 0) {
                    SeasonManager.this.secondRotation = SeasonManager.this.plugin.getConfig().getInt("season-cycle") * 60;
                    
                    if (SeasonManager.this.plugin.getSeason().equals("SPRING")) {
                    	
                        SeasonManager.this.plugin.setSeason("SUMMER");
                        loopOverPlantedCrops(SeasonManager.this.plugin.getPlantedCrops(), "SUMMER");
                        
                    } else if (SeasonManager.this.plugin.getSeason().equals("SUMMER")) {
                    	
                        SeasonManager.this.plugin.setSeason("FALL");
                        loopOverPlantedCrops(SeasonManager.this.plugin.getPlantedCrops(), "FALL");
                        
                    } else if (SeasonManager.this.plugin.getSeason().equals("FALL")) {
                    	
                        SeasonManager.this.plugin.setSeason("WINTER");
                        loopOverPlantedCrops(SeasonManager.this.plugin.getPlantedCrops(), "WINTER");
                        
                    } else if (SeasonManager.this.plugin.getSeason().equals("WINTER")) {
                    	
                        SeasonManager.this.plugin.setSeason("SPRING");
                        loopOverPlantedCrops(SeasonManager.this.plugin.getPlantedCrops(), "SPRING");
                        
                    }
                }
            }
        };
        
        this.seasonCycle.runTaskTimerAsynchronously((Plugin) this.plugin, 5L, 20L);

    }
    
    /**
     * Method used to start our scoreboard cycle
     */
    public void startScoreBoardCycle() {
    	
    	this.scoreboardCycle = new BukkitRunnable() {
    		
    		@SuppressWarnings("deprecation")
			public void run() {
    			
    			// first we're going to create a blank scoreboard manager, and blank board
    			ScoreboardManager manager = Bukkit.getScoreboardManager();
    			Scoreboard board = manager.getNewScoreboard();
    			
    			// create a blank objective
    			Objective objective = board.registerNewObjective("speci", "sfrm", "main");
    			
    			// set our obj's display name and slot position
    			objective.setDisplayName(SeasonManager.this.plugin.color(SeasonManager.this.plugin.getConfig().getString("scoreboard-title")));
    			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    			
    			// create individual blank lines for each scoreboard entry
    	        Score blank = objective.getScore(SeasonManager.this.plugin.color("&c&m"));
    	        Score levelBlank = objective.getScore(SeasonManager.this.plugin.color("&7&m"));
    	        Score balanceBlank = objective.getScore(SeasonManager.this.plugin.color("&4&m"));
    	        
    	        // create our line headers
    	        Score currentSeason = objective.getScore(SeasonManager.this.plugin.color("&7Current Season"));
    	        Score timeLeftHeader = objective.getScore(SeasonManager.this.plugin.color("&7Next Season"));
    	        Score levelPercent = objective.getScore(SeasonManager.this.plugin.color("&7Level Progress"));
    	        Score balance = objective.getScore(SeasonManager.this.plugin.color("&7Star Coins"));
    	        
    	        // create score lines out of static data that isn't player related
    	        Score season = objective.getScore(SeasonManager.this.plugin.color("&b" + SeasonManager.this.plugin.getSeason()));
    	        Score timeLeft = objective.getScore(SeasonManager.this.plugin.color(StringFormatting.formatSeconds(SeasonManager.this.secondRotation)));
    	        
    	        // go ahead and set our static lines to their appropriate location
    	        currentSeason.setScore(11);
    	        season.setScore(10);
    	        blank.setScore(9);
    	        timeLeftHeader.setScore(8);
    	        timeLeft.setScore(7);
    	        levelBlank.setScore(6);
    	        
    	        // now we're going to loop over every online player
    	        // we do this to 1.) add the appropriate values to our scoreboard
    	        // and to dynamically update the scoreboard every tick of this thread
    	        for(Player p : Bukkit.getOnlinePlayers()) {
    	        	
    	        	// grab an instance of our player's data
    	        	StarPlayer player = SeasonManager.this.plugin.getStarPlayer(p);
    	        	
    	        	// add a null check, if the player is
    	        	// for some reason not in cache, we just skip the scoreboard update
    	        	if(player != null) {
    	        		
    	        		// create a placeholder for our level bar
    	        		Score levelPercentage;
    	        		
    	        		// grab our player's current experience
    	        		int farmingXP = player.getFarming();
    	        		
    	        		// check what level our player is
    	        		int level = ExperienceFormatting.getLevelFromXp(farmingXP);
    	        		
    	        		// if the player is max level, just put a generic message
    	        		if(level == 10) {
    	        			levelPercentage = objective.getScore(SeasonManager.this.plugin.color("&c&lMAX LEVEL"));
    	        		}
    	        		// else we create a percentage bar basd on current xp and xp needed for the next level
    	        		else {
    	        			
    	        			// first we grab the amount of xp need for next level
    	        			int next = ExperienceFormatting.getRequiredXpForLevel(level + 1);
    	        			
    	        			// now we construct a progress bar using our action bar class
    	        			String progressBar = ActionBarUtilities.createPercentageBar(next, farmingXP, 18);
    	        			
    	        			// finally we set the objective with appropriate formatting
    	        			levelPercentage = objective.getScore(SeasonManager.this.plugin.color("&3&l" + level + "&8[" + progressBar + "&8]&3&l" + level + 1));
    	        			
    	        		}
    	        		
    	        		// now we set our level percentage to the appropriate sb slots
    	        		levelPercent.setScore(5);
    	        		levelPercentage.setScore(4);
    	        		
    	        		// grab our player's balance and apply it to the appropriate sb slots
    	        		Score balanceTotal = objective.getScore(SeasonManager.this.plugin.color("&6" + StringFormatting.formatNumber(player.getStarCoins())));
    	        		
    	        		balanceBlank.setScore(3);
    	        		balance.setScore(2);
    	        		balanceTotal.setScore(1);
    	        		
    	        		// finally set the player's scoreboard
    	        		p.setScoreboard(board);
    	        		
    	        	}
    	        	
    	        }
    		}
    		
    	};
    	
    	this.scoreboardCycle.runTaskTimer( (Plugin) this.plugin, 5L, 20L);
    	
    }
    
    /**
     * Local utility method to wilt crops that are out of season
     * @param planted A hashmap of all planted crops
     * @param season The current season to compare against
     */
    private void loopOverPlantedCrops(HashMap<Block, PlantedCrop> planted, String season) {
    	
    	// loop over every object in the map
    	for(Map.Entry<Block, PlantedCrop> entry : planted.entrySet()) {
    		
    		// if the planted crop's crop type doesn't include the current season, we deconstruct it from memory
    		if(!(entry.getValue().getCrop().getGrowableSeasons().contains(season.toLowerCase()))) {
    			entry.getValue().harvestCrop();
    		}
    		
    	}
    	
    }

}