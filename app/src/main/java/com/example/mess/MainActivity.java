package com.example.mess;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.DataBase.Contact;
import com.DataBase.DatabaseDAO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerViewChats;
    private ChatAdapter chatAdapter;
    private List<Contact> chatList;
    private DatabaseDAO databaseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        databaseDAO = new DatabaseDAO(this);

        // Add some dummy contacts for testing
        databaseDAO.addContact(new Contact(1, "John Doe", "123456789"));
        databaseDAO.addContact(new Contact(2, "Jane Smith", "987654321"));

        chatList = new ArrayList<>(databaseDAO.getAllContacts());
        Log.d(TAG, "Number of contacts: " + chatList.size());

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseDAO.close();
    }
}
