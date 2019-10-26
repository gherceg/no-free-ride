package com.graham.nofreeride.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.graham.nofreeride.R;

/**
 * Created by grahamherceg on 2/3/18.
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;
    View view;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_metrics);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for(int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            if(getPreferenceScreen().getPreference(i) instanceof  EditTextPreference) {
                updateSummary((EditTextPreference) getPreferenceScreen().getPreference(i));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = getPreferenceScreen().findPreference(key);
        if(pref instanceof EditTextPreference) {
            updateSummary((EditTextPreference) pref);
        } else if(pref instanceof CheckBoxPreference) {

        }
    }

    private void updateSummary(EditTextPreference preference) {
        preference.setSummary(preference.getText());
    }

}
