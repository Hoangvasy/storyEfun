package com.example.storyefun.viewModel

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data class đại diện cho dữ liệu từ Firebase
data class Item(val id: String = "", val name: String = "", val description: String = "")

// ViewModel quản lý logic dữ liệu và tìm kiếm
class Search {
    private val firestore = FirebaseFirestore.getInstance()
    private val _items = MutableStateFlow<List<Item>>(emptyList()) // Toàn bộ dữ liệu từ Firebase
    val items = _items.asStateFlow()

    private val _filteredItems = MutableStateFlow<List<Item>>(emptyList()) // Dữ liệu sau khi lọc
    val filteredItems = _filteredItems.asStateFlow()

    // Tải dữ liệu từ Firebase
    fun loadData() {
        firestore.collection("items")
            .get()
            .addOnSuccessListener { result ->
                val fetchedItems = result.map { document ->
                    Item(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: ""
                    )
                }
                _items.value = fetchedItems
                _filteredItems.value = fetchedItems // Hiển thị toàn bộ dữ liệu ban đầu
            }
    }

    // Lọc dữ liệu dựa trên từ khóa
    fun search(query: String) {
        _filteredItems.value = if (query.isBlank()) {
            _items.value
        } else {
            _items.value.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                        item.description.contains(query, ignoreCase = true)
            }
        }
    }
}
