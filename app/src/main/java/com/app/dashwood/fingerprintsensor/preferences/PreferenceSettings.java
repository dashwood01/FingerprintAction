package com.app.dashwood.fingerprintsensor.preferences;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.dashwood.fingerprintsensor.ActivityMain;
import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.extra.ContextWrapper;
import com.app.dashwood.fingerprintsensor.listener.OnClickDialogListener;
import com.app.dashwood.fingerprintsensor.log.DialogCustom;

import java.util.Locale;


public class PreferenceSettings extends PreferenceActivity {
    private DialogCustom dialogCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceSettingsFragment()).commit();
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        View content = root.getChildAt(0);
        LinearLayout toolbarContainer = (LinearLayout) View.inflate(this, R.layout.appbar_setting, null);
        root.removeAllViews();
        toolbarContainer.addView(content);
        root.addView(toolbarContainer);
        Toolbar mToolBar = (Toolbar) toolbarContainer.findViewById(R.id.toolbarSetting);
        mToolBar.setTitle(getString(R.string.action_settings));
        mToolBar.setNavigationIcon(R.drawable.ic_back);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialogCustom = new DialogCustom(this);

        if (!Data.readPreferecesBoolean(this, getString(R.string.PREF_HOME_CHECKER_HELP_SETTING),
                false, getString(R.string.KEY_CHECKER_HELP_SETTING))) {
            dialogCustom.setTitle(getString(R.string.dialog_title_for_help))
                    .setMessage(getString(R.string.dialog_message_help_setting))
                    .setIcon(getDrawable(R.drawable.ic_help_alert))
                    .setCancable(false)
                    .setButtonPositive(getString(R.string.dialog_button_positive_yes))
                    .setListenerAcceptButton(new OnClickDialogListener() {
                        @Override
                        public void onClick(View v) {
                            Data.saveToPrefermenceBoolean(PreferenceSettings.this, getString(R.string.PREF_HOME_CHECKER_HELP_SETTING),
                                    true, getString(R.string.KEY_CHECKER_HELP_SETTING));
                            dialogCustom.dismass();
                        }
                    })
                    .show();
        }


    }


    public static class PreferenceSettingsFragment extends PreferenceFragment {
        private SwitchPreference switchAdmin;
        private SwitchPreference switchTimerSensor;
        private EditTextPreference edtTimerSensor;
        private DialogCustom dialogCustom;
        private ListPreference listLanguage;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            dialogCustom = new DialogCustom(getActivity());
            switchAdmin = (SwitchPreference) findPreference(getString(R.string.preference_key_admin));
            if (switchAdmin != null) {
                switchAdmin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, A.getComponentName());
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.add_admin_extra_app_text));
                        startActivityForResult(intent, getResources().getInteger(R.integer.REQUEST_CODE_ENABLE_ADMIN));
                        return true;
                    }
                });
            }
            edtTimerSensor = (EditTextPreference) findPreference(getString(R.string.preference_key_edt_timer_samsung));
            switchTimerSensor = (SwitchPreference) findPreference(getString(R.string.preference_key_check_front_fp));
            listLanguage = (ListPreference) findPreference(getString(R.string.preference_key_language));

            if (edtTimerSensor != null) {
                switchTimerSensor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (switchTimerSensor.isChecked()) {
                            if (Data.readPreferecesBoolean(getContext(), getString(R.string.PREF_HOME_CHECKER_FRONT_FP), true,
                                    getString(R.string.KEY_CHECKER_FRONT_FP))) {
                                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                                        .setMessage(getString(R.string.preference_summery_check_front_fp_help))
                                        .setIcon(getActivity().getDrawable(R.drawable.ic_help_alert))
                                        .setCancable(false)
                                        .setCancelButtonVisible(true)
                                        .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                                        .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                                        .setListenerAcceptButton(new OnClickDialogListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogCustom.dismass();
                                            }
                                        })
                                        .setListenerCancelButton(new OnClickDialogListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Data.saveToPrefermenceBoolean(getContext(), getString(R.string.PREF_HOME_CHECKER_FRONT_FP), false,
                                                        getString(R.string.KEY_CHECKER_FRONT_FP));
                                                dialogCustom.dismass();
                                            }
                                        })
                                        .show();
                            }
                            edtTimerSensor.setEnabled(true);
                        } else {
                            edtTimerSensor.setEnabled(false);
                        }
                        return true;
                    }
                });
            }
            if (edtTimerSensor != null && switchTimerSensor != null) {
                if (switchTimerSensor.isChecked()) {
                    edtTimerSensor.setEnabled(true);
                }
            }

            listLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (Data.readToPrefermenceInt(getContext(), getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 0,
                            getString(R.string.KEY_CHANGE_LANGUAGE))==0){
                    }
                    if (newValue.equals(getString(R.string.equals_persian))) {
                        Data.saveToPrefermenceInt(getContext(), getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 0,
                                getString(R.string.KEY_CHANGE_LANGUAGE));
                    } else {
                        Data.saveToPrefermenceInt(getContext(), getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 1,
                                getString(R.string.KEY_CHANGE_LANGUAGE));
                    }
                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
            });


        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.preference_margin), getResources().getDisplayMetrics());
                view.setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);
            }
            return view;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case 47:
                    if (resultCode == Activity.RESULT_OK) {
                        switchAdmin.setChecked(true);
                        Toast.makeText(getContext(), getString(R.string.toast_admin_for_disable), Toast.LENGTH_LONG).show();
                    } else {
                        switchAdmin.setChecked(false);
                    }
                    break;
            }
        }
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
