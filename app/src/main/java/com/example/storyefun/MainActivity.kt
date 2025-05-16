package com.example.storyefun

import android.os.Bundle
import android.widget.Toast
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
    private var hasCompletedOneMin = false
    private var hasCompletedTwoMin = false
    private var hasCompletedTwentyMin = false

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
        val context = this@MainActivity
        userId?.let { currentUserId ->
            trackingJob?.cancel()
            trackingJob = CoroutineScope(Dispatchers.Main).launch {
                questRepository.resetDailyQuests(currentUserId)

                while (true) {
                    delay(1_000L)
                    elapsedTime += 1_000L

                    val quests = questRepository.getQuests(currentUserId)

                    // 1 phút
                    if (elapsedTime >= 10_000L && !hasCompletedOneMin) {
                        quests.find { it.type == "online_one_minute" }?.let { quest ->
                            if (!quest.completed) {
                                questRepository.updateQuestProgress(currentUserId, quest.id, 1L)

                                questRepository.getQuests(currentUserId)
                                    .find { it.id == quest.id }
                                    ?.takeIf { it.completed }
                                    ?.let {
                                        questRepository.completeQuest(currentUserId, it.id, it.reward)
                                        hasCompletedOneMin = true
                                        Toast.makeText(context, "Bạn đã nhận được phần thưởng Online 1 phút: ${it.reward} coin!", Toast.LENGTH_SHORT).show()
                                    }

                            } else {
                                hasCompletedOneMin = true
                            }
                        }
                    }

                    // 2 phút
                    if (elapsedTime >= 30_000L && !hasCompletedTwoMin) {
                        quests.find { it.type == "online_two_minute" }?.let { quest ->
                            if (!quest.completed) {
                                questRepository.updateQuestProgress(currentUserId, quest.id, 1L)
                                questRepository.getQuests(currentUserId)
                                    .find { it.id == quest.id }
                                    ?.takeIf { it.completed }
                                    ?.let {
                                        questRepository.completeQuest(currentUserId, it.id, it.reward)
                                        hasCompletedTwoMin = true
                                        Toast.makeText(context, "Bạn đã nhận được phần thưởng Online 2 phút: ${it.reward} coin!", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                hasCompletedTwoMin = true
                            }
                        }
                    }

                    // 20 phút
                    if (elapsedTime >= 60_000L && !hasCompletedTwentyMin) {
                        quests.find { it.type == "online_twenty_minute" }?.let { quest ->
                            if (!quest.completed) {
                                questRepository.updateQuestProgress(currentUserId, quest.id, 1L)
                                questRepository.getQuests(currentUserId)
                                    .find { it.id == quest.id }
                                    ?.takeIf { it.completed }
                                    ?.let {
                                        questRepository.completeQuest(currentUserId, it.id, it.reward)
                                        hasCompletedTwentyMin = true
                                        Toast.makeText(context, "Bạn đã nhận được phần thưởng Online 20 phút: ${it.reward} coin!", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                hasCompletedTwentyMin = true
                            }
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