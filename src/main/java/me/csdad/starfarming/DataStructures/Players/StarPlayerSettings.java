package me.csdad.starfarming.DataStructures.Players;

import org.json.JSONObject;

public class StarPlayerSettings {
	
	
	private boolean scoreboardToggled;
	private JSONObject serializeableJson;
	
	public StarPlayerSettings(boolean scoreboardToggled, JSONObject serializeableJson) {
		this.scoreboardToggled = scoreboardToggled;
		this.serializeableJson = serializeableJson;
	}
	
	public boolean getScoreboardToggled() {
		return this.scoreboardToggled;
	}
	
	public void setScoreboardToggled(boolean value) {
		this.scoreboardToggled = value;
		this.serializeableJson.put("scoreboard", (value == true) ? "enabled" : "disabled");
	}
	
	public JSONObject getJson() {
		return this.serializeableJson;
	}
	
	
	public String serialize() {
		return this.serializeableJson.toString();
	}

}
