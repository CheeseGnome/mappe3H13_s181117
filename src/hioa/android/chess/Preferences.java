package hioa.android.chess;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
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
	}

	public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
			
			EditTextPreference preference = (EditTextPreference) findPreference("whiteName");
			preference.setSummary(sharedPreferences.getString("whiteName", ""));
			
			preference = (EditTextPreference) findPreference("blackName");
			preference.setSummary(sharedPreferences.getString("blackName", ""));
		}

		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if(!key.equals("whiteName") && !key.equals("blackName")){
				return;
			}
			EditTextPreference preference = (EditTextPreference) findPreference(key);
			preference.setSummary(sharedPreferences.getString(key, ""));
		}
	}
}