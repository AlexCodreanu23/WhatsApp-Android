package com.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "messaging.db";
    private static final int DATABASE_VERSION = 1;

    // Contacts table
    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE contacts ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT NOT NULL, "
            + "phone TEXT NOT NULL);";

    // Messages table
    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE messages ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "contact_id INTEGER, "
            + "message TEXT NOT NULL, "
            + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(contact_id) REFERENCES contacts(id));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }
}
