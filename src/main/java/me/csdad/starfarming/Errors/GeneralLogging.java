package me.csdad.starfarming.Errors;

public enum GeneralLogging {
	
	
	VERBOSE_LOAD("Loaded Player with uuid: "),
	INVALID_SEASON("An invalid season string has been configured. Accepted seasons are SPRING, SUMMER, FALL, WINTER."),
	DISABLING_PLUGIN("A critical error has been encountered. Refer to error logs. Disabling plugin.");
	
	
	public final String log;
	
	
	// constructor to constructor the message with the corresponding error once called
	GeneralLogging(String log) {
		this.log = log;
	}
	
	public String getLog() {
		return this.log;
	}
}
