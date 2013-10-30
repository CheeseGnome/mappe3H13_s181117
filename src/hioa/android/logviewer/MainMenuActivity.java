package hioa.android.logviewer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		LogReader reader = new LogReader();
		String seperator = "\n\n";
		String line;
		StringBuilder log = new StringBuilder();
		int pid;
		String p_name;
		try {
			while ((line = reader.readLine()) != null) {
				pid = getPID(line);
				if (pid != -1) {
					Log.d("d", "" + pid);
					p_name = getProcessName(pid);
					line = line.replace("" + pid, p_name);
				}
				log.append(line);
				log.append(seperator);

			}
		} catch (IOException ioe) {
			// TODO Exception handling
		}
		((TextView) findViewById(R.id.textView1)).setText(log.toString());
	}

	private int getPID(String line) {
		int start = line.indexOf("(");
		int stop = line.indexOf(")");
		if (start == -1 || stop == -1)
			return -1;
		return Integer.parseInt(line.substring(start + 1, stop).trim());
	}

	private String getProcessName(int pid) {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		Iterator iterator = processes.iterator();

		RunningAppProcessInfo process_info;

		while (iterator.hasNext()) {
			process_info = (RunningAppProcessInfo) iterator.next();
			if (process_info.pid == pid)
				return process_info.processName;
		}
		return "Failed to fetch process name";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}
