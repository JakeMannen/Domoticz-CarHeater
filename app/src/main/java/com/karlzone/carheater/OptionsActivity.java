package com.karlzone.carheater;

import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class OptionsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new OptionsFragment()).commit();
    }

    public static class OptionsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
