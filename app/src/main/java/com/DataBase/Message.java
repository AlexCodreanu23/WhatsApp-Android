package com.DataBase;

public class Message {
    private int id;
    private int contactId;
    private String message;
    private String timestamp;

    public Message(int id, int contactId, String message, String timestamp) {
        this.id = id;
        this.contactId = contactId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getContactId() {
        return contactId;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
