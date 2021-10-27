package com.app.dashwood.fingerprintsensor.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.dashwood.fingerprintsensor.ActivityActions;
import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.dataset.InformationActions;
import com.app.dashwood.fingerprintsensor.dataset.InformationFPAction;
import com.app.dashwood.fingerprintsensor.dataset.SynchronizedDatabase;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.listener.OnClickDialogListener;
import com.app.dashwood.fingerprintsensor.log.DialogCustom;
import com.app.dashwood.fingerprintsensor.log.T;

import java.util.ArrayList;
import java.util.List;


public class AdapterRecFPAction extends RecyclerView.Adapter<AdapterRecFPAction.ViewHolder> {

    private ArrayList<InformationFPAction> actions = new ArrayList<>();
    private Activity context;
    private SynchronizedDatabase synchronizedDatabase;

    //alert
    private AlertDialog alertAction;
    private EditText edtNameAction, edtDesAction;
    private RadioButton rdCountOne, rdCountTwo, rdCountThree, rdCountFore, rdCountFive, rdCountSix, rdCountSeven, rdCountEight, rdCountNine;
    private RadioGroup rdGroup;
    private CheckBox chkDoApp, chkDoWork, chkDoCall, chkDoMessage, chkFailFinger, chkSuccefulFinger, chkFastFinger;
    private AdapterRecShowActions adapterRecShowActions;
    private RecyclerView recShowActions;
    private String whatFinger = "";
    private String whatAction = "";
    private boolean isEnableFastfinger = true, isEnableFailfinger = true, isEnablesuccedfinger = true;
    private ArrayList<InformationActions> listActions = new ArrayList<>();
    private int countCheckerFast;
    private int count;
    private T toastManager;
    private DialogCustom dialogCustom;

