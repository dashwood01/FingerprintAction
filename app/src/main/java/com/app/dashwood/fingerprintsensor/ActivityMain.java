package com.app.dashwood.fingerprintsensor;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.dashwood.fingerprintsensor.adapter.AdapterRecFPAction;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.dataset.SynchronizedDatabase;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.extra.ContextWrapper;
import com.app.dashwood.fingerprintsensor.listener.OnClickDialogListener;
import com.app.dashwood.fingerprintsensor.log.DialogCustom;
import com.app.dashwood.fingerprintsensor.log.T;
import com.app.dashwood.fingerprintsensor.preferences.PreferenceSettings;

import java.util.Locale;

public class ActivityMain extends AppCompatActivity {
    private AdapterRecFPAction adapterRecFPAction;
    private SynchronizedDatabase synchronizedDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        synchronizedDatabase = new SynchronizedDatabase(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recActions = (RecyclerView) findViewById(R.id.recActionFingerPrint);
        adapterRecFPAction = new AdapterRecFPAction(this);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recActions.setLayoutManager(gridLayoutManager);
        recActions.setAdapter(adapterRecFPAction);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers

        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Data.readPreferecesBoolean(getApplicationContext(), getString(R.string.PREF_HOME_TOUCHER_FINGERPRINT), false,
                        getString(R.string.KEY_TOUCHER_FINGERPRINT))){
                    startActivity(new Intent(ActivityMain.this,ActivityActionSwipe.class));
                }else {
                    startActivity(new Intent(ActivityMain.this, ActivityActions.class));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnActionSettings:
                startActivity(new Intent(this, PreferenceSettings.class));
                break;
            case R.id.btnActionHelping:
                startActivity(new Intent(this, ActivityHelp.class));
                break;
            case R.id.btnActionAboutUs:
                startActivity(new Intent(this, ActivityAboutUs.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkingBeforeRun() {
        T.log("LOG", "HERE RUN");
        final DialogCustom dialogCustom = new DialogCustom(this);
        if (!Data.readPreferecesBoolean(this, getString(R.string.PREF_HOME_CHECKER_HELP_VIEW),
                false, getString(R.string.KEY_CHECKER_HELP_VIEW))) {
            dialogCustom.setTitle(getString(R.string.dialog_title_for_help))
                    .setMessage(getString(R.string.dialog_message_for_help))
                    .setIcon(getDrawable(R.drawable.ic_help_alert))
                    .setButtonPositive(getString(R.string.dialog_button_positive_yes))
                    .setListenerAcceptButton(new OnClickDialogListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(ActivityMain.this, ActivityHelp.class));
                            dialogCustom.dismass();
                        }
                    })
                    .setCancable(false).show();
        }


    }

    private void getListActions() {
        A.setFingerActions(synchronizedDatabase.getWritableDatabase().getAllAction());
        if (A.getFingerActions().size() > 0) {
            adapterRecFPAction.sendAction(A.getFingerActions());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getListActions();
        checkingBeforeRun();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale;
        if (Data.readToPrefermenceInt(newBase, newBase.getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 3,
                newBase.getString(R.string.KEY_CHANGE_LANGUAGE))==0) {
            newLocale = new Locale("ira");
        } else {
            newLocale = new Locale("en");
        }
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }
}
