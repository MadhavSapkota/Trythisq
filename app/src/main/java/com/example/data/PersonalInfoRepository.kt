package com.example.data

import kotlinx.coroutines.flow.Flow

class PersonalInfoRepository(private val personalInfoDao: PersonalInfoDao) {
    val allRecords: Flow<List<PersonalInfoRecord>> = personalInfoDao.getAllRecords()

    suspend fun getRecordById(id: Int): PersonalInfoRecord? {
        return personalInfoDao.getRecordById(id)
    }

    suspend fun insert(record: PersonalInfoRecord): Long {
        return personalInfoDao.insertRecord(record)
    }

    suspend fun update(record: PersonalInfoRecord) {
        personalInfoDao.updateRecord(record)
    }

    suspend fun delete(record: PersonalInfoRecord) {
        personalInfoDao.deleteRecord(record)
    }

    suspend fun deleteAll() {
        personalInfoDao.deleteAllRecords()
    }
}
