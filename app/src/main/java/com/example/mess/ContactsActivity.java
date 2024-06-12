package com.example.mess;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.DataBase.Contact;
import com.DataBase.DatabaseDAO;

import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewContacts;
    private ContactsAdapter contactsAdapter;
    private DatabaseDAO databaseDAO;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        databaseDAO = new DatabaseDAO(this);

        contactList = databaseDAO.getAllContacts();
        contactsAdapter = new ContactsAdapter(this, contactList);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.setAdapter(contactsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseDAO.close();
    }
}
