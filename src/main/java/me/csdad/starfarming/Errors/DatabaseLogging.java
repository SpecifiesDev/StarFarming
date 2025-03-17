package me.csdad.starfarming.Errors;

public enum DatabaseLogging {
	
	SQL_EXCEPTION("There has been a SQL exception in the current request. Refer to the stack trace."),
	FAILED_TO_LOAD_MEMSTORE("ERROR. Was unable to load players into memory store, indicating an issue with the database connection. Refer to the stack trace for info. Disabling plugin.");
	
	public final String log;
	
	
	// constructor to constructor the message with the corresponding error once called
	DatabaseLogging(String log) {
		this.log = log;
	}
	
	public String getLog() {
		return this.log;
	}

}
