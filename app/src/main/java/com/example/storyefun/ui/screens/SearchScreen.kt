package com.example.storyefun.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.credentials.webauthn.Cbor.Item
import com.example.storyefun.viewModel.SearchViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.foundation.lazy.items

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val filteredBooks by viewModel.filteredBooks.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        BasicTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchBooks(it.text)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredBooks) { book ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = book.name, modifier = Modifier.padding(bottom = 4.dp))
                    Text(text = book.description)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
