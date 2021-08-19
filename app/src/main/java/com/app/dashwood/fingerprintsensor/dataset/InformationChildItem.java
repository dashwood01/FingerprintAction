package com.app.dashwood.fingerprintsensor.dataset;

import android.graphics.drawable.Drawable;


public class InformationChildItem {
    private String name;
    private Drawable img;
    private int prent;


    public void setName(String value){
        name = value;
    }
    public void setImg(Drawable value){
        img = value;
    }
    public void setPrent(int value){
        prent = value;
    }
    public String getName(){
        return name;
    }
    public Drawable getIcon(){
        return img;
    }
    public int getPrent(){
        return prent;
    }

}
