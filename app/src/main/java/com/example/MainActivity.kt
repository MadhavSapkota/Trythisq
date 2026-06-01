package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.PersonalInfoRepository
import com.example.ui.screens.PersonalInfoScreen
import com.example.viewmodel.PersonalInfoViewModel
import com.example.viewmodel.PersonalInfoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Init local Room DB components
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = PersonalInfoRepository(database.personalInfoDao())
        val factory = PersonalInfoViewModelFactory(application, repository)

        setContent {
            com.example.ui.theme.MyApplicationTheme {
                val viewModel: PersonalInfoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                PersonalInfoScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
