package com.example.dictionaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dictionaryapp.ui.theme.AppTheme
import com.example.dictionaryapp.view.DictionaryScreen
import com.example.dictionaryapp.viewmodel.DictionaryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val dictionaryViewModel: DictionaryViewModel = viewModel()
                DictionaryScreen(viewModel = dictionaryViewModel)
            }
        }
    }
}
