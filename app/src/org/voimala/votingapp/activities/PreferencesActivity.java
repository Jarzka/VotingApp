package org.voimala.votingapp.activities;

import org.voimala.votingapp.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.preferences);
        
        addPreferencesFromResource(R.xml.preferences);
        /* PreferenceFragment should be used in Android 3 and higher, but PreferenceActivity
         * seems to work better in this application. */
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
}