package com.app.dashwood.fingerprintsensor.dataset;


public class InformationFPAction {
    private int id;
    private String name, description, whatFinger, packageName, whatAction, displayName;
    private int count;

    public void setId(int value) {
        id = value;
    }

    public void setName(String value) {
        name = value;
    }

    public void setDescription(String value) {
        description = value;
    }

    public void setCount(int value) {
        count = value;
    }

    public void setWhatFinger(String value) {
        whatFinger = value;
    }

    public void setPackageName(String value) {
        packageName = value;
    }

    public void setWhatAction(String value) {
        whatAction = value;
    }

    public void setDisplayName(String value) {
        displayName = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public String getWhatFinger() {
        return whatFinger;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getWhatAction() {
        return whatAction;
    }

    public String getDisplayName() {
        return displayName;
    }
}
