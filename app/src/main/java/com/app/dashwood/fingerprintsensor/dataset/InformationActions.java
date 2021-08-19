package com.app.dashwood.fingerprintsensor.dataset;

import android.graphics.drawable.Drawable;

public class InformationActions {
    private String name;
    private Drawable icon;
    private String phoneNumber;
    private int actionValueView;
    private byte child;

    public void setName(String value) {
        name = value;
    }

    public void setIcon(Drawable value) {
        icon = value;
    }

    public void setPhoneNumber(String value) {
        phoneNumber = value;
    }

    public void setActionValueView(int value) {
        actionValueView = value;
    }

    public void setChild(byte value) {
        child = value;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getActionValueView() {
        return actionValueView;
    }

    public byte getChild() {
        return child;
    }


}