    public AdapterRecFPAction(Activity context) {
        this.context = context;
        synchronizedDatabase = new SynchronizedDatabase(context);
        toastManager = new T(context);
        dialogCustom = new DialogCustom(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_rec_fp_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final InformationFPAction informationFPAction = actions.get(holder.getAdapterPosition());
        holder.txtNameAction.setText(informationFPAction.getName());
        int[] imgFinger = {R.drawable.ic_num1, R.drawable.ic_num2, R.drawable.ic_num3, R.drawable.ic_num4,
                R.drawable.ic_num5, R.drawable.ic_num6, R.drawable.ic_num7, R.drawable.ic_num8, R.drawable.ic_num9,
                R.drawable.ic_finger, R.drawable.ic_finger_nocheck};
        switch (informationFPAction.getCount()) {
            case 1:
                holder.imgAction.setImageResource(imgFinger[0]);
                break;
            case 2:
                holder.imgAction.setImageResource(imgFinger[1]);
                break;
            case 3:
                holder.imgAction.setImageResource(imgFinger[2]);
                break;
            case 4:
                holder.imgAction.setImageResource(imgFinger[3]);
                break;
            case 5:
                holder.imgAction.setImageResource(imgFinger[4]);
                break;
            case 6:
                holder.imgAction.setImageResource(imgFinger[5]);
                break;
            case 7:
                holder.imgAction.setImageResource(imgFinger[6]);
                break;
            case 8:
                holder.imgAction.setImageResource(imgFinger[7]);
                break;
            case 9:
                holder.imgAction.setImageResource(imgFinger[8]);
                break;
            default:
                holder.imgAction.setImageResource(imgFinger[9]);
        }
        if (informationFPAction.getWhatFinger().equals("fail")) {
            holder.imgAction.setImageResource(imgFinger[10]);
        }
        holder.layoutRootFpAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, context.getString(R.string.snackbar_message_edet_action) + informationFPAction.getName(), Snackbar.LENGTH_LONG)
                        .setAction(context.getString(R.string.snackbar_text_btn_edit_action), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               // actionEditAlert(holder.getAdapterPosition());
                                Intent intent = new Intent();
                                intent.putExtra(context.getString(R.string.intent_putextra_edit),holder.getAdapterPosition());
                                intent.setClass(context,ActivityActions.class);
                                context.startActivity(intent);
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    private void actionEditAlert(int position) {
        T.log("LOg","FAST " + isEnableFastfinger + " Fail " + isEnableFailfinger + " succeed " + isEnablesuccedfinger);
        final InformationFPAction informationFPAction = actions.get(position);
        checkEnabler();
        AlertDialog.Builder actionEditBuilder = new AlertDialog.Builder(context);
        LinearLayout viewActionEdit = (LinearLayout) View.inflate(context, R.layout.custom_alert_add_action, null);
        actionEditBuilder.setView(viewActionEdit);
        alertAction = actionEditBuilder.create();
        recShowActions = (RecyclerView) viewActionEdit.findViewById(R.id.recShowActions);
        final Toolbar toolbarAction = (Toolbar) viewActionEdit.findViewById(R.id.toolbarAction);
        toolbarAction.inflateMenu(R.menu.menu_action);
        Menu menu = toolbarAction.getMenu();
        MenuItem menuDelete = menu.findItem(R.id.btnAction_delete);
        menuDelete.setVisible(true);
        edtNameAction = (EditText) viewActionEdit.findViewById(R.id.edtNameAction);
        edtDesAction = (EditText) viewActionEdit.findViewById(R.id.edtDesAction);
        rdCountOne = (RadioButton) viewActionEdit.findViewById(R.id.rdCountOne);
        rdCountTwo = (RadioButton) viewActionEdit.findViewById(R.id.rdCountTwo);
        rdCountThree = (RadioButton) viewActionEdit.findViewById(R.id.rdCountThree);
        rdCountFore = (RadioButton) viewActionEdit.findViewById(R.id.rdCountFore);
        rdCountFive = (RadioButton) viewActionEdit.findViewById(R.id.rdCountFive);
        rdCountSix = (RadioButton) viewActionEdit.findViewById(R.id.rdCountSix);
        rdCountSeven = (RadioButton) viewActionEdit.findViewById(R.id.rdCountSeven);
        rdCountEight = (RadioButton) viewActionEdit.findViewById(R.id.rdCountEight);
        rdCountNine = (RadioButton) viewActionEdit.findViewById(R.id.rdCountNine);
        rdGroup = (RadioGroup) viewActionEdit.findViewById(R.id.rdGroup);
        chkDoApp = (CheckBox) viewActionEdit.findViewById(R.id.chkDoApp);
        chkDoWork = (CheckBox) viewActionEdit.findViewById(R.id.chkDoWork);
        chkDoCall = (CheckBox) viewActionEdit.findViewById(R.id.chkDoCall);
        chkDoMessage = (CheckBox) viewActionEdit.findViewById(R.id.chkDoMessage);
        chkFailFinger = (CheckBox) viewActionEdit.findViewById(R.id.chkFailFinger);
        chkSuccefulFinger = (CheckBox) viewActionEdit.findViewById(R.id.chkSuccefulFinger);
        chkFastFinger = (CheckBox) viewActionEdit.findViewById(R.id.chkFastFinger);
        TextView txtShowAction = (TextView) viewActionEdit.findViewById(R.id.txtViewAction);
        recShowActions.setLayoutManager(new LinearLayoutManager(context));
        adapterRecShowActions = new AdapterRecShowActions(context);
        recShowActions.setAdapter(adapterRecShowActions);
        edtNameAction.setText(informationFPAction.getName());
        edtDesAction.setText(informationFPAction.getDescription());
        count = informationFPAction.getCount();
        whatAction = informationFPAction.getWhatAction();
        whatFinger = informationFPAction.getWhatFinger();
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
        toolbarAction.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btnAction_accept:
                        if (whatFinger.equals("fast")) {
                            if (updateChecker(informationFPAction.getId(), true)) {
                                T.log("LOG", "HERE RUN IF IN UPDATE FAST");
                                toastManager.sT(context.getString(R.string.toast_update_accept),context.getDrawable(R.drawable.ic_accept_toast));
                                getAllAction();
                                alertAction.dismiss();
                            }
                        } else {
                            if (updateChecker(informationFPAction.getId(), false)) {
                                T.log("lOG", "HERE RUN IF UPDATE OTHER FINGER");
                                toastManager.sT(context.getString(R.string.toast_update_accept),context.getDrawable(R.drawable.ic_accept_toast));
                                getAllAction();
                                alertAction.dismiss();
                            }
                            count = 0;
                        }

                        break;
                    case R.id.btnAction_close:
                        alertAction.dismiss();
                        break;
                    case R.id.btnAction_delete:
                        T.dialogQuestionShow(context, context.getString(R.string.dialog_warning_delete),
                                context.getString(R.string.dialog_button_positive_yes), context.getString(R.string.dialog_button_negative_no)
                                , false, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i) {
                                            case Dialog.BUTTON_POSITIVE:
                                                if (synchronizedDatabase.getWritableDatabase().deleteAction(informationFPAction.getId())) {
                                                    getAllAction();
                                                    alertAction.dismiss();

                                                }
                                                break;
                                        }
                                    }
                                });
                        break;
                }
                return true;
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
        switch (informationFPAction.getWhatFinger()) {
            case "fast":
                chkFastFinger.setChecked(true);
                chkFailFinger.setEnabled(false);
                chkSuccefulFinger.setEnabled(false);
                codeFastfinger(informationFPAction, true);
                break;
            case "succeful":
                chkFastFinger.setEnabled(false);
                chkFailFinger.setEnabled(false);
                chkSuccefulFinger.setChecked(true);
                codeSucceedfinger(informationFPAction);
                break;
            case "fail":
                chkFastFinger.setEnabled(false);
                chkFailFinger.setChecked(true);
                chkSuccefulFinger.setEnabled(false);
                codeFailfinger(informationFPAction);
                break;
        }

        chkSuccefulFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkSuccefulFinger.isChecked()) {
                    whatFinger = context.getString(R.string.fingerSucceful);
                    chkFastFinger.setEnabled(false);
                    chkFailFinger.setEnabled(false);
                } else {
                    if (isEnableFastfinger) {
                        chkFastFinger.setEnabled(true);
                    }
                    if (isEnableFailfinger) {
                        chkFailFinger.setEnabled(true);
                    }
                }
            }
        });
        chkFastFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkFastFinger.isChecked()) {
                    whatFinger = context.getString(R.string.fingerFast);
                    chkSuccefulFinger.setEnabled(false);
                    chkFailFinger.setEnabled(false);
                    codeFastfinger(informationFPAction, false);
                } else {
                    for (int i = 0; i < rdGroup.getChildCount(); i++) {
                        rdGroup.getChildAt(i).setEnabled(false);
                    }
                    if (isEnablesuccedfinger) {
                        chkSuccefulFinger.setEnabled(true);
                    }
                    if (isEnableFailfinger) {
                        chkFailFinger.setEnabled(true);
                    }
                }
            }
        });
        chkFailFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkFailFinger.isChecked()) {
                    whatFinger = context.getString(R.string.fingerFail);
                    chkFastFinger.setEnabled(false);
                    chkSuccefulFinger.setEnabled(false);
                } else {
                    if (isEnableFastfinger) {
                        chkFastFinger.setEnabled(true);
                    }
                    if (isEnablesuccedfinger) {
                        chkSuccefulFinger.setEnabled(true);
                    }
                }
            }
        });
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
        alertAction.show();
    }

    private void codeDoCall() {
        if (chkDoCall.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_CALL),
                    true, context.getString(R.string.KEY_CHECKER_DO_CALL));
            if (checkHelp) {
                dialogCustom.setTitle(context.getString(R.string.dialog_example_title))
                        .setMessage(context.getString(R.string.dialog_example_message_docall))
                        .setIcon(context.getDrawable(R.drawable.ic_help_alert))
                        .setButtonPositive(context.getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(context.getString(R.string.dialog_button_negative_text_check))
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
                                Data.saveToPrefermenceBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_CALL),
                                        false, context.getString(R.string.KEY_CHECKER_DO_CALL));
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
            listActions.clear();
            adapterRecShowActions.sendActions(listActions);

        }
    }

    private void codeDoMessage() {
        if (chkDoMessage.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_MESSAGE),
                    true, context.getString(R.string.KEY_CHECKER_DO_MESSAGE));
            if (checkHelp) {
                dialogCustom.setTitle(context.getString(R.string.dialog_example_title))
                        .setMessage(context.getString(R.string.dialog_example_message_domessage))
                        .setIcon(context.getDrawable(R.drawable.ic_help_alert))
                        .setButtonPositive(context.getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(context.getString(R.string.dialog_button_negative_text_check))
                        .setCancable(false)
                        .setCancelButtonVisible(true)
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                Data.saveToPrefermenceBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_MESSAGE),
                                        false, context.getString(R.string.KEY_CHECKER_DO_MESSAGE));
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
            listActions.clear();
            adapterRecShowActions.sendActions(listActions);
        }
    }

    private void codeDoApp() {
        if (chkDoApp.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_APP),
                    true, context.getString(R.string.KEY_CHECKER_DO_APP));
            if (checkHelp) {
                dialogCustom.setTitle(context.getString(R.string.dialog_example_title))
                        .setMessage(context.getString(R.string.dialog_example_message_doapp))
                        .setIcon(context.getDrawable(R.drawable.ic_help_alert))
                        .setCancelButtonVisible(true)
                        .setCancable(false)
                        .setButtonPositive(context.getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(context.getString(R.string.dialog_button_negative_text_check))
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
                                Data.saveToPrefermenceBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_APP),
                                        false, context.getString(R.string.KEY_CHECKER_DO_APP));
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
            listActions.clear();
            adapterRecShowActions.sendActions(listActions);

        }
    }

    private void codeDoWork() {
        if (chkDoWork.isChecked()) {
            boolean checkHelp = Data.readPreferecesBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_WORK),
                    true, context.getString(R.string.KEY_CHECKER_DO_WORK));
            if (checkHelp) {
                dialogCustom.setTitle(context.getString(R.string.dialog_example_title))
                        .setMessage(context.getString(R.string.dialog_example_message_dowork))
                        .setIcon(context.getDrawable(R.drawable.ic_help_alert))
                        .setCancelButtonVisible(true)
                        .setCancable(false)
                        .setButtonPositive(context.getString(R.string.dialog_button_positive_text_check))
                        .setButtonNegitive(context.getString(R.string.dialog_button_negative_text_check))
                        .setListenerCancelButton(new OnClickDialogListener() {
                            @Override
                            public void onClick(View v) {
                                Data.saveToPrefermenceBoolean(context, context.getString(R.string.PREF_HOME_CHECKER_DO_WORK),
                                        false, context.getString(R.string.KEY_CHECKER_DO_WORK));
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
            listActions.clear();
            adapterRecShowActions.sendActions(listActions);
        }
    }

    private void codeFastfinger(InformationFPAction informationFPAction, boolean firstTime) {
        for (int i = 0; i < rdGroup.getChildCount(); i++) {
            rdGroup.getChildAt(i).setEnabled(true);
        }
        for (InformationFPAction fp : actions) {
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

    private boolean updateChecker(int id, boolean checkCount) {
        T.log("LOg", "UPDATE RUN");
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
                            toastManager.lT(context.getString(R.string.error_toast_DoAction),null);
                        }
                    } else {
                        toastManager.lT(context.getString(R.string.error_toast_CountAction),null);
                    }

                } else {
                    if (!actionValue.equals("")) {
                        T.log("LOG", "COUNT DISABLE");
                        return updateAction(id, edtNameAction.getText().toString(), edtDesAction.getText().toString(),
                                0, actionValue, whatFinger, whatAction, displayName);
                    } else {
                        toastManager.lT(context.getString(R.string.error_toast_DoAction),null);
                    }
                }
            } else {
                toastManager.lT(context.getString(R.string.error_toast_fingerprint),null);
            }

        } else {
            toastManager.lT(context.getString(R.string.error_toast_NameAction),null);
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
        chkSuccefulFinger.setChecked(true);
        checkWhatAction(informationFPAction);
    }

    private void codeFailfinger(InformationFPAction informationFPAction) {
        chkSuccefulFinger.setEnabled(false);
        chkFastFinger.setEnabled(false);
        chkFailFinger.setChecked(true);
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

    public void buildRecCall() {
        listActions.clear();
        whatAction = context.getString(R.string.doCall);
        Drawable ic_contact = context.getDrawable(R.drawable.ic_contacts);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Cursor phones = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                "display_name ASC");
        if (phones != null) {
            while (phones.moveToNext()) {
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (phones.getInt(phones.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = context.getContentResolver().query(
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
                            listActions.add(informationActions);
                        }
                    }
                    if (pCur != null) {
                        pCur.close();
                    }
                }
            }
            phones.close();
        }
        adapterRecShowActions.sendActions(listActions);
        chkDoWork.setEnabled(false);
        chkDoApp.setEnabled(false);
        chkDoMessage.setEnabled(false);

    }

    public void buildRecMessage() {
        listActions.clear();
        whatAction = context.getString(R.string.doMessage);
        Drawable ic_contact = context.getDrawable(R.drawable.ic_message);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Cursor phones = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                "display_name ASC");
        if (phones != null) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                if (phones.getInt(phones.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = context.getContentResolver().query(
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
                            listActions.add(informationActions);
                        }
                    }
                    if (pCur != null) {
                        pCur.close();
                    }
                }
            }
            phones.close();
        }
        adapterRecShowActions.sendActions(listActions);
        chkDoWork.setEnabled(false);
        chkDoApp.setEnabled(false);
        chkDoCall.setEnabled(false);
    }

    private void buildRecApp() {
        listActions.clear();
        whatAction = context.getString(R.string.doApp);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo pkgInfo : packages) {
            if ((pkgInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                InformationActions informationActions = new InformationActions();
                informationActions.setName(pkgInfo.packageName);
                informationActions.setIcon(pkgInfo.loadIcon(context.getPackageManager()));
                informationActions.setActionValueView(2);
                listActions.add(informationActions);
            } else if ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                InformationActions informationActions = new InformationActions();
                informationActions.setName(pkgInfo.packageName);
                informationActions.setIcon(pkgInfo.loadIcon(context.getPackageManager()));
                informationActions.setActionValueView(2);
                listActions.add(informationActions);
            }
        }
        adapterRecShowActions.sendActions(listActions);
        chkDoWork.setEnabled(false);
        chkDoCall.setEnabled(false);
        chkDoMessage.setEnabled(false);
    }

    private void buildRecWork() {
        listActions.clear();
        whatAction = context.getString(R.string.doWork);
        recShowActions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        String[] nameList = context.getResources().getStringArray(R.array.actions_work);
        int[] iconList = {R.drawable.ic_wifi, R.drawable.ic_bluetooth, R.drawable.ic_flashlight, R.drawable.ic_home,
                R.drawable.ic_lock, R.drawable.ic_music, R.drawable.ic_vibrate, R.drawable.ic_volume};

        for (int i = 0; i < nameList.length; i++) {
            InformationActions informationActions = new InformationActions();
            informationActions.setName(nameList[i]);
            informationActions.setIcon(context.getDrawable(iconList[i]));
            informationActions.setPhoneNumber("" + i);
            informationActions.setActionValueView(1);
            informationActions.setChild((byte) i);
            listActions.add(informationActions);
        }
        adapterRecShowActions.sendActions(listActions);
        chkDoMessage.setEnabled(false);
        chkDoApp.setEnabled(false);
        chkDoCall.setEnabled(false);
    }

    private void getAllAction() {
        A.setFingerActions(synchronizedDatabase.getWritableDatabase().getAllAction());
        sendAction(A.getFingerActions());
    }

    public void sendAction(ArrayList<InformationFPAction> list) {
        actions = list;
        notifyDataSetChanged();
    }

    public boolean checkPermission(boolean checkCall) {
        if (checkCall) {
            if (context.checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    || context.checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS};
                context.requestPermissions(permissions, context.getResources().getInteger(R.integer.REQUEST_CODE_ASK_PERMISSIONS_CALL_EDIT));
                T.log("LOG", "HERE RUN");
                return false;
            }
        } else {
            if (context.checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                String[] permission = {Manifest.permission.READ_CONTACTS};
                context.requestPermissions(permission, context.getResources().getInteger(R.integer.REQUEST_CODE_ASK_PERMISSIONS_EDIT));
                return false;
            }
        }
        return true;
    }

    public void unCheckDoCallOrMessage() {
        if (chkDoCall != null && chkDoMessage != null) {
            chkDoCall.setChecked(false);
            chkDoMessage.setChecked(false);
        }

    }
    private void checkEnabler(){
        isEnableFastfinger = true;
        isEnableFailfinger = true;
        isEnablesuccedfinger = true;
        for (InformationFPAction informationFPAction: A.getFingerActions()){
            switch (informationFPAction.getWhatFinger()) {
                case "fast":
                    countCheckerFast++;
                    break;
                case "succeful":
                    isEnablesuccedfinger = false;
                    break;
                case "fail":
                    isEnableFailfinger = false;
                    break;
            }
            if (countCheckerFast == 9) {
                isEnableFastfinger = false;
                countCheckerFast = 0;
            }
        }

    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAction;
        private TextView txtNameAction;
        private ViewGroup layoutRootFpAction;

        ViewHolder(View itemView) {
            super(itemView);
            imgAction = (ImageView) itemView.findViewById(R.id.imgAction);
            txtNameAction = (TextView) itemView.findViewById(R.id.txtNameAction);
            layoutRootFpAction = (ViewGroup) itemView.findViewById(R.id.layoutRootFpAction);
        }
    }


}
