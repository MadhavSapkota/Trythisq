package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PersonalInfoRecord
import com.example.data.PersonalInfoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PersonalInfoViewModel(
    application: Application,
    private val repository: PersonalInfoRepository
) : AndroidViewModel(application) {

    // --- State Observables ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    // Form fields for dynamic two-way data-binding
    private val _formName = MutableStateFlow("")
    val formName = _formName.asStateFlow()

    private val _formPhone = MutableStateFlow("")
    val formPhone = _formPhone.asStateFlow()

    private val _formEmail = MutableStateFlow("")
    val formEmail = _formEmail.asStateFlow()

    private val _formRelationship = MutableStateFlow("Friend")
    val formRelationship = _formRelationship.asStateFlow()

    private val _formNote = MutableStateFlow("")
    val formNote = _formNote.asStateFlow()

    private val _formColor = MutableStateFlow("#6750A4")
    val formColor = _formColor.asStateFlow()

    // Flag to track whether we are editing a record or creating a new one
    private val _editingRecord = MutableStateFlow<PersonalInfoRecord?>(null)
    val editingRecord = _editingRecord.asStateFlow()

    // Controls dialog visibility
    private val _isFormDialogOpen = MutableStateFlow(false)
    val isFormDialogOpen = _isFormDialogOpen.asStateFlow()

    // Flow of records, filtered in real-time on search and category inputs
    val records: StateFlow<List<PersonalInfoRecord>> = repository.allRecords
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) list else {
                list.filter {
                    it.fullName.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.phone.contains(query, ignoreCase = true) ||
                    it.relationship.contains(query, ignoreCase = true)
                }
            }
        }
        .combine(_selectedCategory) { list, cat ->
            if (cat == "All") list else {
                list.filter { it.relationship.equals(cat, ignoreCase = true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Pre-populate with high-fidelity mock personnel if database is clean
        viewModelScope.launch {
            repository.allRecords.first().let { currentList ->
                if (currentList.isEmpty()) {
                    val sampleData = listOf(
                        PersonalInfoRecord(
                            fullName = "Sarah Jenkins",
                            phone = "+1 (555) 019-2834",
                            email = "sarah.j@company.com",
                            note = "Senior Creative Director. Contact for project design questions.",
                            relationship = "Work",
                            avatarColorHex = "#3F51B5"
                        ),
                        PersonalInfoRecord(
                            fullName = "Uncle Robert",
                            phone = "+1 (555) 014-9988",
                            email = "robert.woods.99@gmail.com",
                            note = "Emergency contact in Seattle. Birthday is January 12th.",
                            relationship = "Family",
                            avatarColorHex = "#F44336"
                        ),
                        PersonalInfoRecord(
                            fullName = "Dr. Clara Alvarez",
                            phone = "+1 (555) 919-4821",
                            email = "c.alvarez@healthclinic.org",
                            note = "Primary clinic doctor. Office is closed on Wednesdays.",
                            relationship = "Emergency",
                            avatarColorHex = "#4CAF50"
                        )
                    )
                    sampleData.forEach { repository.insert(it) }
                }
            }
        }
    }

    // --- Search & Filters ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // --- Form Handlers ---
    fun updateName(name: String) { _formName.value = name }
    fun updatePhone(phone: String) { _formPhone.value = phone }
    fun updateEmail(email: String) { _formEmail.value = email }
    fun updateRelationship(rel: String) { _formRelationship.value = rel }
    fun updateNote(note: String) { _formNote.value = note }
    fun updateColor(hex: String) { _formColor.value = hex }

    fun openAddDialog() {
        _editingRecord.value = null
        _formName.value = ""
        _formPhone.value = ""
        _formEmail.value = ""
        _formRelationship.value = "Friend"
        _formNote.value = ""
        // Assign a fun dynamic random color palette
        val palettes = listOf("#6750A4", "#3F51B5", "#2196F3", "#009688", "#4CAF50", "#FF9800", "#E91E63", "#9C27B0")
        _formColor.value = palettes.random()
        _isFormDialogOpen.value = true
    }

    fun openEditDialog(record: PersonalInfoRecord) {
        _editingRecord.value = record
        _formName.value = record.fullName
        _formPhone.value = record.phone
        _formEmail.value = record.email
        _formRelationship.value = record.relationship
        _formNote.value = record.note
        _formColor.value = record.avatarColorHex
        _isFormDialogOpen.value = true
    }

    fun closeDialog() {
        _isFormDialogOpen.value = false
        _editingRecord.value = null
    }

    fun saveRecord() {
        val name = _formName.value.trim()
        if (name.isBlank()) return // Simple baseline validation

        val phone = _formPhone.value.trim()
        val email = _formEmail.value.trim()
        val relationship = _formRelationship.value
        val note = _formNote.value.trim()
        val color = _formColor.value

        viewModelScope.launch {
            val recordToEdit = _editingRecord.value
            if (recordToEdit != null) {
                // Perform Update
                val updated = recordToEdit.copy(
                    fullName = name,
                    phone = phone,
                    email = email,
                    relationship = relationship,
                    note = note,
                    avatarColorHex = color,
                    timestamp = System.currentTimeMillis()
                )
                repository.update(updated)
            } else {
                // Perform Create
                val newRecord = PersonalInfoRecord(
                    fullName = name,
                    phone = phone,
                    email = email,
                    relationship = relationship,
                    note = note,
                    avatarColorHex = color
                )
                repository.insert(newRecord)
            }
            closeDialog()
        }
    }

    fun deleteRecord(record: PersonalInfoRecord) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}

class PersonalInfoViewModelFactory(
    private val application: Application,
    private val repository: PersonalInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonalInfoViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
