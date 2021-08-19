package com.app.dashwood.fingerprintsensor.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.FingerprintGestureController;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.fingerprint.FingerPrintHandler;
import com.app.dashwood.fingerprintsensor.fingerprint.FingerprintGesture;
import com.app.dashwood.fingerprintsensor.log.T;

public class ServiceFingerprint extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {
    }
    @Override
    protected boolean onGesture(int gestureId) {
        //Log.d("LOG", "onGesture " + gestureId);
        return super.onGesture(gestureId);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        T.log("LOG","Connected");
        if (Data.readPreferecesBoolean(getApplicationContext(), getString(R.string.PREF_HOME_TOUCHER_FINGERPRINT), false,
                getString(R.string.KEY_TOUCHER_FINGERPRINT))){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                FingerprintGestureController gestureController =  getFingerprintGestureController();
                T.log("LOG", "Is available: " + gestureController.isGestureDetectionAvailable() );
                if (gestureController.isGestureDetectionAvailable()){
                    FingerprintGesture fingerprintGesture = new FingerprintGesture(getApplicationContext());
                    gestureController.registerFingerprintGestureCallback(fingerprintGesture,new Handler());
                }else {
                    //send device and your problem
                }

            }
        }else {
            IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
            filter.addAction(Intent.ACTION_SCREEN_OFF);

            registerReceiver(new BroadCastFingerPrint(), filter);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            FingerPrintHandler fingerPrintHandler = new FingerPrintHandler(this);
            if (fingerPrintHandler.cipherInit())

            {
                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(A.getCipher());
                fingerPrintHandler.startAuth(fingerprintManager, cryptoObject);
            }
        }

    }
}
