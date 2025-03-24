package me.csdad.starfarming.Utility;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.csdad.starfarming.Core;
import me.csdad.starfarming.Commands.Experience.ExperienceFormatting;
import me.csdad.starfarming.DataStructures.CropInfo;
import me.csdad.starfarming.DataStructures.PlantedCrop;
import me.csdad.starfarming.DataStructures.Players.StarPlayer;

public class SeasonManager {
	
	// instance of our main class
	private Core plugin;
	
	// instances of our season and scoreboard cycles in order to disable when needed
	private BukkitRunnable seasonCycle;
	private BukkitRunnable scoreboardCycle;
	private BukkitRunnable cropInfo;
	
	// how long a season lasts in minutes
	private int seconds;
	
	private int secondsRemaining;
	
	private Scoreboard blankBoard;


	
	
	public SeasonManager() {
		
		this.plugin = Core.getInstance();
		
		this.seconds = plugin.getConfig().getInt("general.seasons.season-cycle") * 60;
		this.secondsRemaining = seconds;
		
		blankBoard = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	public void start() {
		this.startSeasonCycle();
		this.startCropInfoCycle();
		this.startScoreboardCycle();
	}
	
	public void stop() {
		this.seasonCycle.cancel();
		this.cropInfo.cancel();
	}
	
	private void startScoreboardCycle() {
		
		this.scoreboardCycle = new BukkitRunnable() {
			
			public void run() {
				
				// create a new scoreboard
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getMainScoreboard();
				
				Objective checkRegistry = board.getObjective("globalsbstarfarming");
				
				if(checkRegistry != null) checkRegistry.unregister();
				
				// create a new objective
				Objective objective = board.registerNewObjective("globalsbstarfarming", Criteria.DUMMY, "main");
				
				
				// set the displayname
				objective.setDisplayName(plugin.color(plugin.getConfig().getString("scoreboard.title")));
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				
				// create blank lines for each scoreboard entry
    	        Score blank = objective.getScore(plugin.color("&c&m"));
    	        Score levelBlank = objective.getScore(plugin.color("&7&m"));
    	        Score balanceBlank = objective.getScore(plugin.color("&4&m"));
    	        
    	        // line headers for values
    	        Score currentSeason = objective.getScore(plugin.color("&7Current Season"));
    	        Score timeLeftHeader = objective.getScore(plugin.color("&7Next Season"));
    	        Score levelPercent = objective.getScore(plugin.color("&7Level Progress"));
    	        Score balance = objective.getScore(plugin.color("&7Star Coins"));
    	        
    	        // score lines out of non related player data
    	        Score season = objective.getScore(plugin.color("&b" + plugin.getMemoryStore().getSeason()));
    	        Score timeLeft = objective.getScore(plugin.color(StringFormatting.formatSeconds(secondsRemaining)));
    	        
    	        // set static lines to their location
    	        currentSeason.setScore(11);
    	        season.setScore(10);
    	        blank.setScore(9);
    	        timeLeftHeader.setScore(8);
    	        timeLeft.setScore(7);
    	        levelBlank.setScore(6);
    	        
    	        // loop over every played
    	        for(Player p : Bukkit.getOnlinePlayers()) {
    	        	
    	        	StarPlayer player = plugin.getMemoryStore().getStarPlayer(p.getUniqueId().toString());
    	        	
    	        	// null check
    	        	if(player == null) continue;
    	        	
    	        	// check if the scoreboard is on or off
    	        	if(player.getSettings().getScoreboardToggled() == false) {
    	  
    	        		
    	        		if(p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
    	        			p.setScoreboard(blankBoard);
    	        		}
    	        		
    	        		continue;
    	        		
    	        	}
    	        	
    	        	// placeholder for the level bar
    	        	Score levelPercentage;
    	        	
    	        	// current farming xp
    	        	int farmingXP = player.getExperience();
    	        	
    	        	// get the player's level
    	        	int level = ExperienceFormatting.getLevelFromExperience(farmingXP);
    	        	
    	        	// if level is 10, we can just set a generic message
    	        	if(level >= 10) {
    	        		levelPercentage = objective.getScore(plugin.color("&a&lMAX FARMING LEVEL"));
    	        	} else {
    	        		
    	        		// grab the xp for the next level
    	        		int next = ExperienceFormatting.getExperienceForLevel(level + 1);
    	        		
    	        		
    	        		String progressBar = StringFormatting.createPercentageBar(next, farmingXP, 18, ':');
    	        		
    	        		levelPercentage = objective.getScore(plugin.color("&3&l " + level + "&8[" + progressBar + "&8]&3&l" + (level + 1)));
    	        		
    	        	}
    	        	
    	        	// set our level percentage to the appropriate slots
    	        	levelPercent.setScore(5);
    	        	levelPercentage.setScore(4);
    	        	
    	        	String balanceString = StringFormatting.formatNumber(player.getStarCoins()).replaceAll(",", "&7,&6");
    	        	// balance for the player
    	        	Score balanceTotal = objective.getScore(plugin.color("&6" + balanceString));
    	        	
    	        	// set the rest
	        		balanceBlank.setScore(3);
	        		balance.setScore(2);
	        		balanceTotal.setScore(1);
	        		
	        		// set the player's sb
	        		p.setScoreboard(board);
    	        		
    	        	
    	        }
			}
			
		};
		
		this.scoreboardCycle.runTaskTimerAsynchronously(plugin, 5L, 20L);
		
		
	}
	
	private void startCropInfoCycle() {
		
		this.cropInfo = new BukkitRunnable() {
			
			public void run() {
				
				HashMap<Player, CropInfo> playersViewing = plugin.getMemoryStore().getAllCropInfo();
				
				if(playersViewing.isEmpty()) return;
				
				for(Map.Entry<Player, CropInfo> entry : playersViewing.entrySet()) {
					
					// retrieve our kvp
					Player p = entry.getKey();
					CropInfo info = entry.getValue();
					
					PlantedCrop crop = plugin.getMemoryStore().getPlantedCrop(info.getPlantedBlock());
					
					boolean canHarvest = crop.canHarvest();
					
					// get the formatted time left string.
					// the reason we do it this way is for real time updates on the crop time. if we reelied on the memory store placed
					// when the inv is opened, it would not be dynamic.
					String formattedTimeLeft = crop.getTimeLeftFormatted();
					
					// get the top inventory
					Inventory viewingInv = p.getOpenInventory().getTopInventory();
					
					// get the clock at 19
					ItemStack item = viewingInv.getItem(19); 
					
					// make sure the item is a clock, otherwise we won't attempt to update.
					if(!(item.getType() == Material.CLOCK)) return;
					
					ItemFactory editable = new ItemFactory(item); // pass the item into the factory so we can reset its lore
					
					editable.setDisplayName(canHarvest ? "&7Click me to harvest&8!" : editable.getItem().getItemMeta().getDisplayName());
					editable.setLore((canHarvest ? "&a&lREADY" : formattedTimeLeft)); // set the lore to the new time
					
					// reset the item in the player's inventory
					viewingInv.setItem(19, editable.getItem());
					
				}
				
			}
			
			
		};
		
		cropInfo.runTaskTimerAsynchronously(plugin, 5L, 20L);
	}
	
	/**
	 * Method to start the season cycle
	 * The thread will tick every 20 ticks, or every real life second.
	 */
	private void startSeasonCycle() {
		
		this.seasonCycle = new BukkitRunnable() {
			
			public void run() {
				
				// subtract a second from the remaining time
				secondsRemaining = secondsRemaining - 1;
			
				
				// if the time alloted has passed, let's begin the process of switching to a new season.
				
				// get the current season
				String currentSeason = plugin.getMemoryStore().getSeason();
				
				if(secondsRemaining <= 0) {
					secondsRemaining = seconds;
					if(currentSeason.equals("SPRING")) {
						
						plugin.getMemoryStore().setSeason("SUMMER");
						loopOverPlantedCrops("SUMMER");
						
					} else if(currentSeason.equals("SUMMER")) {
						
						plugin.getMemoryStore().setSeason("FALL");
						loopOverPlantedCrops("FALL");
						
					} else if(currentSeason.equals("FALL")) {
						
						plugin.getMemoryStore().setSeason("WINTER");
						loopOverPlantedCrops("WINTER");
					} else {
						
						plugin.getMemoryStore().setSeason("SPRING");
						loopOverPlantedCrops("SPRING");
						
					}
					
				}
			
				
			}
			
		};
		
		// execute the task timer every second
		this.seasonCycle.runTaskTimerAsynchronously(plugin, 5L, 20L);
		
	}
	

	/**
	 * Method to loop over planted crops and "wilt" them if they aren't growable in the new season
	 * @param season The new season
	 */
	private void loopOverPlantedCrops(String season) {
		
		for(Map.Entry<Block, PlantedCrop> entry : this.plugin.getMemoryStore().getPlantedCrops().entrySet()) {
			
			if(!entry.getValue().getCrop().getGrowableSeasons().contains(season)) {
				entry.getValue().harvestCrop();
			}
			
		}
		
	}
	
	

}
