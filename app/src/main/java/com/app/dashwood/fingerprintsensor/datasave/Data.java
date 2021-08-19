package com.app.dashwood.fingerprintsensor.datasave;

import android.content.Context;
import android.content.SharedPreferences;


public class Data {
    public static void saveToPrefermenceBoolean(Context context, String preferenceHome, boolean preferenceValuee, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceHome, preferenceValuee);
        editor.apply();
    }

    public static boolean readPreferecesBoolean(Context context, String preferenceHome, boolean defultValue, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(preferenceHome, defultValue);

    }

    public static void saveToPrefermenceInt(Context context,String preferenceHome,int preferenceValue,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceHome,preferenceValue);
        editor.apply();
    }
    public static int readToPrefermenceInt(Context context,String preferenceHome,int defaultValue,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(preferenceHome,defaultValue);
    }

}
