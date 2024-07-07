package com.example.app10

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FeedReaderDbHelperTest {

    private lateinit var dbHelper: FeedReaderDbHelper

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = FeedReaderDbHelper(context)
    }

    @Test
    fun testGetAllContacts() {
        // Populate the database with test data
        dbHelper.writableDatabase.execSQL("INSERT INTO entry (NAME, PhoneNumber) VALUES ('John Doe', '1234567890')")

        // Retrieve the contacts
        val contacts = dbHelper.getAllContacts()

        // Verify the results
        assertEquals(1, contacts.size)
        assertEquals("John Doe", contacts[0].name)
        assertEquals("1234567890", contacts[0].phoneNumber)
    }

    // Adaugă aici teste suplimentare pentru alte funcționalități ale FeedReaderDbHelper

}
