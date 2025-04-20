package com.example.storyefun.data.repository

import android.util.Log
import com.example.storyefun.data.models.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class CommentRepository {

    private val db = FirebaseFirestore.getInstance()
    private val booksCollection = db.collection("books")

    // Thêm bình luận vào book
    suspend fun addComment(bookId: String, comment: Comment) {
        try {
            val bookRef = booksCollection.document(bookId)
            // Cập nhật danh sách comments trong document sách
            bookRef.update(
                "comments", FieldValue.arrayUnion(comment)
            ).await()

        } catch (e: Exception) {
            throw e
        }
    }

    // Lấy danh sách bình luận của sách
    suspend fun getComments(bookId: String): List<Comment> {
        return try {
            val snapshot = booksCollection.document(bookId).get().await()
            val commentList = snapshot["comments"] as? List<Map<String, Any>> ?: emptyList()

            // Chuyển từ List<Map> sang List<Comment>
            commentList.map {
                Comment(
                    userId = it["userId"] as? String ?: "",
                    userName = it["userName"] as? String ?: "",
                    date = it["date"] as? String ?: "",
                    content = it["content"] as? String ?: "",
                    type = it["type"] as? String ?: "text"
                )
            }
        } catch (e: Exception) {
            emptyList() // Nếu có lỗi, trả về danh sách rỗng
        }
    }

    // Xóa bình luận khỏi sách
    suspend fun deleteComment(bookId: String, comment: Comment) {
        try {
            val bookRef = booksCollection.document(bookId)
            // Xóa comment khỏi danh sách trong document
            bookRef.update(
                "comments", FieldValue.arrayRemove(comment)
            ).await()
        } catch (e: Exception) {
            throw e
        }
    }
}
