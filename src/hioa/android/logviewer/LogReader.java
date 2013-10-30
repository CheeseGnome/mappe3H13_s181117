package hioa.android.logviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogReader extends BufferedReader {

	private static final String[] COMMAND = { "logcat", "-d" };
	
	private static Process logcat_process;
	static {
		try {
			logcat_process = Runtime.getRuntime().exec(COMMAND);
		} catch (IOException ioe) {
			//TODO do stuff
		}
	}

	public LogReader() {
		super(new InputStreamReader(logcat_process.getInputStream()));
	}
}
