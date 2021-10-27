package com.app.dashwood.fingerprintsensor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.extra.ContextWrapper;
import com.app.dashwood.fingerprintsensor.log.T;

import java.util.Locale;

public class ActivityStartup extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnNext;
    private String numeric = "";
    private Typeface typeface;
    private Button btnPermission;
    private boolean check, checkFingerprint = false;
    private RadioButton rdSwipe, rdTouch;
    private Button btnAccessibility;
    private int currentPos;
    private FingerprintManager fingerprintManager;
    private T toast;
    private Button btnSecurityFingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        //typeface = Typeface.createFromAsset(getAssets(), "fonts/bkoodak.ttf");
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnNext = (Button) findViewById(R.id.btn_next);

        btnNext.setTypeface(typeface);
        if (Data.readPreferecesBoolean(this, getString(R.string.PREF_HOME_CHECKER_STARTUP),
                false, getString(R.string.KEY_CHECKER_STARTUP))) {
            startActivity(new Intent(ActivityStartup.this, ActivityMain.class));
            finish();
        }
        toast = new T(getApplicationContext());
        fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (!fingerprintManager.isHardwareDetected()) {
            toast.lT(getString(R.string.error_toast_fingerprint_notsupported), getDrawable(R.drawable.ic_finger_nocheck));
            //finish();
        }
        layouts = new int[]{
                R.layout.startup_page_one,
                R.layout.startup_page_two,
                R.layout.startup_page_three};
        //changeStatusBarColor();

        btnNext.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.VISIBLE);
        viewPager.setAdapter(new MyViewPagerAdapter());

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        addBottomDots(0);
        btnNext.setBackground(getDrawable(R.drawable.bg_button));
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        Data.saveToPrefermenceBoolean(this, getString(R.string.PREF_HOME_CHECKER_STARTUP),
                true, getString(R.string.KEY_CHECKER_STARTUP));
        startActivity(new Intent(ActivityStartup.this, ActivityMain.class));
        finish();


    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            pageSelected(position);
            currentPos = position;
            if (position == layouts.length - 1) {
                btnNext.setText(getString(R.string.btn_startup_start));
            } else {
                btnNext.setText(getString(R.string.btn_startup_next));
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void pageSelected(int position) {
        if (position == 1) {
            //swipe TRUE touch FALSE
            if (!rdSwipe.isChecked() && !rdTouch.isChecked()) {
                btnNext.setVisibility(View.GONE);
                viewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                rdSwipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rdTouch.setChecked(false);
                        saveToucher(true);
                        viewPager.setOnTouchListener(null);
                        btnNext.setVisibility(View.VISIBLE);
                    }
                });
                rdTouch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rdSwipe.setChecked(false);
                        saveToucher(false);
                        viewPager.setOnTouchListener(null);
                        btnNext.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else if (position == 2) {
            //btnNext.setVisibility(View.GONE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                btnSecurityFingerprint.setVisibility(View.VISIBLE);
            }

        }

    }

    private void saveToucher(boolean value) {
        T.log("LOG","WHAT ? : " + value);
        Data.saveToPrefermenceBoolean(getApplicationContext(), getString(R.string.PREF_HOME_TOUCHER_FINGERPRINT), value,
                getString(R.string.KEY_TOUCHER_FINGERPRINT));
    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d("LOG", "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.d("LOG", "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.d("LOG", "***ACCESSIBILIY IS ENABLED***: ");


            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d("LOG", "Setting: " + settingValue);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessabilityService = mStringColonSplitter.next();
                    Log.d("LOG", "Setting: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase("com.app.dashwood.fingerprintsensor/com.app.dashwood.fingerprintsensor.service.ServiceFingerprint")) {
                        Log.d("LOG", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            Log.d("LOG", "***END***");
        } else {
            Log.d("LOG", "***ACCESSIBILIY IS DISABLED***");
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        T.log("LOG", "POS " + currentPos);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (isAccessibilityEnabled() && fingerprintManager.hasEnrolledFingerprints()) {
            T.log("LOG", "ENABLE");
            if (btnNext != null)
                btnNext.setVisibility(View.VISIBLE);

        } else {
            T.log("LOG", "DISABLE");
            if (btnNext != null)
                btnNext.setVisibility(View.GONE);
        }
        if (isAccessibilityEnabled()) {
            if (btnAccessibility != null){
                btnAccessibility.setEnabled(false);
                btnAccessibility.setTextColor(Color.GREEN);
            }

        }
        if (fingerprintManager.hasEnrolledFingerprints()) {
            if (btnSecurityFingerprint != null){
                btnSecurityFingerprint.setEnabled(false);
                btnAccessibility.setTextColor(Color.GREEN);
            }


        }


    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private View view;

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            itemView(position);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        private void itemView(int position) {
            //Data save for Swipe == True and for Touch == False
            if (layouts[position] == layouts[1]) {
                rdSwipe = view.findViewById(R.id.rdSwipe);
                rdTouch = view.findViewById(R.id.rdTouch);
                TextView txtStartupTwoAndroid = view.findViewById(R.id.txtStartupTwoAndroid);
                TextView txtStartupForView = view.findViewById(R.id.txtStartupForView);
                if (Build.VERSION.SDK_INT < 26) {
                    rdSwipe.setEnabled(false);
                    txtStartupTwoAndroid.setVisibility(View.VISIBLE);
                    txtStartupForView.setVisibility(View.VISIBLE);
                }
            } else if (layouts[position] == layouts[2]) {
                btnAccessibility = view.findViewById(R.id.btnPermissionAccessibility);
                btnSecurityFingerprint = view.findViewById(R.id.btnSecurityFingerPrint);
                btnAccessibility.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }
                });
                btnSecurityFingerprint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                    }
                });

            }
        }

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
