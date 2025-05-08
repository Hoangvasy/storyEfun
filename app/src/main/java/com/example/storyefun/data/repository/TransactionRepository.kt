package com.example.storyefun.data.repository

import android.util.Log
import com.example.storyefun.data.models.Transactions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TransactionRepository{
    private val db = FirebaseFirestore.getInstance()
    suspend fun addTransaction(
        userID: String,
        coin: Int,
        money:Double,
    ):Result<Unit> {
        return try {
            val transactionData = hashMapOf(
                "uid" to userID,
                "coin" to coin,
                "money" to money,
                "time" to System.currentTimeMillis()
            )
            val Tran = db.collection("users")
                .document(userID)
                .collection("transactions")
                .add(transactionData).await()
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }

    }
    suspend fun getTransactions(userId: String): Result<List<Transactions>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("transactions")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.data?.let {
                    Transactions(
                        uid = it["uid"] as? String ?: "",
                        coin = (it["coin"] as? Long)?.toInt() ?: it["coin"] as? Int ?: 0,
                        money = (it["money"] as? Long)?.toDouble() ?: it["money"] as? Double ?: 0.0,
                        time = it["time"] as? Long ?: 0L
                    )
                }
            }
            Log.d("TransactionRepo", "Fetched ${transactions.size} transactions for userId=$userId")
            Result.success(transactions)
        } catch (e: Exception) {
            Log.e("TransactionRepo", "Error fetching transactions: ${e.message}")
            Result.failure(e)
        }
    }
}
