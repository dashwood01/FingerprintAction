package com.app.dashwood.fingerprintsensor.log;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dashwood.fingerprintsensor.R;

public class T {
    private Toast toast;
    private TextView txtToast;
    private ImageView imgToast;
    private Context context;
    public T(Context context){
        this.context = context;
        LinearLayout viewToast = (LinearLayout) View.inflate(context, R.layout.custom_toast, null);
        toast = new Toast(context);
        txtToast = (TextView) viewToast.findViewById(R.id.txtToast);
        imgToast = (ImageView) viewToast.findViewById(R.id.imgToast);
        toast.setView(viewToast);
    }
    public static void log(String tag,String msg){
        Log.i(tag,msg);
    }
    public void lT(String msg, @Nullable Drawable img){
        if (toast.getView().getWindowVisibility() == View.VISIBLE){
            toast.cancel();
        }
        if (img != null){
            imgToast.setImageDrawable(img);
        }
        txtToast.setText(msg);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
    public void sT(String msg,@Nullable Drawable img){
        if (img != null){
            imgToast.setImageDrawable(img);
        }else {
            imgToast.setImageDrawable(context.getDrawable(R.drawable.ic_finger));
        }
        txtToast.setText(msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    public static void dialogShow(Context context, String title, String message,Drawable drawable,boolean cancable,DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("باشه", listener)
                .setIcon(drawable)
                .setCancelable(cancable)
                .create()
                .show();
    }
    public static void dialogQuestionShow(Context context,String message,String positiveButton,String negativeButton,boolean cancable, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton(negativeButton, listener)
                .setCancelable(cancable)
                .create()
                .show();
    }

}
