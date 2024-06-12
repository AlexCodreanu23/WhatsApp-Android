package com.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

public class DatabaseDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public DatabaseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Contacts
    public long addContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("phone", contact.getPhone());
        return db.insert("contacts", null, values);
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = db.query("contacts", null, null, null, null, null, "name ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("phone"));
                contacts.add(new Contact(id, name, phone));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return contacts;
    }

    // Messages
    public long addMessage(Message message) {
        ContentValues values = new ContentValues();
        values.put("contact_id", message.getContactId());
        values.put("message", message.getMessage());
        values.put("timestamp", message.getTimestamp());
        return db.insert("messages", null, values);
    }

    public List<Message> getMessagesByContactId(int contactId) {
        List<Message> messages = new ArrayList<>();
        Cursor cursor = db.query("messages", null, "contact_id = ?", new String[]{String.valueOf(contactId)}, null, null, "timestamp ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String messageText = cursor.getString(cursor.getColumnIndex("message"));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                messages.add(new Message(id, contactId, messageText, timestamp));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return messages;
    }

    public void close() {
        db.close();
    }
}
