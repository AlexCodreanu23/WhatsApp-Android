package com.example.app10

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.provider.BaseColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app10.ui.theme.App10Theme

object FeedReaderContract {
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val NAME = "NAME"
        const val PHONE_NUMBER = "PhoneNumber"
    }
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedReaderContract.FeedEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${FeedReaderContract.FeedEntry.NAME} TEXT," +
            "${FeedReaderContract.FeedEntry.PHONE_NUMBER} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedReaderContract.FeedEntry.TABLE_NAME}"

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }

    fun getAllContacts(): List<Contact> {
        val db = readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.FeedEntry.NAME,
            FeedReaderContract.FeedEntry.PHONE_NUMBER
        )
        val cursor = db.query(
            FeedReaderContract.FeedEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val contacts = mutableListOf<Contact>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.NAME))
                val phoneNumber =
                    getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.PHONE_NUMBER))
                contacts.add(Contact(name, phoneNumber))
            }
        }
        cursor.close()
        return contacts
    }
}


data class Contact(val name: String, val phoneNumber: String)

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var isUserLoggedIn: Boolean
        get() = sharedPreferences.getBoolean("is_user_logged_in", false)
        set(value) {
            sharedPreferences.edit().putBoolean("is_user_logged_in", value).apply()
        }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this)
        val dbHelper = FeedReaderDbHelper(this)

        setContent {
            App10Theme {
                var isUserLoggedIn by remember { mutableStateOf(userPreferences.isUserLoggedIn) }
                var contacts by remember { mutableStateOf(mutableStateListOf<Contact>()) }
                var showAddContactScreen by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isUserLoggedIn) {
                        if(showAddContactScreen){
                            AddContactScreen(
                                context = this,
                                onAddContactSucces = {
                                showAddContactScreen = false

                            },
                                onAddContact =  {name,phoneNumber -> contacts.add(Contact(name,phoneNumber))
                            })
                        }
                        else {
                            MainScreen(contacts = contacts,
                                onAddContactClick = {
                                    showAddContactScreen = true
                                })
                        }
                    } else {
                        SignInScreen(onSignInSuccess = {
                            userPreferences.isUserLoggedIn = true
                            isUserLoggedIn = true
                        })
                    }
                }

            }
        }
        /*
        setContent {
            App10Theme {
                var isUserLoggedIn by remember { mutableStateOf(userPreferences.isUserLoggedIn) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   SignInScreen(onSignInSuccess = {
                            userPreferences.isUserLoggedIn = true
                            isUserLoggedIn = true
                        })
                    }
                }
            }
        }
        */
    }
}

@Composable
fun SignInScreen(onSignInSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isNotBlank() && phoneNumber.isNotBlank()) {
                    onSignInSuccess()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Sign In")
        }
    }
}

@Composable
fun AddContactScreen(context: Context,
                     onAddContactSucces: () -> Unit,
                     onAddContact:(String, String) -> Unit){
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isAddingContact by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = phoneNumber
            , onValueChange = {phoneNumber = it},
            label = {Text("Phone Number")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if(name.isNotBlank() && phoneNumber.isNotBlank()){
                isAddingContact = true
                onAddContact(name, phoneNumber)
                onAddContactSucces()
                val dbHelper = FeedReaderDbHelper(context)
                val db = dbHelper.writableDatabase

                val values = ContentValues().apply {
                    put(FeedReaderContract.FeedEntry.NAME, name)
                    put(FeedReaderContract.FeedEntry.PHONE_NUMBER, phoneNumber)
                }
                db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
            }
        },modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add contact")
        }
    }
}

@Composable
fun MainScreen( contacts: List<Contact>,
                onAddContactClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = "WhatsApp",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(16.dp))
        LazyColumn {
            items(contacts) { contact ->
                Text(
                    text = "${contact.name}",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = {
                onAddContactClick()
            },
        ) {
            Text(text = "Add contact")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App10Theme {
        SignInScreen(onSignInSuccess = {})
    }
}
