package com.example.dictionaryapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.model.DictionaryData
import com.example.dictionaryapp.model.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DictionaryViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var wordData by mutableStateOf<DictionaryData?>(null)
        private set

    // Set when a lookup fails (word not found / network error), so the UI can react
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun searchWord(word: String) {
        if (word.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitInstance.api.getWordMeaning(word.trim())
                wordData = response.firstOrNull()
                if (wordData == null) {
                    errorMessage = "No definition found for \"$word\""
                }
            } catch (e: HttpException) {
                wordData = null
                if (e.code() == 404) {
                    errorMessage = "No definition found for \"$word\""
                } else {
                    errorMessage = "Server error: ${e.code()}. Please try again."
                }
            } catch (e: Exception) {
                wordData = null
                errorMessage = "Couldn't find \"$word\". Check your spelling or connection."
            } finally {
                isLoading = false
            }
        }
    }
}
