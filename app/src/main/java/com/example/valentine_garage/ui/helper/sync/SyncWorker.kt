package com.example.valentine_garage.ui.helper.sync

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.valentine_garage.ui.repositories.ClientRepository
import com.example.valentine_garage.ui.repositories.InvoiceRepository
import com.example.valentine_garage.ui.repositories.JobRepository
import com.example.valentine_garage.ui.repositories.VehicleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val clientRepository: ClientRepository,
    private val jobRepository: JobRepository,
    private val invoiceRepository: InvoiceRepository,
    private val vehicleRepository: VehicleRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "valentine_sync_worker"
        private const val PING_HOST = "8.8.8.8"
        private const val PING_PORT = 53
        private const val PING_TIMEOUT_MS = 3_000
        private const val MAX_ATTEMPTS = 3
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val totalPending = jobRepository.getUnsyncedCount() + invoiceRepository.getUnsyncedCount()

        if (totalPending == 0L) {
            Log.d(TAG, "Nothing to sync, exiting early")
            notificationHelper.dismissUnsyncedNotification()
            return Result.success()
        }

        notify { notificationHelper.notifyPending(totalPending) }

        if (!isNetworkAvailable()) {
            Log.d(TAG, "No network available, scheduling retry")
            notify { notificationHelper.notifyNoInternet() }
            return Result.retry()
        }

        if (!isInternetReachable()) {
            Log.d(TAG, "Internet unreachable, scheduling retry")
            notify { notificationHelper.notifyNoInternet() }
            return Result.retry()
        }

        return try {
            var totalSynced = 0

            notify { notificationHelper.notifyProgress("Uploading clients…") }
            totalSynced += clientRepository.pushUnsyncedClients()

            notify { notificationHelper.notifyProgress("Uploading vehicles…") }
            totalSynced += vehicleRepository.pushUnsyncedVehicles()

            notify { notificationHelper.notifyProgress("Uploading jobs…") }
            totalSynced += jobRepository.pushUnsyncedJobs()

            notify { notificationHelper.notifyProgress("Uploading invoices…") }
            totalSynced += invoiceRepository.pushUnsyncedInvoices()

            notify { notificationHelper.notifyProgress("Pulling latest data…") }
            clientRepository.syncRemoteClients()
            vehicleRepository.syncRemoteVehicles()
            jobRepository.syncRemoteJobs()
            invoiceRepository.syncRemoteInvoices()

            Log.d(TAG, "Sync complete — $totalSynced record(s) uploaded")
            notify { notificationHelper.notifySuccess(totalSynced) }
            // Everything is uploaded — dismiss the unsynced reminder
            notificationHelper.dismissUnsyncedNotification()
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Sync failed on attempt $runAttemptCount", e)
            val message = e.localizedMessage ?: "Unknown error"

            if (runAttemptCount >= MAX_ATTEMPTS) {
                notify { notificationHelper.notifyFailure("Sync failed after $MAX_ATTEMPTS attempts: $message") }
                Result.failure()
            } else {
                notify { notificationHelper.notifyFailure("Sync failed, retrying… ($message)") }
                Result.retry()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private suspend fun isInternetReachable(): Boolean = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(PING_HOST, PING_PORT), PING_TIMEOUT_MS)
                true
            }
        } catch (e: IOException) {
            false
        }
    }

    private fun notify(block: () -> Unit) {
        if (!hasNotificationPermission()) return
        try {
            block()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post notification", e)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}