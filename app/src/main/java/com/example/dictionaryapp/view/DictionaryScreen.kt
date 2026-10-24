package com.example.dictionaryapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dictionaryapp.viewmodel.DictionaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(viewModel: DictionaryViewModel) {
    var searchWord by remember { mutableStateOf("") }
    val wordResult = viewModel.wordData
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Dictionary", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // First Section: Search Functionality
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchWord,
                    onValueChange = { searchWord = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    placeholder = { Text("Search a word", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    textStyle = TextStyle(color = Color.Black),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.searchWord(searchWord) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Red)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.searchWord(searchWord) })
                )
            }

            // Second Section: Word Results
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)) // Lighter gray for better contrast
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color.Red)
                    }
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }

                wordResult?.let { wordMeaning ->
                    // Card 1: Main Word Information
                    RoundedCard {
                        Text(text = wordMeaning.word.replaceFirstChar { it.uppercase() }, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        wordMeaning.phonetic?.let {
                            Text(text = it, fontSize = 16.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show all meanings
                    wordMeaning.meanings.forEach { meaning ->
                        RoundedCard {
                            Text(text = meaning.partOfSpeech.replaceFirstChar { it.uppercase() }, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(8.dp))

                            meaning.definitions.forEachIndexed { index, definition ->
                                Text(
                                    text = "${index + 1}. ${definition.definition}",
                                    color = Color.DarkGray,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                )
                                if (!definition.example.isNullOrBlank()) {
                                    Text(
                                        text = "Example: \"${definition.example}\"",
                                        fontStyle = FontStyle.Italic,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            val synonyms = meaning.synonyms
                            val antonyms = meaning.antonyms

                            if (!synonyms.isNullOrEmpty()) {
                                Text(text = "Synonyms: ${synonyms.joinToString()}", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (!antonyms.isNullOrEmpty()) {
                                Text(text = "Antonyms: ${antonyms.joinToString()}", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// Custom Reusable Card Component
@Composable
fun RoundedCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}
