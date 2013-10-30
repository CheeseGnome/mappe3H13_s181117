package hioa.android.logviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Custom implementation of BufferedReader designed to read system logs through
 * the logcat command.
 * <p>
 * The class will self-initialize with an InputStreamReader on the inputstream
 * of the logcat process.
 * 
 * @author Lars Sætaberget
 * @version 2013-10-30
 * @see BufferedReader
 */
public class LogReader extends BufferedReader {

	private static final String[] COMMAND = { "logcat", "-d", "-n", "" + Integer.MAX_VALUE};

	private static Process logcat_process;
	static {
		try {
			logcat_process = Runtime.getRuntime().exec(COMMAND);
		} catch (IOException ioe) {
			// TODO Exception handling
		}
	}

	/**
	 * Initializes a LogReader with an InputStreamReader on the inputstream of
	 * the logcat process.
	 * 
	 * @see java.io.InputStream
	 * @see InputStreamReader
	 */
	public LogReader() {
		super(new InputStreamReader(logcat_process.getInputStream()));
	}
}
