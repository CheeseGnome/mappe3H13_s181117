package hioa.android.chess;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * The activity to set preferences
 * 
 * @author Lars Sætaberget
 * @version 2013-11-22
 */

public class Preferences extends PreferenceActivity {

	MyPreferenceFragment mFragment = new MyPreferenceFragment();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, mFragment).commit();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);

			// Set summaries to reflect current values
			EditTextPreference preference = (EditTextPreference) findPreference("whiteName");
			preference.setSummary(sharedPreferences.getString("whiteName", ""));

			preference = (EditTextPreference) findPreference("blackName");
			preference.setSummary(sharedPreferences.getString("blackName", ""));

			ListPreference list = (ListPreference) findPreference("time_preference");
			list.setSummary(getTimeString(false, Long.parseLong(sharedPreferences.getString("time_preference", "0"))));

			list = (ListPreference) findPreference("bonus_preference");
			list.setSummary(getTimeString(true, Long.parseLong(sharedPreferences.getString("bonus_preference", "0"))));
		}

		// Set summaries to reflect current values whenever a preference changes
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals("whiteName") || key.equals("blackName")) {
				EditTextPreference preference = (EditTextPreference) findPreference(key);
				preference.setSummary(sharedPreferences.getString(key, ""));
			} else if (key.equals("time_preference") || key.equals("bonus_preference")) {
				ListPreference list = (ListPreference) findPreference(key);
				list.setSummary(getTimeString(key.equals("bonus_preference"),
						Long.parseLong(sharedPreferences.getString(key, "0"))));
			}
		}

		/**
		 * Converts the provided time to the string representation as found in
		 * arrays.xml
		 * 
		 * @param bonus
		 *            True if you want the strings for bonus time, false for
		 *            normal time strings
		 * @param time
		 *            The time to convert
		 * @return A string representing the current selection
		 */
		private String getTimeString(boolean bonus, long time) {
			if (time == 0) {
				if (bonus) {
					return "No bonus";
				} else {
					return "Infinite";
				}
			} else if (time == 1) {
				return "1 second";
			} else if (time == 2) {
				return "2 seconds";
			} else if (time == 5) {
				return "5 seconds";
			} else if (time == 10) {
				return "10 seconds";
			} else if (time == 20) {
				return "20 seconds";
			} else if (time == 30) {
				return "30 seconds";
			} else if (time == 60) {
				return "1 minute";
			} else if (time == 300) {
				return "5 minutes";
			} else if (time == 600) {
				return "10 minutes";
			} else if (time == 1200) {
				return "20 minutes";
			} else if (time == 1800) {
				return "30 minutes";
			} else if (time == 3600) {
				return "1 hour";
			} else if (time == 7200) {
				return "2 hours";
			}
			return "";
		}
	}
}