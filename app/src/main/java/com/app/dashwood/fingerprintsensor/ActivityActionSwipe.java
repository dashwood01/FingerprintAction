package com.app.dashwood.fingerprintsensor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.dashwood.fingerprintsensor.adapter.AdapterRecShowActions;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.dataset.InformationActions;
import com.app.dashwood.fingerprintsensor.extra.ContextWrapper;
import com.app.dashwood.fingerprintsensor.listener.OnClickDialogListener;
import com.app.dashwood.fingerprintsensor.log.DialogCustom;
import com.app.dashwood.fingerprintsensor.log.T;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityActionSwipe extends AppCompatActivity {

    private EditText edtNameAction, edtDesAction;
    private ViewGroup layoutFast, layoutOnce;
    private CheckBox chkSwipeFast, chkSwipeOnce;
    private CheckBox chkSwipeUp, chkSwipeDown, chkSwipeRight, chkSwipeLeft;
    private CheckBox chkDoWork, chkDoApp, chkDoCall, chkDoMessage;
    private RadioButton rdCountOne, rdCountTwo, rdCountThree, rdCountFore, rdCountFive, rdCountSix, rdCountSeven, rdCountEight, rdCountNine;
    private Toolbar toolbar;
    private Button btnAccept, btnCancel;
    private RecyclerView recShowActions;
    private TextView txtViewAction;
    private T toastManager;

    private EditText edtSearch;

    private String whatAction = "";

    private int position = -1;

    private ArrayList<InformationActions> actions = new ArrayList<>();
    private AdapterRecShowActions adapterRecShowActions;

    private ArrayList<InformationActions> listSearch = new ArrayList<>();

    private DialogCustom dialogCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_swipe);
        toolbar = (Toolbar) findViewById(R.id.toolbarAction);
        setSupportActionBar(toolbar);
        adapterRecShowActions = new AdapterRecShowActions(this);
        setViews();


        dialogCustom = new DialogCustom(this);

        clickableViews();
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
                break;
            case R.id.btnAction_close:
                break;
            case R.id.btnAction_delete:
                break;

        }
        return true;
    }

    private void setViews() {
        toastManager = new T(this);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        //adapterRecFPAction = new AdapterRecFPAction(this);
        recShowActions = (RecyclerView) findViewById(R.id.recShowActions);
        recShowActions.setAdapter(adapterRecShowActions);
        recShowActions.setLayoutManager(new LinearLayoutManager(this));
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
        //rdGroup = (RadioGroup) findViewById(R.id.rdGroup);
        chkDoApp = (CheckBox) findViewById(R.id.chkDoApp);
        chkDoWork = (CheckBox) findViewById(R.id.chkDoWork);
        chkDoCall = (CheckBox) findViewById(R.id.chkDoCall);
        chkDoMessage = (CheckBox) findViewById(R.id.chkDoMessage);
        chkSwipeFast = (CheckBox) findViewById(R.id.chkFastSwipe);
        chkSwipeOnce = (CheckBox) findViewById(R.id.chkOnesSwipe);
        chkSwipeUp = (CheckBox) findViewById(R.id.chkSwipeUp);
        chkSwipeDown = (CheckBox) findViewById(R.id.chkSwipeDown);
        chkSwipeLeft = (CheckBox) findViewById(R.id.chkSwipeLeft);
        chkSwipeRight = (CheckBox) findViewById(R.id.chkSwipeRight);
        layoutFast = (ViewGroup) findViewById(R.id.layoutFast);
        layoutOnce = (ViewGroup) findViewById(R.id.layoutOnce);
        txtViewAction = (TextView) findViewById(R.id.txtViewAction);

        if (Data.readToPrefermenceInt(this, getString(R.string.PREF_HOME_CHANGE_LANGUAGE), 3,
                getString(R.string.KEY_CHANGE_LANGUAGE)) != 0) {
            chkSwipeFast.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkSwipeOnce.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkSwipeUp.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkSwipeDown.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkSwipeLeft.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkSwipeRight.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkDoApp.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkDoCall.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkDoMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chkDoWork.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        // adapterRecShowActions = new AdapterRecShowActions(this);
        //recShowActions.setAdapter(adapterRecShowActions);
        //recShowActions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void clickableViews() {
        chkSwipeFast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkSwipeOnce.setChecked(false);
                    layoutOnce.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeInDown)
                            .duration(100)
                            .repeat(0)
                            .playOn(findViewById(R.id.layoutFast));
                    layoutFast.setVisibility(View.VISIBLE);
                } else {
                    layoutFast.setVisibility(View.GONE);
                }
            }
        });
        chkSwipeOnce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkSwipeFast.setChecked(false);
                    layoutFast.setVisibility(View.GONE);
                    layoutOnce.setVisibility(View.VISIBLE);
                } else {
                    layoutOnce.setVisibility(View.GONE);

                }
            }
        });

        chkSwipeUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkSwipeDown.setChecked(false);
                    chkSwipeLeft.setChecked(false);
                    chkSwipeRight.setChecked(false);

                    chkDoApp.setEnabled(true);
                    chkDoMessage.setEnabled(true);
                    chkDoWork.setEnabled(true);
                    chkDoCall.setEnabled(true);
                } else {
                    chkDoApp.setEnabled(false);
                    chkDoMessage.setEnabled(false);
                    chkDoWork.setEnabled(false);
                    chkDoCall.setEnabled(false);
                }
            }
        });
        chkSwipeDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkSwipeUp.setChecked(false);
                    chkSwipeLeft.setChecked(false);
                    chkSwipeRight.setChecked(false);

                    chkDoApp.setEnabled(true);
                    chkDoMessage.setEnabled(true);
                    chkDoWork.setEnabled(true);
                    chkDoCall.setEnabled(true);
                } else {
                    chkDoApp.setEnabled(false);
                    chkDoMessage.setEnabled(false);
                    chkDoWork.setEnabled(false);
                    chkDoCall.setEnabled(false);
                }
            }
        });
        chkSwipeLeft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkSwipeDown.setChecked(false);
                    chkSwipeUp.setChecked(false);
                    chkSwipeRight.setChecked(false);

                    chkDoApp.setEnabled(true);
                    chkDoMessage.setEnabled(true);
                    chkDoWork.setEnabled(true);
                    chkDoCall.setEnabled(true);
                } else {
                    chkDoApp.setEnabled(false);
                    chkDoMessage.setEnabled(false);
                    chkDoWork.setEnabled(false);
                    chkDoCall.setEnabled(false);
                }
            }
        });
        chkSwipeRight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkSwipeDown.setChecked(false);
                    chkSwipeLeft.setChecked(false);
                    chkSwipeUp.setChecked(false);

                    chkDoApp.setEnabled(true);
                    chkDoMessage.setEnabled(true);
                    chkDoWork.setEnabled(true);
                    chkDoCall.setEnabled(true);
                } else {
                    chkDoApp.setEnabled(false);
                    chkDoMessage.setEnabled(false);
                    chkDoWork.setEnabled(false);
                    chkDoCall.setEnabled(false);
                }
            }
        });

        chkDoApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkDoMessage.setChecked(false);
                    chkDoCall.setChecked(false);
                    chkDoWork.setChecked(false);
                    createAppRec();
                } else {
                    actions.clear();
                    adapterRecShowActions.sendActions(actions);
                    edtSearch.setVisibility(View.GONE);
                }
            }
        });
        chkDoMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkDoApp.setChecked(false);
                    chkDoCall.setChecked(false);
                    chkDoWork.setChecked(false);
                    if (checkPermission(false)) {
                        createMessage();
                    }
                } else {
                    actions.clear();
                    adapterRecShowActions.sendActions(actions);
                    edtSearch.setVisibility(View.GONE);
                }
            }
        });
        chkDoWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkDoMessage.setChecked(false);
                    chkDoCall.setChecked(false);
                    chkDoApp.setChecked(false);
                    createWork();
                }else {
                    actions.clear();
                    adapterRecShowActions.sendActions(actions);
                }
            }
        });
        chkDoCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkDoMessage.setChecked(false);
                    chkDoApp.setChecked(false);
                    chkDoWork.setChecked(false);
                    if (checkPermission(true)) {
                        createCall();
                    }
                } else {
                    actions.clear();
                    adapterRecShowActions.sendActions(actions);
                    edtSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    private void createAppRec() {
        edtSearch.setVisibility(View.VISIBLE);
        edtSearch.setText("");
        actions.clear();
        whatAction = getString(R.string.doApp);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo pkgInfo : packages) {
            if ((pkgInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                InformationActions informationActions = new InformationActions();
                informationActions.setName(pm.getApplicationLabel(pkgInfo).toString());
                informationActions.setIcon(pkgInfo.loadIcon(getPackageManager()));
                informationActions.setActionValueView(2);
                actions.add(informationActions);
            } else if ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                InformationActions informationActions = new InformationActions();
                informationActions.setName(pm.getApplicationLabel(pkgInfo).toString());
                informationActions.setIcon(pkgInfo.loadIcon(getPackageManager()));
                informationActions.setActionValueView(2);
                actions.add(informationActions);
            }
        }

        adapterRecShowActions.sendActions(actions);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                listSearch.clear();
                if (s.toString().equals("")) {
                    adapterRecShowActions.sendActions(actions);
                } else {
                    for (InformationActions result : actions) {
                        if (result.getName().toLowerCase().contains(s)) {
                            InformationActions informationActions = new InformationActions();
                            informationActions.setName(result.getName());
                            informationActions.setIcon(result.getIcon());
                            informationActions.setActionValueView(2);
                            listSearch.add(informationActions);
                        }
                    }
                    adapterRecShowActions.sendActions(listSearch);
                }
            }
        });
    }

    private void createCall() {
        edtSearch.setVisibility(View.VISIBLE);
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

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                listSearch.clear();
                if (s.toString().equals("")) {
                    adapterRecShowActions.sendActions(actions);
                } else {
                    for (InformationActions result : actions) {
                        if (result.getName().toLowerCase().contains(s)) {
                            InformationActions informationActions = new InformationActions();
                            informationActions.setName(result.getName());
                            informationActions.setPhoneNumber(result.getPhoneNumber());
                            informationActions.setIcon(result.getIcon());
                            informationActions.setActionValueView(4);
                            listSearch.add(informationActions);
                        }
                    }
                    adapterRecShowActions.sendActions(listSearch);
                }

            }
        });
    }

    private void createMessage() {
        actions.clear();
        edtSearch.setVisibility(View.VISIBLE);
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
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listSearch.clear();
                if (s.toString().equals("")) {
                    adapterRecShowActions.sendActions(actions);
                } else {
                    for (InformationActions result : actions) {
                        if (result.getName().toLowerCase().contains(s)) {
                            InformationActions informationActions = new InformationActions();
                            informationActions.setName(result.getName());
                            informationActions.setPhoneNumber(result.getPhoneNumber());
                            informationActions.setIcon(result.getIcon());
                            informationActions.setActionValueView(4);
                            listSearch.add(informationActions);
                        }
                    }
                    adapterRecShowActions.sendActions(listSearch);
                }
            }
        });
    }

    private void createWork(){
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
                        //codeDoMessage();
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
                                            T.dialogQuestionShow(ActivityActionSwipe.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
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
                        createCall();
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
                                            T.dialogQuestionShow(ActivityActionSwipe.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
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
                        //adapterRecFPAction.buildRecMessage();
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
                                            T.dialogQuestionShow(ActivityActionSwipe.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
                                                    getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    toastManager.lT(getString(R.string.permission_toast_getpermission), getDrawable(R.drawable.ic_block));
                                                                    //adapterRecFPAction.unCheckDoCallOrMessage();
                                                                    dialogCustom.dismass();
                                                                    break;
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    // adapterRecFPAction.checkPermission(false);
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
                            //  adapterRecFPAction.unCheckDoCallOrMessage();
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
                        //adapterRecFPAction.buildRecCall();
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
                                            T.dialogQuestionShow(ActivityActionSwipe.this, getResources().getString(R.string.permissionQuestion), getString(R.string.dialog_button_positive_yes),
                                                    getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    toastManager.lT(getString(R.string.permission_toast_getpermission), getDrawable(R.drawable.ic_block));
                                                                    //    adapterRecFPAction.unCheckDoCallOrMessage();
                                                                    dialogCustom.dismass();
                                                                    break;
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    //  adapterRecFPAction.checkPermission(true);
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
                            // adapterRecFPAction.unCheckDoCallOrMessage();
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
