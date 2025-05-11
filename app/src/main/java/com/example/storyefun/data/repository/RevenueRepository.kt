package com.example.storyefun.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class RevenueRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getMonthlyRevenue(year: Int, month: Int): Result<Double> {
        return try {
            // Dùng múi giờ UTC, đảm bảo thời gian chính xác
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.clear()
            calendar.set(year, month - 1, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis

            calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfMonth = calendar.timeInMillis

            // Log khoảng thời gian
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            Log.d("RevenueRepo", "Querying for $year-$month: start=${sdf.format(Date(startOfMonth))} ($startOfMonth), end=${sdf.format(Date(endOfMonth))} ($endOfMonth)")

            // Lấy tất cả user
            val usersSnapshot = db.collection("users").get().await()
            var totalRevenue = 0.0

            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id
                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("transactions")
                    .whereGreaterThanOrEqualTo("time", startOfMonth)
                    .whereLessThanOrEqualTo("time", endOfMonth)
                    .get()
                    .await()

                // Log số transaction cho từng user
                Log.d("RevenueRepo", "Found ${snapshot.size()} transactions for user $userId")
                totalRevenue += snapshot.documents.sumOf { doc ->
                    val time = doc.getLong("time")
                    val money = doc.getDouble("money") ?: doc.getLong("money")?.toDouble() ?: 0.0
                    Log.d("RevenueRepo", "Transaction: id=${doc.id}, time=$time (${sdf.format(Date(time ?: 0L))}), money=$money")
                    money
                }
            }

            Log.d("RevenueRepo", "Total revenue for $year-$month: $totalRevenue")
            Result.success(totalRevenue)
        } catch (e: Exception) {
            Log.e("RevenueRepo", "Error fetching revenue for $year-$month: ${e.message}", e)
            Result.failure(e)
        }
    }
}