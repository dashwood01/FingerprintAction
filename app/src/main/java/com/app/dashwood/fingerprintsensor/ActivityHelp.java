package com.app.dashwood.fingerprintsensor;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.extra.ContextWrapper;

import java.util.Locale;

public class ActivityHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHelping);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Data.saveToPrefermenceBoolean(this, getString(R.string.PREF_HOME_CHECKER_HELP_VIEW),
                true, getString(R.string.KEY_CHECKER_HELP_VIEW));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale;
        if (Data.readToPrefermenceInt(newBase, newBase.getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 3,
                newBase.getString(R.string.KEY_CHANGE_LANGUAGE)) == 0) {
            newLocale = new Locale("ira");

        } else {
            newLocale = new Locale("en");

        }
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }
}
