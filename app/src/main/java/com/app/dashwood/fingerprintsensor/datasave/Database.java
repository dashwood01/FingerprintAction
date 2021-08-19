package com.app.dashwood.fingerprintsensor.datasave;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteStatement;
import com.app.dashwood.fingerprintsensor.dataset.InformationFPAction;
import com.app.dashwood.fingerprintsensor.extra.SendEmail;
import java.util.ArrayList;


public class Database {
    private SQLiteDatabase database;
    private Context context;

    public Database(Context context) {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        database = databaseHandler.getWritableDatabase();
        this.context = context;
    }

    public String insertAction(String nameAction, String description, int count,
                               String packageName, String checkFinger, String whatAction,String displayName) {

        try {
            String sql = "INSERT INTO " + DatabaseHandler.TABLE_ACTION + " VALUES(?,?,?,?,?,?,?,?);";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            statement.clearBindings();
            statement.bindString(2, nameAction);
            statement.bindString(3, description);
            statement.bindLong(4, count);
            statement.bindString(5, packageName);
            statement.bindString(6, checkFinger);
            statement.bindString(7, whatAction);
            statement.bindString(8, displayName);
            statement.execute();
            database.setTransactionSuccessful();
            database.endTransaction();
            return "done";
        } catch (SQLiteFullException e) {
            e.printStackTrace();
            new SendEmail(context).sendBug(e.toString());
            return e.toString();
        }

    }


    public boolean updateAction(int id, String nameAction, String description, int count,
                                String packageName, String whatFinger, String whatAction,String displayName) {
        ContentValues con = new ContentValues();
        con.put(DatabaseHandler.COLUMN_NAME, nameAction);
        con.put(DatabaseHandler.COLUMN_DESCRIPTION, description);
        con.put(DatabaseHandler.COLUMN_COUNT, count);
        con.put(DatabaseHandler.COLUMN_DESCRIPTION, description);
        con.put(DatabaseHandler.COLUMN_PACKAGENAME, packageName);
        con.put(DatabaseHandler.COLUMN_WHATFINGER, whatFinger);
        con.put(DatabaseHandler.COLUMN_WHATACTION, whatAction);
        con.put(DatabaseHandler.COLUMN_DISPLAYNAME, displayName);
        return database.update(DatabaseHandler.TABLE_ACTION, con, DatabaseHandler.COLUMN_ID + "=" + id, null) > 0;
    }

    public ArrayList<InformationFPAction> getAllAction() {
        String[] columns = {DatabaseHandler.COLUMN_ID
                , DatabaseHandler.COLUMN_NAME
                , DatabaseHandler.COLUMN_DESCRIPTION
                , DatabaseHandler.COLUMN_COUNT
                , DatabaseHandler.COLUMN_PACKAGENAME
                , DatabaseHandler.COLUMN_WHATFINGER
                , DatabaseHandler.COLUMN_WHATACTION
                , DatabaseHandler.COLUMN_DISPLAYNAME
        };
        Cursor cursor = database.query(DatabaseHandler.TABLE_ACTION, columns, null, null, null, null, null);
        ArrayList<InformationFPAction> listAction = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                InformationFPAction informationFpAction = new InformationFPAction();
                informationFpAction.setId((cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COLUMN_ID))));
                informationFpAction.setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_NAME)));
                informationFpAction.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_DESCRIPTION)));
                informationFpAction.setCount(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COLUMN_COUNT)));
                informationFpAction.setPackageName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_PACKAGENAME)));
                informationFpAction.setWhatFinger(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_WHATFINGER)));
                informationFpAction.setWhatAction(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_WHATACTION)));
                informationFpAction.setDisplayName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_DISPLAYNAME)));
                listAction.add(informationFpAction);

            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return listAction;
    }

    public boolean deleteAction(int id) {
        return database.delete(DatabaseHandler.TABLE_ACTION, DatabaseHandler.COLUMN_ID + "=" + id, null) > 0;
    }
}
