package com.app.dashwood.fingerprintsensor.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.fingerprint.FingerPrintHandler;


public class BroadCastFingerPrint extends BroadcastReceiver {
    private FingerPrintHandler fingerPrintHandler;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            fingerPrintHandler = new FingerPrintHandler(context);
            if (fingerPrintHandler.cipherInit()) {
                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(A.getCipher());
                fingerPrintHandler.startAuth(fingerprintManager, cryptoObject);
            }
        }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            if (fingerPrintHandler != null) {
                fingerPrintHandler.setCancellationSignalCanceled();
            }
        }
    }
}
