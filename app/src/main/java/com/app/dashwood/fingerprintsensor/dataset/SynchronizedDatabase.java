package com.app.dashwood.fingerprintsensor.dataset;

import android.content.Context;

import com.app.dashwood.fingerprintsensor.datasave.Database;

public class SynchronizedDatabase {
    private Context context;
    private Database actionDatabase;
    public SynchronizedDatabase(Context context){
        this.context = context;
    }
    public synchronized Database getWritableDatabase() {
        if (actionDatabase == null) {
            actionDatabase = new Database(context);
        }
        return actionDatabase;
    }
}
