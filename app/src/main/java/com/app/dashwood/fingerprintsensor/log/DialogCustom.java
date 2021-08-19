package com.app.dashwood.fingerprintsensor.log;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.datasave.Data;
import com.app.dashwood.fingerprintsensor.listener.OnClickDialogListener;

import java.util.Locale;

public class DialogCustom {
    private AlertDialog alertDialog;
    private Button btnAccept, btnCancel;
    private TextView txtTitle, txtMessage;
    private ImageView icon;

    public DialogCustom(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LinearLayout viewAlert = (LinearLayout) View.inflate(context, R.layout.custom_alert_for_all_reason, null);
        btnAccept = (Button) viewAlert.findViewById(R.id.btnAccept);
        btnCancel = (Button) viewAlert.findViewById(R.id.btnCancel);
        txtTitle = (TextView) viewAlert.findViewById(R.id.txtTitle);
        txtMessage = (TextView) viewAlert.findViewById(R.id.txtMessage);
        icon = (ImageView) viewAlert.findViewById(R.id.icon);
        ViewGroup layoutTitle = (ViewGroup) viewAlert.findViewById(R.id.layoutTitle);
        if (Data.readToPrefermenceInt(context,context.getString(R.string.PREF_HOME_CHANGE_LANGUAGE),3,
                context.getString(R.string.KEY_CHANGE_LANGUAGE))==0){
            layoutTitle.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            txtTitle.setTextDirection(View.TEXT_DIRECTION_RTL);
        }else {
            layoutTitle.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            txtTitle.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        alertDialogBuilder.setView(viewAlert);
        alertDialog = alertDialogBuilder.create();
    }

    public DialogCustom setTitle(String value) {
        txtTitle.setText(value);
        return this;
    }

    public DialogCustom setMessage(String value) {
        txtMessage.setText(value);
        return this;
    }

    public DialogCustom setButtonNegitive(String value) {
        btnCancel.setText(value);
        return this;
    }

    public DialogCustom setButtonPositive(String value) {
        btnAccept.setText(value);
        return this;
    }

    public DialogCustom setIcon(Drawable value) {
        icon.setImageDrawable(value);
        return this;
    }

    public DialogCustom setListenerAcceptButton(OnClickDialogListener listener) {
        btnAccept.setOnClickListener(listener);
        return this;
    }

    public DialogCustom setListenerCancelButton(OnClickDialogListener listener) {
        btnCancel.setOnClickListener(listener);
        return this;
    }

    public DialogCustom setCancelButtonVisible(boolean check) {
        if (check) {
            btnCancel.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public DialogCustom setCancable(boolean cancable) {
        alertDialog.setCancelable(cancable);
        return this;
    }

    public DialogCustom show() {
        alertDialog.show();
        return this;
    }

    public DialogCustom dismass() {
        alertDialog.dismiss();
        return this;
    }

}
