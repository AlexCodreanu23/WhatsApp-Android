package com.example.mess;

public class Chat {
    private String chatId;
    private String chatName;
    private String lastMessage;

    public Chat(String chatId, String chatName, String lastMessage) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.lastMessage = lastMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
