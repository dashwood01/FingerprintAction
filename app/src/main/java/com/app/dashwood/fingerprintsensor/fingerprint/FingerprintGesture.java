package com.app.dashwood.fingerprintsensor.fingerprint;

import android.accessibilityservice.FingerprintGestureController;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.dataset.InformationFPAction;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.log.T;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FingerprintGesture extends FingerprintGestureController.FingerprintGestureCallback {
    private Context context;
    private int count;
    private T toastManager;
    private Handler handler = new Handler(Looper.myLooper());
    private int deleyValue = 1;

    public FingerprintGesture(Context context) {
        this.context = context;
        toastManager = new T(context);
    }

    @Override
    public void onGestureDetectionAvailabilityChanged(boolean available) {
        super.onGestureDetectionAvailabilityChanged(available);
        //Toast.makeText(getApplicationContext(), "Gesture available change to: " + available, Toast.LENGTH_SHORT).show();
        T.log("LOG", "onGestureDetectionAvailabilityChanged " + available);
    }

    @Override
    public void onGestureDetected(int gesture) {
        super.onGestureDetected(gesture);
        T.log("LOG", "G " + gesture);
        if (gesture == 8){
            preventStatusBarExpansion(context);
        }
        /*
        if (none fast just one){

        }else{
            count++;
            toastManager.sT(String.valueOf(count), null);
            A.getVibrator().vibrate(50);
            checkAction("fast");
        }
        */
    }

    private void checkAction(String action){
        switch (action){
            case "fast":
                handler.removeCallbacks(runnableAction);
                handler.postDelayed(runnableAction, deleyValue * 1000);
                break;
            case "ones":
                for (InformationFPAction fp : A.getFingerActions()) {
                    if (Objects.equals(fp.getWhatFinger(), "ones")) {
                        workToDo(fp.getWhatAction(), fp.getPackageName());
                        return;
                    }
                }
                toastManager.sT(context.getString(R.string.toast_msg_noneAction),context.getDrawable(R.drawable.ic_block));
                break;
        }
    }

    private void workToDo(String whatAction,String packageName){

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

    private void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            // Use Fallback size:
            result = 60; // 60px Fallback
        }

        localLayoutParams.height = result;
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(context);
        manager.addView(view, localLayoutParams);
    }

    private static class CustomViewGroup extends ViewGroup {
        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            // Intercepted touch!
            return true;
        }
    }
}
