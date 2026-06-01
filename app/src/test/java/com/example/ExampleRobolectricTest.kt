package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.PersonalInfoRecord
import com.example.data.PersonalInfoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: PersonalInfoRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabase.getDatabase(context)
        repository = PersonalInfoRepository(db.personalInfoDao())
        
        // Clean out database between tests synchronously
        runBlocking {
            repository.deleteAll()
        }
    }

    @After
    fun tearDown() {
        // Do not close the singleton helper cache to keep successive tests active
    }

    @Test
    fun read_app_name_string_resource_from_context() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Pocket Binder", appName)
    }

    @Test
    fun test_database_insert_and_retrieve() = runBlocking {
        val record = PersonalInfoRecord(
            fullName = "Bruce Wayne",
            phone = "555-0199",
            email = "bruce@waynecorp.com",
            note = "Gotham Billionaire Philanthropist",
            relationship = "Emergency",
            avatarColorHex = "#F44336"
        )
        val id = repository.insert(record)
        assertTrue("Generated ID should be positive", id > 0)

        val retrievedList = repository.allRecords.first()
        assertEquals(1, retrievedList.size)
        
        val retrieved = retrievedList.first()
        assertEquals("Bruce Wayne", retrieved.fullName)
        assertEquals("555-0199", retrieved.phone)
        assertEquals("bruce@waynecorp.com", retrieved.email)
        assertEquals("Gotham Billionaire Philanthropist", retrieved.note)
        assertEquals("Emergency", retrieved.relationship)
        assertEquals("#F44336", retrieved.avatarColorHex)
    }

    @Test
    fun test_database_update_profile() = runBlocking {
        val record = PersonalInfoRecord(
            fullName = "Clark Kent",
            phone = "555-0144",
            email = "clark.kent@dailyplanet.com",
            note = "Metropolis Newspaper Reporter",
            relationship = "Work",
            avatarColorHex = "#3F51B5"
        )
        val id = repository.insert(record).toInt()
        
        val savedRecord = repository.getRecordById(id)
        assertNotNull(savedRecord)

        val updatedRecord = savedRecord!!.copy(
            fullName = "Clark Kent (Superman)",
            phone = "555-9999",
            note = "Metropolis Hero Reporter"
        )
        repository.update(updatedRecord)

        val freshRecord = repository.getRecordById(id)
        assertEquals("Clark Kent (Superman)", freshRecord?.fullName)
        assertEquals("555-9999", freshRecord?.phone)
        assertEquals("Metropolis Hero Reporter", freshRecord?.note)
    }

    @Test
    fun test_database_delete_profile() = runBlocking {
        val record = PersonalInfoRecord(
            fullName = "Tony Stark",
            phone = "555-9000",
            email = "tony@stark.com",
            note = "Iron Man billionaire",
            relationship = "Other",
            avatarColorHex = "#E91E63"
        )
        val id = repository.insert(record).toInt()
        
        var list = repository.allRecords.first()
        assertEquals(1, list.size)

        val saved = repository.getRecordById(id)
        assertNotNull(saved)

        repository.delete(saved!!)
        
        list = repository.allRecords.first()
        assertEquals(0, list.size)
        
        val deleted = repository.getRecordById(id)
        assertNull(deleted)
    }
}
