package com.app.dashwood.fingerprintsensor.fingerprint;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import androidx.core.app.ActivityCompat;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.dataset.InformationFPAction;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.extra.SendEmail;
import com.app.dashwood.fingerprintsensor.log.T;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private final String KEY_NAME = "FINGER_SECURITY_KEY";
    private KeyStore keyStore;
    private int count;
    private Handler handler = new Handler();
    private Handler h = new Handler(Looper.myLooper());
    private boolean checkFlashlight;
    private int deleyValue;
    private static boolean checkSamsung;
    private static int countSasmsung;
    private CancellationSignal cancellationSignal;
    private SendEmail sendEmail;
    private AudioManager audioManager;
    private T toastManager;


    public FingerPrintHandler(final Context context) {
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        checkSamsung = preferences.getBoolean(context.getString(R.string.preference_key_check_front_fp), false);
        String time = preferences.getString("TIMER_FOR_ZERO_COUNT", "1");
        deleyValue = Integer.valueOf(time);
        toastManager = new T(context);
        sendEmail = new SendEmail(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        A.getCheckTimer(true);
        checkFlashlight = Data.readPreferecesBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_FLASHLIGHT), true,
                context.getString(R.string.KEY_CHECKER_FLASHLIGHT));
        generateKey();
    }


    public void startAuth(FingerprintManager manager,
                          FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, handler);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        if (checkSamsung) {
            countSasmsung++;
            toastManager.sT(String.valueOf(countSasmsung),context.getDrawable(R.drawable.ic_close_toast));
            checkSamsungRunnable();
            if (countSasmsung == 2) {
                A.getVibrator().vibrate(500);
                toastManager.sT(context.getString(R.string.toast_sami_enable),context.getDrawable(R.drawable.ic_accept_toast));
                countSasmsung = 0;
                checkSamsung = false;
                countDowntimerEnable();
            }
        } else {
            count++;
            toastManager.sT(String.valueOf(count),null);
            A.getVibrator().vibrate(50);
            checkAction("fast");
        }

    }

    @Override
    public void onAuthenticationFailed() {
        toastManager.sT(context.getString(R.string.toast_fail_finger),context.getDrawable(R.drawable.ic_finger_nocheck));
        if (!checkSamsung) {
            checkAction("fail");
        }
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        if (!checkSamsung) {
            checkAction("secceed");
        }
        FingerprintManager fpManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        FingerPrintHandler fingerPrintHandler = new FingerPrintHandler(context);
        if (fingerPrintHandler.cipherInit()) {
            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(A.getCipher());
            fingerPrintHandler.startAuth(fpManager, cryptoObject);
        }


    }

    private void checkAction(String checkAction) {
        switch (checkAction) {
            case "fast":
                h.removeCallbacks(runnableAction);
                h.postDelayed(runnableAction, deleyValue * 1000);
                break;
            case "secceed":
                for (InformationFPAction fp : A.getFingerActions()) {
                    if (Objects.equals(fp.getWhatFinger(), "succeful")) {
                        workToDo(fp.getWhatAction(), fp.getPackageName());
                        return;
                    }
                }
                toastManager.sT(context.getString(R.string.toast_msg_noneAction),context.getDrawable(R.drawable.ic_block));
                break;
            case "fail":
                for (InformationFPAction fp : A.getFingerActions()) {
                    if (Objects.equals(fp.getWhatFinger(), "fail")) {
                        workToDo(fp.getWhatAction(), fp.getPackageName());
                        return;
                    }
                }
                break;
        }

    }

    private Runnable runnableAction = new Runnable() {
        @Override
        public void run() {
            for (InformationFPAction fp : A.getFingerActions()) {
                if (fp.getCount() == count) {
                    workToDo(fp.getWhatAction(), fp.getPackageName());
                    return;
                }
            }
            toastManager.sT(context.getString(R.string.toast_msg_noneAction),context.getDrawable(R.drawable.ic_block));
            count = 0;
        }
    };




    private void checkSamsungRunnable() {
        h.removeCallbacks(runnableSamsung);
        h.postDelayed(runnableSamsung, 1000);
    }

    private Runnable runnableSamsung = new Runnable() {
        @Override
        public void run() {
            countSasmsung = 0;
        }
    };

    private void countDowntimerEnable() {
        if (A.getCheckTimer(false)) {
            countSasmsung = 0;
        }

    }

    private void workToDo(String action, String doAction) {
        Intent intent;
        int maxVolMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolRing = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int volRing = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int maxVolCall = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int volCall = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        switch (action) {
            case "call":
                intent = new Intent(Intent.ACTION_CALL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + doAction));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent);
                toastManager.sT(context.getString(R.string.toast_msg_call),context.getDrawable(R.drawable.ic_accept_toast));
                break;
            case "message":
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.fromParts("sms", doAction, null));
                context.startActivity(intent);
                toastManager.sT(context.getString(R.string.toast_msg_message),context.getDrawable(R.drawable.ic_accept_toast));
                break;
            case "app":
                PackageManager pm = context.getPackageManager();
                intent = pm.getLaunchIntentForPackage(doAction);
                if (null != intent) {
                    context.startActivity(intent);
                    toastManager.sT(context.getString(R.string.toast_msg_app),context.getDrawable(R.drawable.ic_accept_toast));
                } else {
                    toastManager.sT(context.getString(R.string.toast_error_app_run),context.getDrawable(R.drawable.ic_block));
                }
                break;
            case "work":
                switch (doAction) {
                    case "wifi":
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        if (wifiManager.isWifiEnabled()) {
                            wifiManager.setWifiEnabled(false);
                        } else {
                            wifiManager.setWifiEnabled(true);
                        }
                        break;
                    case "bluetooth":
                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.disable();
                        } else {
                            mBluetoothAdapter.enable();
                        }
                        break;
                    case "flashlight":
                        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                            try {
                                String id = cameraManager.getCameraIdList()[0];
                                if (checkFlashlight) {
                                    cameraManager.setTorchMode(id, true);
                                    checkFlashlight = false;
                                    Data.saveToPrefermenceBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_FLASHLIGHT), false,
                                            context.getString(R.string.KEY_CHECKER_FLASHLIGHT));
                                } else {
                                    cameraManager.setTorchMode(id, false);
                                    checkFlashlight = true;
                                    Data.saveToPrefermenceBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_FLASHLIGHT), true,
                                            context.getString(R.string.KEY_CHECKER_FLASHLIGHT));
                                }
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                                sendEmail.sendBug(e.toString());
                            }
                        }
                        break;
                    case "home":
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;
                    case "lock":
                        if (A.getDevicePolicyManager().isAdminActive(A.getComponentName())) {
                            A.getDevicePolicyManager().lockNow();
                        } else {
                            toastManager.lT("لطفا دسترسی ویژه را از تنظیمات اعمال کنید",context.getDrawable(R.drawable.ic_block));
                        }

                        break;
                    case "play": {
                        intent = new Intent("com.android.music.musicservicecommand");
                        intent.putExtra("command", "play");
                        context.sendBroadcast(intent);
                        break;
                    }
                    case "stop": {
                        intent = new Intent("com.android.music.musicservicecommand");
                        intent.putExtra("command", "pause");
                        context.sendBroadcast(intent);
                        break;
                    }
                    case "next": {
                        intent = new Intent("com.android.music.musicservicecommand");
                        intent.putExtra("command", "next");
                        context.sendBroadcast(intent);
                        break;
                    }
                    case "backward": {
                        intent = new Intent("com.android.music.musicservicecommand");
                        intent.putExtra("command", "previous");
                        context.sendBroadcast(intent);
                        break;
                    }
                    case "up":
                        if (audioManager.isMusicActive()) {
                            if (maxVolMusic >= volMusic) {
                                volMusic++;
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volMusic, AudioManager.FLAG_SHOW_UI);
                            }
                        } else if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
                            if (maxVolCall >= volCall) {
                                volCall++;
                                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volCall, AudioManager.FLAG_SHOW_UI);
                            }
                        } else if (audioManager.getMode() == AudioManager.MODE_NORMAL ||
                                audioManager.getMode() == AudioManager.RINGER_MODE_VIBRATE) {
                            if (maxVolRing >= volRing) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        return;
                                    }
                                }
                                volRing++;
                                audioManager.setStreamVolume(AudioManager.STREAM_RING, volRing, AudioManager.FLAG_SHOW_UI);
                            }
                        }
                        break;
                    case "down":
                        if (audioManager.isMusicActive()) {
                            if (maxVolMusic >= volMusic) {
                                volMusic--;
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volMusic, AudioManager.FLAG_SHOW_UI);
                            }
                        } else if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
                            if (maxVolCall >= volCall) {
                                volCall--;
                                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volCall, AudioManager.FLAG_SHOW_UI);
                            }
                        } else if (audioManager.getMode() == AudioManager.MODE_NORMAL ||
                                audioManager.getMode() == AudioManager.RINGER_MODE_VIBRATE) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                    return;
                                }
                            }
                            if (maxVolRing >= volRing && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                                volRing--;
                                audioManager.setStreamVolume(AudioManager.STREAM_RING, volRing, AudioManager.FLAG_SHOW_UI);
                            }
                        }
                        break;
                    case "vibrate":
                        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL &&
                                audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                        } else if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        } else {
                            toastManager.sT(context.getString(R.string.toast_error_distrub),context.getDrawable(R.drawable.ic_block));
                        }
                        break;
                }
                toastManager.sT(context.getString(R.string.toast_msg_work),context.getDrawable(R.drawable.ic_accept_toast));
                break;
        }
        if (h != null){
            h.removeCallbacks(runnableAction);
        }
        count = 0;

    }


    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
            sendEmail.sendBug(e.toString());
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            sendEmail.sendBug(e.toString());
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            sendEmail.sendBug(e.toString());
            throw new RuntimeException(e);
        }
    }

    public Boolean cipherInit() {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            A.setCipher(cipher);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            e.printStackTrace();
            return false;

        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public static void setFalseSami(){
        countSasmsung = 0;
        checkSamsung = true;
    }
    public void setCancellationSignalCanceled() {
        cancellationSignal.cancel();
    }


}
