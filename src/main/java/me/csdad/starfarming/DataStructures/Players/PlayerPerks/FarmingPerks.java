package me.csdad.starfarming.DataStructures.Players.PlayerPerks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.csdad.starfarming.Errors.DatabaseLogging;

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
	
	public void save(Connection conn, String uuid, boolean forceClose) {
		
		String sql_statement = "UPDATE farming_perks SET farming_level = ?, " +
                "perk_1 = ?, perk_1_opt = ?, perk_1_opt_2 = ?, perk_1_selected = ?, " +
                "perk_2 = ?, perk_2_opt = ?, perk_2_opt_2 = ?, perk_2_selected = ?, " +
                "perk_3 = ?, perk_3_opt = ?, perk_3_opt_2 = ?, perk_3_selected = ? " +
                "WHERE owner_uuid = ?";
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql_statement);
			
	        stmt.setInt(1, level);

	        // Perk 1
	        stmt.setInt(2, perkOptions.get(1)[0]);
	        stmt.setInt(3, perkOptions.get(1)[1]);
	        stmt.setInt(4, perkOptions.get(1)[2]);
	        stmt.setInt(5, perkSelection.get(1));

	        // Perk 2
	        stmt.setInt(6, perkOptions.get(2)[0]);
	        stmt.setInt(7, perkOptions.get(2)[1]);
	        stmt.setInt(8, perkOptions.get(2)[2]);
	        stmt.setInt(9, perkSelection.get(2));

	        // Perk 3
	        stmt.setInt(10, perkOptions.get(3)[0]);
	        stmt.setInt(11, perkOptions.get(3)[1]);
	        stmt.setInt(12, perkOptions.get(3)[2]);
	        stmt.setInt(13, perkSelection.get(3));

	        stmt.setString(14, uuid);
	        stmt.executeUpdate();
	        
	        if(forceClose) conn.close();
			
		} catch(SQLException e) {
			
			Bukkit.getLogger().log(Level.SEVERE, DatabaseLogging.SQL_EXCEPTION.getLog());
			e.printStackTrace();
			
		}
		
	}
	
	
	
}
