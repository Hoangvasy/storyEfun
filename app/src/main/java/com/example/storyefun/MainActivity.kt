package com.example.storyefun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.storyefun.data.repository.QuestRepository
import com.example.storyefun.navigation.AppNavigation
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.viewModel.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val questRepository = QuestRepository()
    private var elapsedTime = 0L
    private var hasCompletedQuest = false
    private var trackingJob: Job? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Lấy userId từ FirebaseAuth
        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            println("UserId is not set. Please login first.")
        }

        // Theo dõi vòng đời ứng dụng
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        startTrackingOnlineTime()
                    }
                    Lifecycle.Event.ON_STOP -> {
                        stopTrackingOnlineTime()
                    }
                    else -> {}
                }
            }
        })

        setContent {
            val navController = rememberNavController()
            val themeViewModel: ThemeViewModel = viewModel()

            // Observe the isDarkTheme state
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            // Wrap the entire app in AppTheme with the latest isDarkTheme
            AppTheme(darkTheme = isDarkTheme) {
                AppNavigation(navController, themeViewModel)
            }
        }
    }

    private fun startTrackingOnlineTime() {
        userId?.let { currentUserId ->
            if (hasCompletedQuest) return

            trackingJob?.cancel() // Hủy job cũ nếu có
            trackingJob = CoroutineScope(Dispatchers.Main).launch {
                questRepository.resetDailyQuests(currentUserId) // Reset nhiệm vụ hàng ngày
                while (!hasCompletedQuest) {
                    delay(1_000L) // Đợi 1 giây
                    elapsedTime += 1_000L
                    if (elapsedTime >= 60_000L) { // 60 giây
                        val quest = questRepository.getQuests(currentUserId).find { it.type == "online_one_minute" }
                        if (quest != null && !quest.completed) {
                            questRepository.updateQuestProgress(currentUserId, quest.id, 1L)
                            val updatedQuest = questRepository.getQuests(currentUserId).find { it.id == quest.id }
                            if (updatedQuest?.completed == true) {
                                questRepository.completeQuest(currentUserId, quest.id, quest.reward)
                            }
                            hasCompletedQuest = true
                        }
                    }
                }
            }
        }
    }

    private fun stopTrackingOnlineTime() {
        trackingJob?.cancel() // Hủy job khi ứng dụng vào background
        elapsedTime = 0L // Reset thời gian khi dừng
    }
}