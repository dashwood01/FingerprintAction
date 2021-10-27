package com.app.dashwood.fingerprintsensor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.extra.ContextWrapper;
import com.app.dashwood.fingerprintsensor.log.T;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Intro extends AppIntro2 {
    private T toastManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermission()) {
            showSkipButton(false);
            setProgressButtonEnabled(true);
            setVibrate(false);
            setNextPageSwipeLock(true);
            setFadeAnimation();
            boolean checkIntroScreen = Data.readPreferecesBoolean(this, getString(R.string.PREF_HOME_CHECKER_INTROSCREEN), false,
                    getString(R.string.KEY_CHECKER_INTROSCREEN));
            if (checkIntroScreen) {
                startActivity(new Intent(this, ActivityMain.class));
                this.finish();
                return;
            } else {
                setNextPageSwipeLock(false);
            }
        }
        toastManager = new T(this);
        addSlide(AppIntroFragment.newInstance("", getString(R.string.intro_description_first_page)
                , R.drawable.intro_first_pic, getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("",
                getString(R.string.intro_description_second_page), R.drawable.intro_second_pic, getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_third_page),
                getString(R.string.intro_description_third_page), R.drawable.intro_third_pic, getColor(R.color.colorPrimaryDark)));

    }

    private boolean checkPermission() {
        ArrayList<String> listPermissions = new ArrayList<>();
        int bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothAdminPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int FingerprintPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT);
        int VibratePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE);
        int AccessWifiPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int ChangeWifiPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.BLUETOOTH);
        }
        if (bluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (FingerprintPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.USE_FINGERPRINT);
        }
        if (VibratePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.VIBRATE);
        }
        if (AccessWifiPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (ChangeWifiPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        }
        if (!listPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissions.toArray(new String[listPermissions.size()]), getResources().getInteger(R.integer.REQUEST_CODE_ASK_PERMISSIONS));
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.BLUETOOTH, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH_ADMIN, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.USE_FINGERPRINT, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.VIBRATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_WIFI_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CHANGE_WIFI_STATE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                        if (perms.get(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                                perms.get(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                                perms.get(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                                perms.get(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
                            setNextPageSwipeLock(false);
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.USE_FINGERPRINT) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)) {
                                T.dialogQuestionShow(this, getResources().getString(R.string.permissionAlert), getString(R.string.dialog_button_positive_text_check),
                                        getString(R.string.dialog_button_negative_text_check), false,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkPermission();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        T.dialogQuestionShow(Intro.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
                                                                getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        switch (which) {
                                                                            case DialogInterface.BUTTON_POSITIVE:
                                                                                toastManager.lT(getString(R.string.permission_toast_getpermission),getDrawable(R.drawable.ic_block));
                                                                                break;
                                                                            case DialogInterface.BUTTON_NEGATIVE:
                                                                                checkPermission();
                                                                        }
                                                                    }
                                                                });
                                                }
                                            }
                                        });
                            } else {
                                toastManager.lT(getString(R.string.permissionDenied),getDrawable(R.drawable.ic_block));
                            }
                        }
                    }

                }
                break;
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Data.saveToPrefermenceBoolean(this, getString(R.string.PREF_HOME_CHECKER_INTROSCREEN), true,
                getString(R.string.KEY_CHECKER_INTROSCREEN));
        startActivity(new Intent(this, ActivityMain.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale;
        if (Data.readPreferecesBoolean(newBase,newBase.getString(R.string.PREF_HOME_CHANGE_LANGUAGE),false,
                newBase.getString(R.string.KEY_CHANGE_LANGUAGE))){
            newLocale = new Locale("ira");

        }else {
            newLocale = new Locale("en");

        }
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }
}
