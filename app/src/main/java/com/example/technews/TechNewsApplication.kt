package com.example.technews

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.technews.worker.DailyCheckWorker
import com.example.technews.worker.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class TechNewsApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Schedule daily check worker
        scheduleDailyCheckWorker()
    }

    private fun scheduleDailyCheckWorker() {
        val dailyWorkRequest =
                PeriodicWorkRequestBuilder<DailyCheckWorker>(
                                repeatInterval = 1,
                                repeatIntervalTimeUnit = TimeUnit.DAYS
                        )
                        .build()

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        DailyCheckWorker.WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        dailyWorkRequest
                )
    }
}
