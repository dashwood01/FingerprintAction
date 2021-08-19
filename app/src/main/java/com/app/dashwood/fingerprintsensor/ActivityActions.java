package com.app.dashwood.fingerprintsensor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.dashwood.fingerprintsensor.adapter.AdapterRecFPAction;
import com.app.dashwood.fingerprintsensor.adapter.AdapterRecShowActions;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.dataset.InformationActions;
import com.app.dashwood.fingerprintsensor.dataset.InformationFPAction;
import com.app.dashwood.fingerprintsensor.dataset.SynchronizedDatabase;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.extra.ContextWrapper;
import com.app.dashwood.fingerprintsensor.listener.OnClickDialogListener;
import com.app.dashwood.fingerprintsensor.log.DialogCustom;
import com.app.dashwood.fingerprintsensor.log.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityActions extends AppCompatActivity {

    private EditText edtNameAction, edtDesAction;
    private RadioButton rdCountOne, rdCountTwo, rdCountThree, rdCountFore, rdCountFive, rdCountSix, rdCountSeven, rdCountEight, rdCountNine;
    private RadioGroup rdGroup;
    private CheckBox chkDoApp, chkDoWork, chkDoCall, chkDoMessage, chkFailFinger, chkSuccessfulFinger, chkFastFinger;
    private AdapterRecShowActions adapterRecShowActions;
    private ArrayList<InformationActions> actions = new ArrayList<>();
    private RecyclerView recShowActions;
    private String whatFinger = "", whatAction = "";
    private boolean isEnableFastFinger = true, isEnableFailFinger = true, isEnableSuccessfulFinger = true;
    private int count;
    private T toastManager;
    private AdapterRecFPAction adapterRecFPAction;
    private SynchronizedDatabase synchronizedDatabase;
    private TextView txtShowAction;
    private int position;
    private InformationFPAction informationFPAction;
    private DialogCustom dialogCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        dialogCustom = new DialogCustom(this);
        Intent intent = getIntent();
        position = intent.getIntExtra(getString(R.string.intent_putextra_edit), -1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAction);
        setSupportActionBar(toolbar);
        synchronizedDatabase = new SynchronizedDatabase(this);
        getAllList();
        setViews();
        if (position >= 0) {
            informationFPAction = A.getFingerActions().get(position);
            edtNameAction.setText(informationFPAction.getName());
            edtDesAction.setText(informationFPAction.getDescription());
            whatFinger = informationFPAction.getWhatFinger();
            whatAction = informationFPAction.getWhatAction();
            count = informationFPAction.getCount();
            A.setActionvalue(informationFPAction.getPackageName());
            A.setDisplayName(informationFPAction.getDisplayName());
            switch (informationFPAction.getPackageName()) {
                case "wifi":
                    txtShowAction.setText("وای فای");
                    break;
                case "bluetooth":
                    txtShowAction.setText("بلوتوث");
                    break;
                case "flashlight":
                    txtShowAction.setText("چراغ قوه");
                    break;
                case "home":
                    txtShowAction.setText("صفحه اصلی");
                    break;
                case "lock":
                    txtShowAction.setText("قفل صفحه");
                    break;
                case "play":
                    txtShowAction.setText("پخش آهنگ");
                    break;
                case "stop":
                    txtShowAction.setText("توقف آهنگ");
                    break;
                case "next":
                    txtShowAction.setText("آهنگ بعدی");
                    break;
                case "backward":
                    txtShowAction.setText("آهنگ قبلی");
                    break;
                case "up":
                    txtShowAction.setText("تنظیم صدای زیاد");
                    break;
                case "down":
                    txtShowAction.setText("تنظیم صدای کم");
                    break;
                case "vibrate":
                    txtShowAction.setText("حالت ویبره");
                    break;
                default:
                    txtShowAction.setText(informationFPAction.getDisplayName() + " " + informationFPAction.getPackageName());
                    break;
            }
            switch (informationFPAction.getWhatFinger()) {
                case "fast":
                    chkFastFinger.setChecked(true);
                    chkFailFinger.setEnabled(false);
                    chkSuccessfulFinger.setEnabled(false);
                    codeFastfinger(informationFPAction, true);
                    break;
                case "succeful":
                    chkFastFinger.setEnabled(false);
                    chkFailFinger.setEnabled(false);
                    chkSuccessfulFinger.setChecked(true);
                    codeSucceedfinger(informationFPAction);
                    break;
                case "fail":
                    chkFastFinger.setEnabled(false);
                    chkFailFinger.setChecked(true);
                    chkSuccessfulFinger.setEnabled(false);
                    codeFailfinger(informationFPAction);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action, menu);
        if (position >= 0) {
            menu.findItem(R.id.btnAction_delete).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnAction_accept:
                if (!whatFinger.equals(getString(R.string.fingerFast))) {
                    checkValueAction(false);
                } else {
                    checkValueAction(true);
                }
                break;
            case R.id.btnAction_close:
                finish();
                break;
            case R.id.btnAction_delete:
                dialogCustom.setTitle(getString(R.string.dialog_title_warning_delete))
                        .setMessage(getString(R.string.dialog_warning_delete))
                        .setButtonPositive(getString(R.string.dialog_button_positive_yes))
                        .setButtonNegitive(getString(R.string.dialog_button_negative_no))
                        .setCancable(true)
                        .setIcon(getDrawable(R.drawable.ic_trash))
                        .setListenerAcceptButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                if (synchronizedDatabase.getWritableDatabase().deleteAction(informationFPAction.getId())) {
                                    ActivityActions.this.finish();
                                    toastManager.lT(getString(R.string.action_deleted), null);
                                    dialogCustom.dismass();
                                }
                            }
                        })
                        .setCancelButtonVisible(true)
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                dialogCustom.dismass();
                            }
                        })
                        .show();
                break;
        }
        return true;
    }

    private void setViews() {
        toastManager = new T(this);
        adapterRecFPAction = new AdapterRecFPAction(this);
        recShowActions = (RecyclerView) findViewById(R.id.recShowActions);
        edtNameAction = (EditText) findViewById(R.id.edtNameAction);
        edtDesAction = (EditText) findViewById(R.id.edtDesAction);
        rdCountOne = (RadioButton) findViewById(R.id.rdCountOne);
        rdCountTwo = (RadioButton) findViewById(R.id.rdCountTwo);
        rdCountThree = (RadioButton) findViewById(R.id.rdCountThree);
        rdCountFore = (RadioButton) findViewById(R.id.rdCountFore);
        rdCountFive = (RadioButton) findViewById(R.id.rdCountFive);
        rdCountSix = (RadioButton) findViewById(R.id.rdCountSix);
        rdCountSeven = (RadioButton) findViewById(R.id.rdCountSeven);
        rdCountEight = (RadioButton) findViewById(R.id.rdCountEight);
        rdCountNine = (RadioButton) findViewById(R.id.rdCountNine);
        rdGroup = (RadioGroup) findViewById(R.id.rdGroup);
        chkDoApp = (CheckBox) findViewById(R.id.chkDoApp);
        chkDoWork = (CheckBox) findViewById(R.id.chkDoWork);
        chkDoCall = (CheckBox) findViewById(R.id.chkDoCall);
        chkDoMessage = (CheckBox) findViewById(R.id.chkDoMessage);
        chkFailFinger = (CheckBox) findViewById(R.id.chkFailFinger);
        chkSuccessfulFinger = (CheckBox) findViewById(R.id.chkSuccefulFinger);
        chkFastFinger = (CheckBox) findViewById(R.id.chkFastFinger);
        txtShowAction = (TextView) findViewById(R.id.txtViewAction);
        adapterRecShowActions = new AdapterRecShowActions(this);
        recShowActions.setAdapter(adapterRecShowActions);
        recShowActions.setLayoutManager(new LinearLayoutManager(this));

        if (Data.readToPrefermenceInt(this, getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 3,
                getString(R.string.KEY_CHANGE_LANGUAGE)) == 1) {
            edtNameAction.setTextDirection(View.TEXT_DIRECTION_LTR);
            edtDesAction.setTextDirection(View.TEXT_DIRECTION_LTR);
            chkDoApp.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkDoWork.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkDoCall.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkDoMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkFailFinger.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkSuccessfulFinger.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkFastFinger.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            txtShowAction.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        int countFastFinger = 0;
        for (InformationFPAction fp : A.getFingerActions()) {
            if (fp.getWhatFinger().equals(getString(R.string.fingerSucceful))) {
                chkSuccessfulFinger.setEnabled(false);
                isEnableSuccessfulFinger = false;
            } else if (fp.getWhatFinger().equals(getString(R.string.fingerFail))) {
                chkFailFinger.setEnabled(false);
                isEnableFailFinger = false;
            } else if (fp.getWhatFinger().equals(getString(R.string.fingerFast))) {
                countFastFinger++;
            }
        }
        if (countFastFinger == 9) {
            chkFastFinger.setEnabled(false);
            isEnableFastFinger = false;
        }
        chkDoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeDoApp();
            }
        });

        chkDoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(true)) {
                    codeDoCall();
                }


            }
        });
        chkDoMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(false)) {
                    codeDoMessage();
                }
            }
        });
        chkDoWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeDoWork();

            }
        });
        chkFastFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatFinger = getString(R.string.fingerFast);
                codeFastFinger();
            }
        });
        chkSuccessfulFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatFinger = getString(R.string.fingerSucceful);
                codeSucceedFinger();
            }
        });
        chkFailFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatFinger = getString(R.string.fingerFail);
                codeFailFinger();
            }
        });
        rdCountOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 1;
            }
        });
        rdCountTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 2;
            }
        });
        rdCountThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 3;
            }
        });
        rdCountFore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 4;
            }
        });
        rdCountFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 5;
            }
        });
        rdCountSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 6;
            }
        });
        rdCountSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 7;
            }
        });
        rdCountEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 8;
            }
        });
        rdCountNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 9;
            }
        });
    }

    private void checkValueAction(boolean checkCount) {
        String actionValue = A.getActionvalue();
        String displayName = A.getDisplayName();
        if (edtNameAction.getText().toString().trim().length() > 0) {
            if (!whatFinger.equals("")) {
                if (checkCount) {
                    if (count != 0) {
                        if (!actionValue.equals("")) {
                            if (position >= 0) {
                                if (updateChecker(informationFPAction.getId(), true)) {
                                    getAllList();
                                    toastManager.sT(getString(R.string.toast_update_accept), getDrawable(R.drawable.ic_accept_toast));
                                    finish();
                                } else {
                                    toastManager.sT(getString(R.string.toast_update_dissmass), getDrawable(R.drawable.ic_close_toast));
                                }
                            } else {
                                insertAction(edtNameAction.getText().toString(), edtDesAction.getText().toString(),
                                        count, actionValue, whatFinger, whatAction, displayName);
                            }
                        } else {
                            toastManager.lT(getString(R.string.error_toast_DoAction), getDrawable(R.drawable.ic_block));
                        }
                    } else {
                        toastManager.lT(getString(R.string.error_toast_CountAction), getDrawable(R.drawable.ic_block));
                    }

                } else {
                    if (!actionValue.equals("")) {
                        if (position >= 0) {
                            if (updateChecker(informationFPAction.getId(), false)) {
                                getAllList();
                                toastManager.sT(getString(R.string.toast_update_accept), getDrawable(R.drawable.ic_accept_toast));
                                finish();
                            } else {
                                toastManager.sT(getString(R.string.toast_update_dissmass), getDrawable(R.drawable.ic_close_toast));
                            }
                        } else {
                            insertAction(edtNameAction.getText().toString(), edtDesAction.getText().toString(),
                                    count, actionValue, whatFinger, whatAction, displayName);
                        }
                    } else {
                        toastManager.lT(getString(R.string.error_toast_DoAction), getDrawable(R.drawable.ic_block));

                    }
                }
            } else {
                toastManager.lT(getString(R.string.error_toast_fingerprint), getDrawable(R.drawable.ic_block));
            }

        } else {
            toastManager.lT(getString(R.string.error_toast_NameAction), getDrawable(R.drawable.ic_block));
        }
    }

    private void insertAction(String name, String description, int count, String packageName,
                              String whatFinger, String whatAction, String displayName) {
        String checkDone = synchronizedDatabase.getWritableDatabase().insertAction(name, description, count, packageName, whatFinger, whatAction, displayName);
        T.log("LOg", whatAction);
        if (checkDone.equals("done")) {
            toastManager.sT(getString(R.string.succeful_toast_insert), getDrawable(R.drawable.ic_accept_toast));
            getAllList();
            finish();
        } else {
            toastManager.lT(getString(R.string.error_toast_not_insert) + checkDone, null);
        }
    }

    private void getAllList() {
        A.setFingerActions(synchronizedDatabase.getWritableDatabase().getAllAction());
    }

    private void codeDoCall() {
        if (chkDoCall.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(this, getString(R.string.PREF_HOME_CHECKER_DO_CALL),
                    true, getString(R.string.KEY_CHECKER_DO_CALL));
            if (checkHelp) {
                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                        .setMessage(getString(R.string.dialog_example_message_docall))
                        .setIcon(getDrawable(R.drawable.ic_help_alert))
                        .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                        .setCancable(false)
                        .setCancelButtonVisible(true)
                        .setListenerAcceptButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                buildRecCall();
                                dialogCustom.dismass();
                            }
                        })
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                Data.saveToPrefermenceBoolean(ActivityActions.this, getString(R.string.PREF_HOME_CHECKER_DO_CALL),
                                        false, getString(R.string.KEY_CHECKER_DO_CALL));
                                buildRecCall();
                                dialogCustom.dismass();
                            }
                        })
                        .show();
            } else {
                buildRecCall();
            }
        } else {
            chkDoWork.setEnabled(true);
            chkDoApp.setEnabled(true);
            chkDoMessage.setEnabled(true);
            actions.clear();
            adapterRecShowActions.sendActions(actions);

        }
    }

    private void buildRecCall() {
        actions.clear();
        whatAction = getString(R.string.doCall);
        Drawable ic_contact = getDrawable(R.drawable.ic_contacts);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Cursor phones = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                "display_name ASC");
        if (phones != null) {
            while (phones.moveToNext()) {
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (phones.getInt(phones.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            InformationActions informationActions = new InformationActions();
                            informationActions.setName(name);
                            informationActions.setPhoneNumber(phoneNo);
                            informationActions.setIcon(ic_contact);
                            informationActions.setActionValueView(4);
                            actions.add(informationActions);
                        }
                    }
                    if (pCur != null) {
                        pCur.close();
                    }
                }
            }
            phones.close();
        }
        adapterRecShowActions.sendActions(actions);
        chkDoWork.setEnabled(false);
        chkDoApp.setEnabled(false);
        chkDoMessage.setEnabled(false);

    }

    private void codeDoMessage() {
        if (chkDoMessage.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(ActivityActions.this, getString(R.string.PREF_HOME_CHECKER_DO_MESSAGE),
                    true, getString(R.string.KEY_CHECKER_DO_MESSAGE));
            if (checkHelp) {
                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                        .setMessage(getString(R.string.dialog_example_message_domessage))
                        .setIcon(getDrawable(R.drawable.ic_help_alert))
                        .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                        .setCancable(false)
                        .setCancelButtonVisible(true)
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                Data.saveToPrefermenceBoolean(ActivityActions.this, getString(R.string.PREF_HOME_CHECKER_DO_MESSAGE),
                                        false, getString(R.string.KEY_CHECKER_DO_MESSAGE));
                                buildRecMessage();
                                dialogCustom.dismass();
                            }
                        })
                        .setListenerAcceptButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                buildRecMessage();
                                dialogCustom.dismass();
                            }
                        })
                        .show();
            } else {
                buildRecMessage();
            }
        } else {
            chkDoWork.setEnabled(true);
            chkDoApp.setEnabled(true);
            chkDoCall.setEnabled(true);
            actions.clear();
            adapterRecShowActions.sendActions(actions);
        }
    }

    private void buildRecMessage() {
        actions.clear();
        ContentResolver cr = getContentResolver();
        whatAction = getString(R.string.doMessage);
        Drawable ic_contact = getDrawable(R.drawable.ic_message);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Cursor phones = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                "display_name ASC");
        if (phones != null) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                if (phones.getInt(phones.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            InformationActions informationActions = new InformationActions();
                            informationActions.setName(name);
                            informationActions.setPhoneNumber(phoneNo);
                            informationActions.setIcon(ic_contact);
                            informationActions.setActionValueView(4);
                            actions.add(informationActions);
                        }
                    }
                    if (pCur != null) {
                        pCur.close();
                    }
                }
            }
            phones.close();
        }
        adapterRecShowActions.sendActions(actions);
        chkDoWork.setEnabled(false);
        chkDoApp.setEnabled(false);
        chkDoCall.setEnabled(false);
    }

    private void codeDoApp() {
        if (chkDoApp.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(ActivityActions.this, getString(R.string.PREF_HOME_CHECKER_DO_APP),
                    true, getString(R.string.KEY_CHECKER_DO_APP));
            if (checkHelp) {
                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                        .setMessage(getString(R.string.dialog_example_message_doapp))
                        .setIcon(getDrawable(R.drawable.ic_help_alert))
                        .setCancelButtonVisible(true)
                        .setCancable(false)
                        .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                        .setListenerAcceptButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                buildRecApp();
                                dialogCustom.dismass();
                            }
                        })
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                Data.saveToPrefermenceBoolean(ActivityActions.this, getString(R.string.PREF_HOME_CHECKER_DO_APP),
                                        false, getString(R.string.KEY_CHECKER_DO_APP));
                                buildRecApp();
                                dialogCustom.dismass();
                            }
                        })
                        .show();
            } else {
                buildRecApp();
            }
        } else {
            chkDoWork.setEnabled(true);
            chkDoCall.setEnabled(true);
            chkDoMessage.setEnabled(true);
            actions.clear();
            adapterRecShowActions.sendActions(actions);

        }
    }

    private void buildRecApp() {
        actions.clear();
        whatAction = getString(R.string.doApp);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo pkgInfo : packages) {
            if ((pkgInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                InformationActions informationActions = new InformationActions();
                informationActions.setName(pkgInfo.packageName);
                informationActions.setIcon(pkgInfo.loadIcon(getPackageManager()));
                informationActions.setActionValueView(2);
                actions.add(informationActions);
            } else if ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                InformationActions informationActions = new InformationActions();
                informationActions.setName(pkgInfo.packageName);
                informationActions.setIcon(pkgInfo.loadIcon(getPackageManager()));
                informationActions.setActionValueView(2);
                actions.add(informationActions);
            }
        }
        adapterRecShowActions.sendActions(actions);
        chkDoWork.setEnabled(false);
        chkDoCall.setEnabled(false);
        chkDoMessage.setEnabled(false);
    }

    private void codeDoWork() {
        if (chkDoWork.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(ActivityActions.this, getString(R.string.PREF_HOME_CHECKER_DO_WORK),
                    true, getString(R.string.KEY_CHECKER_DO_WORK));
            if (checkHelp) {
                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                        .setMessage(getString(R.string.dialog_example_message_dowork))
                        .setIcon(getDrawable(R.drawable.ic_help_alert))
                        .setCancelButtonVisible(true)
                        .setCancable(false)
                        .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                Data.saveToPrefermenceBoolean(ActivityActions.this, getString(R.string.PREF_HOME_CHECKER_DO_WORK),
                                        false, getString(R.string.KEY_CHECKER_DO_WORK));
                                buildRecWork();
                                dialogCustom.dismass();
                            }
                        })
                        .setListenerAcceptButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                buildRecWork();
                                dialogCustom.dismass();
                            }
                        })
                        .show();
            } else {
                buildRecWork();
            }
        } else {
            chkDoMessage.setEnabled(true);
            chkDoApp.setEnabled(true);
            chkDoCall.setEnabled(true);
            actions.clear();
            adapterRecShowActions.sendActions(actions);
        }
    }

    private void buildRecWork() {
        actions.clear();
        whatAction = getString(R.string.doWork);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        String[] nameList = getResources().getStringArray(R.array.actions_work);
        int[] iconList = {R.drawable.ic_wifi, R.drawable.ic_bluetooth, R.drawable.ic_flashlight, R.drawable.ic_home,
                R.drawable.ic_lock, R.drawable.ic_music, R.drawable.ic_vibrate, R.drawable.ic_volume};

        for (int i = 0; i < nameList.length; i++) {
            InformationActions informationActions = new InformationActions();
            informationActions.setName(nameList[i]);
            informationActions.setIcon(getDrawable(iconList[i]));
            informationActions.setPhoneNumber("" + i);
            informationActions.setActionValueView(1);
            informationActions.setChild((byte) i);
            actions.add(informationActions);
        }
        adapterRecShowActions.sendActions(actions);
        chkDoMessage.setEnabled(false);
        chkDoApp.setEnabled(false);
        chkDoCall.setEnabled(false);
    }

    private void codeFastFinger() {
        if (chkFastFinger.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(ActivityActions.this, getResources().getString(R.string.PREF_HOME_CHECKER_FASTFINGER), true,
                    getResources().getString(R.string.KEY_CHECKER_FASTFINGER));
            if (checkHelp) {
                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                        .setMessage(getString(R.string.dialog_example_message_fastfinger))
                        .setIcon(getDrawable(R.drawable.ic_help_alert))
                        .setCancelButtonVisible(true)
                        .setCancable(false)
                        .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(getResources().getString(R.string.dialog_button_negative_text_check))
                        .setListenerAcceptButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                dialogCustom.dismass();
                            }
                        })
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                Data.saveToPrefermenceBoolean(ActivityActions.this, getResources().getString(R.string.PREF_HOME_CHECKER_FASTFINGER), false,
                                        getResources().getString(R.string.KEY_CHECKER_FASTFINGER));
                                dialogCustom.dismass();
                            }
                        })
                        .show();
            }

            for (int i = 0; i < rdGroup.getChildCount(); i++) {
                rdGroup.getChildAt(i).setEnabled(true);
            }
            for (InformationFPAction informationFPAction : A.getFingerActions()) {
                switch (informationFPAction.getCount()) {
                    case 1:
                        rdCountOne.setEnabled(false);
                        break;
                    case 2:
                        rdCountTwo.setEnabled(false);
                        break;
                    case 3:
                        rdCountThree.setEnabled(false);
                        break;
                    case 4:
                        rdCountFore.setEnabled(false);
                        break;
                    case 5:
                        rdCountFive.setEnabled(false);
                        break;
                    case 6:
                        rdCountSix.setEnabled(false);
                        break;
                    case 7:
                        rdCountSeven.setEnabled(false);
                        break;
                    case 8:
                        rdCountEight.setEnabled(false);
                        break;
                    case 9:
                        rdCountNine.setEnabled(false);
                        break;
                }
            }

            chkFailFinger.setEnabled(false);
            chkSuccessfulFinger.setEnabled(false);

        } else {
            for (int i = 0; i < rdGroup.getChildCount(); i++) {
                rdGroup.getChildAt(i).setEnabled(false);
            }
            if (isEnableSuccessfulFinger) {
                chkSuccessfulFinger.setEnabled(true);
            }
            if (isEnableFailFinger) {
                chkFailFinger.setEnabled(true);
            }

        }
    }

    private void codeSucceedFinger() {
        if (chkSuccessfulFinger.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(ActivityActions.this, getResources().getString(R.string.PREF_HOME_CHECKER_SUCCEFULFINGER), true,
                    getResources().getString(R.string.KEY_CHECKER_SUCCEFULFINGER));
            if (checkHelp) {
                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                        .setMessage(getString(R.string.dialog_example_message_succefulfinger))
                        .setIcon(getDrawable(R.drawable.ic_help_alert))
                        .setCancelButtonVisible(true)
                        .setCancable(false)
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
                                Data.saveToPrefermenceBoolean(ActivityActions.this, getResources().getString(R.string.PREF_HOME_CHECKER_SUCCEFULFINGER), false,
                                        getResources().getString(R.string.KEY_CHECKER_SUCCEFULFINGER));
                                dialogCustom.dismass();
                            }
                        })
                        .show();
            }
            chkFailFinger.setEnabled(false);
            chkFastFinger.setEnabled(false);
        } else {
            if (isEnableFastFinger) {
                chkFastFinger.setEnabled(true);
            }
            if (isEnableFailFinger) {
                chkFailFinger.setEnabled(true);
            }

        }
    }

    private void codeFailFinger() {
        if (chkFailFinger.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(ActivityActions.this, getResources().getString(R.string.PREF_HOME_CHECKER_FAILFINGER), true,
                    getResources().getString(R.string.KEY_CHECKER_FAILFINGER));
            if (checkHelp) {
                dialogCustom.setTitle(getString(R.string.dialog_example_title))
                        .setMessage(getString(R.string.dialog_example_message_failfinger))
                        .setIcon(getDrawable(R.drawable.ic_help_alert))
                        .setCancelButtonVisible(true)
                        .setCancable(false)
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
                                Data.saveToPrefermenceBoolean(ActivityActions.this, getResources().getString(R.string.PREF_HOME_CHECKER_FAILFINGER), false,
                                        getResources().getString(R.string.KEY_CHECKER_FAILFINGER));
                                dialogCustom.dismass();
                            }
                        })
                        .show();

            }
            chkSuccessfulFinger.setEnabled(false);
            chkFastFinger.setEnabled(false);
        } else {
            if (isEnableSuccessfulFinger) {
                chkSuccessfulFinger.setEnabled(true);
            }
            if (isEnableFastFinger) {
                chkFastFinger.setEnabled(true);
            }

        }
    }

    private void codeFastfinger(InformationFPAction informationFPAction, boolean firstTime) {
        for (int i = 0; i < rdGroup.getChildCount(); i++) {
            rdGroup.getChildAt(i).setEnabled(true);
        }
        for (InformationFPAction fp : A.getFingerActions()) {
            switch (fp.getCount()) {
                case 1:
                    rdCountOne.setEnabled(false);
                    break;
                case 2:
                    rdCountTwo.setEnabled(false);
                    break;
                case 3:
                    rdCountThree.setEnabled(false);
                    break;
                case 4:
                    rdCountFore.setEnabled(false);
                    break;
                case 5:
                    rdCountFive.setEnabled(false);
                    break;
                case 6:
                    rdCountSix.setEnabled(false);
                    break;
                case 7:
                    rdCountSeven.setEnabled(false);
                    break;
                case 8:
                    rdCountEight.setEnabled(false);
                    break;
                case 9:
                    rdCountNine.setEnabled(false);
                    break;
            }
        }
        if (firstTime) {
            switch (informationFPAction.getCount()) {
                case 1:
                    rdCountOne.setEnabled(true);
                    rdCountOne.setChecked(true);
                    break;
                case 2:
                    rdCountTwo.setEnabled(true);
                    rdCountTwo.setChecked(true);
                    break;
                case 3:
                    rdCountThree.setEnabled(true);
                    rdCountThree.setChecked(true);
                    break;
                case 4:
                    rdCountFore.setEnabled(true);
                    rdCountFore.setChecked(true);
                    break;
                case 5:
                    rdCountFive.setEnabled(true);
                    rdCountFive.setChecked(true);
                    break;
                case 6:
                    rdCountSix.setEnabled(true);
                    rdCountSix.setChecked(true);
                    break;
                case 7:
                    rdCountSeven.setEnabled(true);
                    rdCountSeven.setChecked(true);
                    break;
                case 8:
                    rdCountEight.setEnabled(true);
                    rdCountEight.setChecked(true);
                    break;
                case 9:
                    rdCountNine.setEnabled(true);
                    rdCountNine.setChecked(true);
                    break;
            }
        }
        checkWhatAction(informationFPAction);
    }

    private void checkWhatAction(InformationFPAction informationFPAction) {
        switch (informationFPAction.getWhatAction()) {
            case "work":
                chkDoWork.setChecked(true);
                chkDoApp.setEnabled(false);
                chkDoCall.setEnabled(false);
                chkDoMessage.setEnabled(false);
                buildRecWork();
                break;
            case "app":
                chkDoApp.setChecked(true);
                chkDoWork.setEnabled(false);
                chkDoCall.setEnabled(false);
                chkDoMessage.setEnabled(false);
                buildRecApp();
                break;
            case "call":
                chkDoCall.setChecked(true);
                chkDoApp.setEnabled(false);
                chkDoMessage.setEnabled(false);
                chkDoWork.setEnabled(false);
                buildRecCall();
                break;
            case "message":
                chkDoMessage.setChecked(true);
                chkDoCall.setEnabled(false);
                chkDoWork.setEnabled(false);
                chkDoApp.setEnabled(false);
                buildRecMessage();
                break;
        }
    }

    private boolean updateChecker(int id, boolean checkCount) {
        String actionValue = A.getActionvalue();
        String displayName = A.getDisplayName();
        if (edtNameAction.getText().toString().trim().length() > 0) {
            if (!whatFinger.equals("")) {
                if (checkCount) {
                    if (count != 0) {
                        if (!actionValue.equals("")) {
                            return updateAction(id, edtNameAction.getText().toString(), edtDesAction.getText().toString(),
                                    count, actionValue, whatFinger, whatAction, displayName);
                        } else {
                            toastManager.lT(getString(R.string.error_toast_DoAction), null);
                        }
                    } else {
                        toastManager.lT(getString(R.string.error_toast_CountAction), null);
                    }

                } else {
                    if (!actionValue.equals("")) {
                        T.log("LOG", "COUNT DISABLE");
                        return updateAction(id, edtNameAction.getText().toString(), edtDesAction.getText().toString(),
                                0, actionValue, whatFinger, whatAction, displayName);
                    } else {
                        toastManager.lT(getString(R.string.error_toast_DoAction), null);
                    }
                }
            } else {
                toastManager.lT(getString(R.string.error_toast_fingerprint), null);
            }

        } else {
            toastManager.lT(getString(R.string.error_toast_NameAction), null);
        }
        return false;
    }

    private boolean updateAction(int id, String name, String description, int count, String packageName,
                                 String whatFinger, String whatAction, String displayName) {
        return synchronizedDatabase.getWritableDatabase().updateAction(id, name, description, count, packageName, whatFinger, whatAction, displayName);
    }

    private void codeSucceedfinger(InformationFPAction informationFPAction) {
        chkFailFinger.setEnabled(false);
        chkFastFinger.setEnabled(false);
        chkSuccessfulFinger.setChecked(true);
        chkSuccessfulFinger.setEnabled(true);
        checkWhatAction(informationFPAction);
    }

    private void codeFailfinger(InformationFPAction informationFPAction) {
        chkSuccessfulFinger.setEnabled(false);
        chkFastFinger.setEnabled(false);
        chkFailFinger.setChecked(true);
        chkFailFinger.setEnabled(true);
        checkWhatAction(informationFPAction);
    }

    private boolean checkPermission(boolean checkCall) {
        if (checkCall) {
            if (checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    || checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS};
                requestPermissions(permissions, getResources().getInteger(R.integer.REQUEST_CODE_ASK_PERMISSIONS_CALL));
                return false;
            }
        } else {
            if (checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                String[] permission = {Manifest.permission.READ_CONTACTS};
                requestPermissions(permission, getResources().getInteger(R.integer.REQUEST_CODE_ASK_PERMISSIONS));
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Map<String, Integer> perms = new HashMap<>();
        switch (requestCode) {
            case 1:
                perms.clear();
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        codeDoMessage();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                            dialogCustom.setTitle(getString(R.string.dialog_title_permission))
                                    .setMessage(getString(R.string.permissionAlert))
                                    .setIcon(getDrawable(R.drawable.ic_security))
                                    .setCancelButtonVisible(true)
                                    .setCancable(false)
                                    .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                                    .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                                    .setListenerAcceptButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkPermission(false);
                                        }
                                    })
                                    .setListenerCancelButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            T.dialogQuestionShow(ActivityActions.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
                                                    getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    toastManager.lT(getString(R.string.permission_toast_getpermission), getDrawable(R.drawable.ic_block));
                                                                    if (chkDoMessage != null && chkDoCall != null) {
                                                                        chkDoMessage.setChecked(false);
                                                                        chkDoCall.setChecked(false);
                                                                    }
                                                                    dialogCustom.dismass();
                                                                    break;
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    checkPermission(false);
                                                                    dialogCustom.dismass();
                                                                    break;
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .show();
                        } else {
                            toastManager.lT(getString(R.string.permissionDenied), getDrawable(R.drawable.ic_block));
                            if (chkDoMessage != null && chkDoCall != null) {
                                chkDoMessage.setChecked(false);
                                chkDoCall.setChecked(false);
                            }
                        }
                    }

                }
                break;
            case 2:
                perms.clear();
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        codeDoCall();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                            dialogCustom.setTitle(getString(R.string.dialog_title_permission))
                                    .setMessage(getString(R.string.permissionAlert))
                                    .setIcon(getDrawable(R.drawable.ic_security))
                                    .setCancelButtonVisible(true)
                                    .setCancable(false)
                                    .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                                    .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                                    .setListenerAcceptButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkPermission(true);
                                        }
                                    })
                                    .setListenerCancelButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            T.dialogQuestionShow(ActivityActions.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
                                                    getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    toastManager.lT(getString(R.string.permission_toast_getpermission), getDrawable(R.drawable.ic_block));
                                                                    if (chkDoMessage != null && chkDoCall != null) {
                                                                        chkDoMessage.setChecked(false);
                                                                        chkDoCall.setChecked(false);
                                                                    }
                                                                    dialogCustom.dismass();
                                                                    break;
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    checkPermission(true);
                                                                    dialogCustom.dismass();
                                                                    break;
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .show();
                        } else {
                            toastManager.lT(getString(R.string.permissionDenied), getDrawable(R.drawable.ic_block));
                            if (chkDoCall != null && chkDoMessage != null) {
                                chkDoCall.setChecked(false);
                                chkDoMessage.setChecked(false);
                            }
                        }
                    }

                }
                break;
            case 3:
                perms.clear();
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        adapterRecFPAction.buildRecMessage();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                            dialogCustom.setTitle(getString(R.string.dialog_title_permission))
                                    .setMessage(getString(R.string.permissionAlert))
                                    .setIcon(getDrawable(R.drawable.ic_security))
                                    .setCancelButtonVisible(true)
                                    .setCancable(false)
                                    .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                                    .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                                    .setListenerAcceptButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkPermission(true);
                                        }
                                    })
                                    .setListenerCancelButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            T.dialogQuestionShow(ActivityActions.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
                                                    getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    toastManager.lT(getString(R.string.permission_toast_getpermission), getDrawable(R.drawable.ic_block));
                                                                    adapterRecFPAction.unCheckDoCallOrMessage();
                                                                    dialogCustom.dismass();
                                                                    break;
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    adapterRecFPAction.checkPermission(false);
                                                                    dialogCustom.dismass();
                                                                    break;
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .show();
                        } else {
                            toastManager.lT(getString(R.string.permissionDenied), getDrawable(R.drawable.ic_block));
                            adapterRecFPAction.unCheckDoCallOrMessage();
                        }
                    }
                }
                break;
            case 4:
                perms.clear();
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        adapterRecFPAction.buildRecCall();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                            dialogCustom.setTitle(getString(R.string.dialog_title_permission))
                                    .setMessage(getString(R.string.permissionAlert))
                                    .setIcon(getDrawable(R.drawable.ic_security))
                                    .setCancelButtonVisible(true)
                                    .setCancable(false)
                                    .setButtonPositive(getString(R.string.dialog_button_positive_text_check))
                                    .setButtonNegitive(getString(R.string.dialog_button_negative_text_check))
                                    .setListenerAcceptButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkPermission(true);
                                        }
                                    })
                                    .setListenerCancelButton(new OnClickDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            T.dialogQuestionShow(ActivityActions.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
                                                    getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    toastManager.lT(getString(R.string.permission_toast_getpermission), getDrawable(R.drawable.ic_block));
                                                                    adapterRecFPAction.unCheckDoCallOrMessage();
                                                                    dialogCustom.dismass();
                                                                    break;
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    adapterRecFPAction.checkPermission(true);
                                                                    dialogCustom.dismass();
                                                                    break;
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .show();
                        } else {
                            toastManager.lT(getString(R.string.permissionDenied), getDrawable(R.drawable.ic_block));
                            adapterRecFPAction.unCheckDoCallOrMessage();
                        }
                    }

                }
                break;
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
