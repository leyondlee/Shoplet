package mapp.teamkcl.shoplet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by Leyond on 27/1/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements OnPreferenceChangeListener {
    private ListPreference themeLP;
    private CheckBoxPreference autologinCP;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Load xml
        addPreferencesFromResource(R.xml.settings_content);

        themeLP = (ListPreference) findPreference("theme");
        autologinCP = (CheckBoxPreference) findPreference("autologin");

        //Set listeners
        themeLP.setOnPreferenceChangeListener(this);
        autologinCP.setOnPreferenceChangeListener(this);
        autologinCP.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                autologinCP.setChecked(!autologinCP.isChecked());
                return false;
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        int color = -1;
        boolean autologin = true;

        SharedPreferences sp = getActivity().getSharedPreferences("Settings",getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        switch (preference.getKey()) {
            case "theme": {
                //Change display to new value
                String newValue = o.toString();
                themeLP.setValue(newValue);
                themeLP.setSummary(themeLP.getEntry());

                color = Integer.parseInt(newValue);

                getActivity().invalidateOptionsMenu();
                editor.putInt("theme",color);

                break;
            }

            case "autologin": {
                //Set check box depending on boolean
                autologin = (boolean) o;
                editor.putBoolean("autologin",autologin);

                break;
            }
        }

        editor.commit();

        return false;
    }
}
