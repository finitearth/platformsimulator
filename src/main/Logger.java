package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * This auxiliary class creates a log file of the most recent execution of the code.
 * Previously created log files are automatically overwritten.
 */
public class Logger {
	
	public static Logger instance;	
	public static int verboseness = 1;	// controls the level of detail of the log file	
	PrintStream wif;
	
	// Level 1 - Only general game events and stats (create/load/save/turn) are logged
	// Level 2 - Player level events and stats (research/income/expeditures) are logged
	// Level 3 - General behavior of buyers & sellers are logged (registrations/change of platform)
	// Level 4 - Details of buyer & sellers thoughts are logged (excluding utility function calculations)
	// Level 5 - Details of buyer & sellers thoughts are logged (including utility function calculations)
	
	public static Logger getInstane() {
		
		if(instance==null) {
			instance=new Logger();
			String file = System.getProperty("user.dir")+"/logs/log.txt";
			try {
				instance.wif = new PrintStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return instance;
		
	}
	
	/**
	 * Request the logger to add the given message to the log file. If the level of detail of the message is higher
	 * than the level of detail setting in the logger, the message is ignored.
	 * @param message The message to be added to the log file
	 * @param category The detail level of the message 
	 */
	public void log(String message, int category) {
		if(verboseness>=category)wif.println(message);
	}

}
