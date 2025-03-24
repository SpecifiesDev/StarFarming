package me.csdad.starfarming.Errors;

public enum Permissions {
	
	LEVELS_ALL("starfarming.general.levels"),
	SELLALL("starfarming.general.sellall"),
	
	GIVESEED("starfarming.admin.giveseed"),
	
	SETEXP("starfarming.admin.setexp"),
	
	PROFILE("starfarming.general.profile"),
	
	PERKS("starfarming.general.perks");
	
	
	public final String permission;
	
	Permissions(String permission) {
		this.permission = permission;
	}
	
	public String getPermission() {
		
		return this.permission;
	}


}
