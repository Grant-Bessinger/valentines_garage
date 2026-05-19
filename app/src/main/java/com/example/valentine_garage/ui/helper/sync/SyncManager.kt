package com.example.valentine_garage.ui.helper.sync

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val WORK_TAG             = "valentine_sync"
        private const val WORK_ONE_SHOT        = "valentine_sync_oneshot"
        private const val WORK_PERIODIC        = "valentine_sync_periodic"
        private const val PERIODIC_INTERVAL_HR = 1L
        private const val BACKOFF_SECONDS      = 30L
    }

    private val workManager = WorkManager.getInstance(context)

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun scheduleSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_SECONDS, TimeUnit.SECONDS)
            .addTag(WORK_TAG)
            .build()

        workManager.enqueueUniqueWork(WORK_ONE_SHOT, ExistingWorkPolicy.KEEP, request)
    }

    fun cancelSync() {
        workManager.cancelAllWorkByTag(WORK_TAG)
    }

    fun observeSyncState(): LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(WORK_TAG)
}