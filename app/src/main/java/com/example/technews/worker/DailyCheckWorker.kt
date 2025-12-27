package com.example.technews.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.technews.data.preferences.PreferencesManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyCheckWorker
@AssistedInject
constructor(
        @Assisted private val context: Context,
        @Assisted workerParams: WorkerParameters,
        private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Check if notifications are enabled
            val notificationsEnabled = preferencesManager.isNotificationsEnabled.first()
            if (!notificationsEnabled) {
                return Result.success()
            }

            // Get last opened time
            val lastOpenedTime = preferencesManager.lastOpenedTime.first()

            // Check if user hasn't opened the app today
            if (!hasOpenedToday(lastOpenedTime)) {
                NotificationHelper.showDailyReminderNotification(context)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun hasOpenedToday(lastOpenedTime: Long): Boolean {
        if (lastOpenedTime == 0L) return false

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val thisYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = lastOpenedTime
        val lastOpenedDay = calendar.get(Calendar.DAY_OF_YEAR)
        val lastOpenedYear = calendar.get(Calendar.YEAR)

        return today == lastOpenedDay && thisYear == lastOpenedYear
    }

    companion object {
        const val WORK_NAME = "daily_check_worker"
    }
}
