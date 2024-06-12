package com.example.mess;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.DataBase.DatabaseDAO;
import com.DataBase.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements ChatClient.MessageListener {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatClient chatClient;
    private static final String TAG = "ChatActivity";
    private DatabaseDAO databaseDAO;
    private int contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        databaseDAO = new DatabaseDAO(this);

        contactId = getIntent().getIntExtra("chatId", -1);
        String chatName = getIntent().getStringExtra("chatName");
        setTitle(chatName);

        messages = new ArrayList<>(databaseDAO.getMessagesByContactId(contactId));
        messageAdapter = new MessageAdapter(messages);

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        chatClient = new ChatClient(this);

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString();
            if (!TextUtils.isEmpty(messageText)) {
                // Send message to server
                chatClient.sendMessage(messageText);

                // Add message to local database and update UI
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Message message = new Message(0, contactId, "Me: " + messageText, timestamp);
                databaseDAO.addMessage(message);

                messages.add(message);
                messageAdapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
                editTextMessage.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(() -> chatClient.connect()).start();
    }

    @Override
    public void onMessageReceived(final String messageText) {
        runOnUiThread(() -> {
            Log.d(TAG, "New message received: " + messageText);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Message message = new Message(0, contactId, messageText, timestamp);
            databaseDAO.addMessage(message);

            messages.add(message);
            messageAdapter.notifyItemInserted(messages.size() - 1);
            recyclerViewMessages.scrollToPosition(messages.size() - 1);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatClient.disconnect();
        databaseDAO.close();
    }
}
