package com.app.dashwood.fingerprintsensor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.dashwood.fingerprintsensor.datasave.Data;

public class ActivityLang extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Data.readToPrefermenceInt(getApplicationContext(),getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 3,
                getString(R.string.KEY_CHANGE_LANGUAGE))== 0 || Data.readToPrefermenceInt(getApplicationContext(),getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 3,
                getString(R.string.KEY_CHANGE_LANGUAGE))== 1){
            gotoStartup();
        }
        setContentView(R.layout.activity_lang);
        Button btnPersian = (Button) findViewById(R.id.btnSelectLanPersian);
        Button btnEn = (Button) findViewById(R.id.btnSelectLanEn);
        btnPersian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.saveToPrefermenceInt(getApplicationContext(), getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 0,
                        getString(R.string.KEY_CHANGE_LANGUAGE));
                saveLang();
                gotoStartup();
            }
        });
        btnEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.saveToPrefermenceInt(getApplicationContext(), getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 1,
                        getString(R.string.KEY_CHANGE_LANGUAGE));
                saveLang();
                gotoStartup();
            }
        });
    }
    private void gotoStartup(){
        startActivity(new Intent(this,ActivityStartup.class));
        this.finish();
    }
    private void saveLang(){
        Data.saveToPrefermenceBoolean(getApplicationContext(),getString(R.string.PREF_HOME_CHECKER_LANG_ACTIVITY),true,
                getString(R.string.KEY_CHECKER_LANG_ACTIVITY));
    }
}
