package com.example.rushali.library.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rushali on 13/10/18.
 */

public class UserDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "users.db";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE = "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserContract.UserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                UserContract.UserEntry.COLUMN_UID + " TEXT NOT NULL, " +
                UserContract.UserEntry.COLUMN_FINE + " INTEGER DEFAULT 0, " +
                UserContract.UserEntry.COLUMN_DEPT + " TEXT NOT NULL, " +
                UserContract.UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                UserContract.UserEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                UserContract.UserEntry.COLUMN_ISSUED + " TEXT NOT NULL, "+
                UserContract.UserEntry.COLUMN_IDATE + " TEXT NOT NULL, "+
                UserContract.UserEntry.COLUMN_NUMBER + " INTEGER DEFAULT 0, "+
                UserContract.UserEntry.COLUMN_RESERVE + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
