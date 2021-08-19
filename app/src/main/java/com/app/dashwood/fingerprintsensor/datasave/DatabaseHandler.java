package com.app.dashwood.fingerprintsensor.datasave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DB_NAME= "FPA_DB";
    private static final int DB_VERSION = 1;
    static final String TABLE_ACTION = "TB_Action";
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_DESCRIPTION = "description";
    static final String COLUMN_COUNT = "count";
    static final String COLUMN_PACKAGENAME = "packagename";
    static final String COLUMN_WHATFINGER = "fingerchecker";
    static final String COLUMN_WHATACTION = "actionchecker";
    static final String COLUMN_DISPLAYNAME = "displayname";
    private static final String CREATE_TABLE_ACTION = "CREATE TABLE " + TABLE_ACTION + " ("+
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
            COLUMN_NAME + " TEXT,"+
            COLUMN_DESCRIPTION + " TEXT,"+
            COLUMN_COUNT + " INTEGER,"+
            COLUMN_PACKAGENAME + " TEXT," +
            COLUMN_WHATFINGER + " TEXT," +
            COLUMN_WHATACTION + " TEXT," +
            COLUMN_DISPLAYNAME + " TEXT " +
            ");";


    DatabaseHandler(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_TABLE_ACTION);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(" DROP TABLE " + TABLE_ACTION + "IF EXISTS;");
            onCreate(sqLiteDatabase);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
}
