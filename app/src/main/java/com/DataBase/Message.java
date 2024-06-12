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

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
