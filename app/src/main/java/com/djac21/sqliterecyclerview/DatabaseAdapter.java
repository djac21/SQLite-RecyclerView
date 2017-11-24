package com.djac21.sqliterecyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

class DatabaseAdapter {

    private DbHelper helper;

    DatabaseAdapter(Context context) {
        helper = new DbHelper(context);
    }

    long insertData(String title, String text) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.TITLE, title);
        contentValues.put(DbHelper.TEXT, text);
        long id = db.insert(DbHelper.TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    String getAllData(List data) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DbHelper.UID, DbHelper.TITLE, DbHelper.TEXT};
        Cursor cursor = db.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DbHelper.TITLE);
            int index2 = cursor.getColumnIndex(DbHelper.TEXT);
            String title = cursor.getString(index1);
            String address = cursor.getString(index2);
            DataModel dataModel = new DataModel(title, address);
            data.add(dataModel);
        }
        return "";
    }

    public String getData(String title) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DbHelper.TITLE, DbHelper.TEXT};
        Cursor cursor = db.query(DbHelper.TABLE_NAME, columns, DbHelper.TITLE + " = '" + title + "'", null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DbHelper.TITLE);
            int index2 = cursor.getColumnIndex(DbHelper.TEXT);
            String personName = cursor.getString(index1);
            String password = cursor.getString(index2);
            buffer.append(title + " " + password + "\n");
        }
        return buffer.toString();
    }

    public void updateName(String oldName, String newName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.TITLE, newName);
        String[] whereArgs = {oldName};
        int count = db.update(DbHelper.TABLE_NAME, contentValues, DbHelper.TITLE + "=?", whereArgs);
        // return count;
    }

    public boolean deleteRow(String title) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DbHelper.TABLE_NAME, DbHelper.TITLE + "=" + title, null) > 0;
    }

    private static class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "djdatabase";
        private static final String TABLE_NAME = "DJTABLE";
        private static final int DATABASE_VERSION = 3;
        private static final String UID = "_id";
        private static final String TITLE = "title";
        private static final String TEXT = "Password";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + "(" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE
                + " VARCHAR(255)," + TEXT + " VARCHAR(255));";
        private static final String DROP_TABLE = "DROP TABLE" + TABLE_NAME + ";";
        private Context context;

        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}