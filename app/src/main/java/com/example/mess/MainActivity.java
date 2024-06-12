// MainActivity.java
package com.example.mess;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.DataBase.Contact;
import com.DataBase.DatabaseDAO;
import com.DataBase.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChats;
    private ChatAdapter chatAdapter;
    private List<Contact> chatList;
    private DatabaseDAO databaseDAO;
    private ChatClient chatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        databaseDAO = new DatabaseDAO(this);

        // Insert dummy data
        insertDummyData();

        chatList = new ArrayList<>(databaseDAO.getContactsWithMessages());

        chatAdapter = new ChatAdapter(this, chatList);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChats.setAdapter(chatAdapter);

        chatAdapter.setOnItemClickListener(position -> {
            Contact selectedChat = chatList.get(position);
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("chatId", selectedChat.getId());
            intent.putExtra("chatName", selectedChat.getName());
            startActivity(intent);
        });

        findViewById(R.id.buttonViewContacts).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(intent);
        });

        // Initialize and connect the ChatClient
        chatClient = new ChatClient(message -> {
            // Handle received messages here
            Log.d("MainActivity", "Received message: " + message);
        });
        chatClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseDAO.close();
        chatClient.disconnect();
    }

    private void insertDummyData() {
        // Check if the dummy data already exists
        List<Contact> allContacts = databaseDAO.getAllContacts();
        if (allContacts.isEmpty()) {
            // Insert dummy contacts
            Contact contact1 = new Contact(0, "John Doe", "1234567890");
            Contact contact2 = new Contact(0, "Jane Smith", "0987654321");
            long contact1Id = databaseDAO.addContact(contact1);
            long contact2Id = databaseDAO.addContact(contact2);

            // Insert dummy messages for contact1
            Message message1 = new Message(0, (int) contact1Id, "Hello John!", null);
            Message message2 = new Message(0, (int) contact1Id, "How are you?", null);
            databaseDAO.addMessage(message1);
            databaseDAO.addMessage(message2);

            // Insert dummy messages for contact2
            Message message3 = new Message(0, (int) contact2Id, "Hi Jane!", null);
            Message message4 = new Message(0, (int) contact2Id, "Nice to meet you!", null);
            databaseDAO.addMessage(message3);
            databaseDAO.addMessage(message4);
        }
    }
}
