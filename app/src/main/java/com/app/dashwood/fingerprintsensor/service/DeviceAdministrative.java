package com.app.dashwood.fingerprintsensor.service;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;


public class DeviceAdministrative extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }
}
