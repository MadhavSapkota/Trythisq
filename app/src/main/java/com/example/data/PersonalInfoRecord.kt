package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.Serializable

@Entity(tableName = "personal_info_records")
data class PersonalInfoRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val phone: String,
    val email: String,
    val note: String,
    val relationship: String, // e.g. "Work", "Family", "Friend", "Emergency", "Other"
    val avatarColorHex: String, // Hex color for circular avatar representation
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Dao
interface PersonalInfoDao {
    @Query("SELECT * FROM personal_info_records ORDER BY fullName ASC")
    fun getAllRecords(): Flow<List<PersonalInfoRecord>>

    @Query("SELECT * FROM personal_info_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Int): PersonalInfoRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PersonalInfoRecord): Long

    @Update
    suspend fun updateRecord(record: PersonalInfoRecord)

    @Delete
    suspend fun deleteRecord(record: PersonalInfoRecord)

    @Query("DELETE FROM personal_info_records")
    suspend fun deleteAllRecords()
}
