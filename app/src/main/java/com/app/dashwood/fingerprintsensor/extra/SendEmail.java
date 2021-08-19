package com.app.dashwood.fingerprintsensor.extra;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.log.T;


public class SendEmail {
    private Context context;
    public SendEmail(Context context){
        this.context = context;
    }
    public void sendBug(final String message) {
        T.dialogQuestionShow(context, context.getString(R.string.dialog_message_bug_send), context.getString(R.string.dialog_button_positive_yes),
                context.getString(R.string.dialog_button_negative_no), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case Dialog.BUTTON_POSITIVE:
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                email.setType("text/plain");
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"iiviiohammadnzm@gmail.com"});
                                email.putExtra(Intent.EXTRA_SUBJECT, "BUG");
                                email.putExtra(Intent.EXTRA_TEXT, message);
                                try {
                                    context.startActivity(Intent.createChooser(email, context.getString(R.string.app_send_email)));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(context,ex.toString(),Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                });
    }
}
