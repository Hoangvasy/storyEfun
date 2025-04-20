package com.example.storyefun.viewModel

import com.example.storyefun.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//suspend fun searchBooks(nameQuery: String): List<Book> {
//    val firestore = FirebaseFirestore.getInstance()
//    val result = mutableListOf<Book>()
//
//    try {
//        val snapshot = firestore.collection("books")
//            .whereGreaterThanOrEqualTo("name", nameQuery)
//            .whereLessThanOrEqualTo("name", nameQuery + "\uf8ff")
//            .get()
//            .await()
//
//        for (document in snapshot.documents) {
//            document.toObject(Book::class.java)?.let { result.add(it) }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//    return result
//}
//
//suspend fun searchCategory(categoryName: String): List<Book> {
//    val firestore = FirebaseFirestore.getInstance()
//    val result = mutableListOf<Book>()
//
//    try {
//        val categorySnapshot = firestore.collection("categories")
//            .whereEqualTo("name", categoryName)
//            .get()
//            .await()
//
//        val categoryId = categorySnapshot.documents.firstOrNull()?.id
//
//        if (categoryId != null) {
//            val bookSnapshot = firestore.collection("books")
//                .whereEqualTo("categoryId", categoryId)
//                .get()
//                .await()
//
//            for (document in bookSnapshot.documents) {
//                document.toObject(Book::class.java)?.let { result.add(it) }
//            }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//    return result
//}
import com.google.firebase.firestore.Query

fun searchBooks(query: String, searchType: String, onResult: (List<Book>) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val booksRef = firestore.collection("books")

    // Đảm bảo rằng query không rỗng
    if (query.isNotEmpty()) {
        var queryRef: Query = booksRef

        // Nếu tìm kiếm theo thể loại
        if (searchType == "category") {
            queryRef = queryRef.whereArrayContains("categories", query)
        }

        // Nếu tìm kiếm theo tiêu đề
        if (searchType == "title") {
            queryRef.get().addOnSuccessListener { snapshot ->
                val books = mutableListOf<Book>()

                // Lọc qua tất cả các sách và kiểm tra nếu tiêu đề chứa từ khóa tìm kiếm
                for (doc in snapshot) {
                    val book = doc.toObject(Book::class.java)
                    if (book.name.contains(query, ignoreCase = true)) {  // Kiểm tra chuỗi con trong title
                        books.add(book)
                    }
                }
                onResult(books) // Trả về danh sách sách phù hợp
            }.addOnFailureListener {
                // Xử lý lỗi nếu có
                onResult(emptyList())
            }
        }
    }
}
