package com.example.valentine_garage

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.valentine_garage.ui.helper.sync.NotificationHelper
import com.example.valentine_garage.ui.helper.sync.SyncManager
import com.example.valentine_garage.ui.repositories.InvoiceRepository
import com.example.valentine_garage.ui.repositories.JobRepository
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ValentineGarageApplication: Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var syncManager: SyncManager
    @Inject lateinit var jobRepository: JobRepository
    @Inject lateinit var invoiceRepository: InvoiceRepository
    @Inject lateinit var notificationHelper: NotificationHelper

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        registerConnectivityCallback()
    }

    private fun registerConnectivityCallback() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val hasPending = jobRepository.getUnsyncedCount() + invoiceRepository.getUnsyncedCount() > 0

                        if (hasPending) {
                            Log.d("ValentineApp", "Network available and unsynced data found — scheduling sync")
                            syncManager.scheduleSync()
                        }
                    }
                }

                override fun onUnavailable() {

                    CoroutineScope(Dispatchers.IO).launch {
                        val count = jobRepository.getUnsyncedCount() + invoiceRepository.getUnsyncedCount()
                        if (count > 0) {
                            Log.d("ValentineApp", "Network unavailable — $count record(s) pending")
                            // notify here — but you need the permission check
                            notificationHelper.notifyUnsyncedData(count)
                        }
                    }
                }

                override fun onLost(network: Network) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val count = jobRepository.getUnsyncedCount() + invoiceRepository.getUnsyncedCount()
                        if (count > 0) {
                            notificationHelper.notifyUnsyncedData(count)
                        }
                    }
                }
            }
        )
    }
}