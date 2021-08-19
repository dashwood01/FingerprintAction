package com.app.dashwood.fingerprintsensor.extra;

import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.dataset.InformationFPAction;
import com.app.dashwood.fingerprintsensor.fingerprint.FingerPrintHandler;
import com.app.dashwood.fingerprintsensor.log.T;
import com.app.dashwood.fingerprintsensor.service.DeviceAdministrative;

import java.util.ArrayList;

import javax.crypto.Cipher;

public class A extends Application {

    private static A instance;
    private static ArrayList<InformationFPAction> fingerActions = new ArrayList<>();
    private static String actionvalue = "";
    private static String displayName = "";
    private static Cipher cipher;
    private static DevicePolicyManager devicePolicyManager;
    private static ComponentName componentName;
    private static CountDownTimer countDownTimer;
    private static boolean checkCountDownTime = false;
    private static SharedPreferences sharedPreferences;
    private static Vibrator vibrator;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, DeviceAdministrative.class);
    }
    private static A getInstance() {
        return instance;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static void setFingerActions(ArrayList<InformationFPAction> list) {
        fingerActions.clear();
        fingerActions = list;
    }

    public static ArrayList<InformationFPAction> getFingerActions() {
        return fingerActions;
    }

    public static void setActionvalue(String value) {
        actionvalue = value;
    }

    public static void setDisplayName(String value){
        displayName = value;
    }

    public static String getActionvalue() {
        return actionvalue;
    }

    public static String getDisplayName(){
        return displayName;
    }

    public static void setCipher(Cipher value) {
        cipher = value;
    }

    public static Cipher getCipher() {
        return cipher;
    }

    public static DevicePolicyManager getDevicePolicyManager() {
        return devicePolicyManager;
    }

    public static ComponentName getComponentName() {
        return componentName;
    }

    public static boolean getCheckTimer(boolean newChecker) {
        int timerSensor = (Integer.valueOf(sharedPreferences.getString("KEY_TIMER_SAMI_SENSOR", "10"))) * 1000;
        if (newChecker) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            checkCountDownTime = false;
            return false;
        } else {
            if (checkCountDownTime) {
                countDownTimer.cancel();
                checkCountDownTime = false;
                return false;
            } else {
                checkCountDownTime = true;
                countDownTimer = countDownTimerNew(timerSensor);
                countDownTimer.start();
                T.log("LOG", "" + timerSensor);
                return true;
            }

        }

    }

    private static CountDownTimer countDownTimerNew(int timerSensor) {

        return new CountDownTimer(timerSensor, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                Toast.makeText(getContext(),getContext().getString(R.string.toast_end_time_deley),Toast.LENGTH_SHORT).show();
                vibrator.vibrate(500);
                FingerPrintHandler.setFalseSami();
            }
        };
    }

    public static Vibrator getVibrator() {
        return vibrator;
    }

}

