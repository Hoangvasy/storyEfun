package com.example.storyefun.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SearchViewModel: ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _books = MutableStateFlow(listOf<Book>())
    val books = searchText
        .combine(_books) { text, books ->
            if(text.isBlank()) {
                books
            } else {
                books.filter {
                    it.doesMatchSearch(text)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _books.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}

data class Book (
    val name: String,
    val category: String
) {
    fun doesMatchSearch(query: String): Boolean {
        val matchingCombinations = listOf(
            "$name", "$category"
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
