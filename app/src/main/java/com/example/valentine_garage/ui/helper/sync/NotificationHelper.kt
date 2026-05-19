package com.example.valentine_garage.ui.helper.sync

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.valentine_garage.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_PROGRESS  = "sync_progress_channel"
        private const val CHANNEL_ALERTS    = "sync_alerts_channel"
        private const val NOTIF_ID_PROGRESS = 2001
        private const val NOTIF_ID_ALERT    = 2002
        private const val NOTIF_ID_UNSYNCED = 2003
    }

    private val manager = NotificationManagerCompat.from(context)

    init {
        createChannels()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val nm = context.getSystemService(NotificationManager::class.java)

        NotificationChannel(
            CHANNEL_PROGRESS,
            "Sync Progress",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shown while data is being synced"
            nm.createNotificationChannel(this)
        }

        NotificationChannel(
            CHANNEL_ALERTS,
            "Sync Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies when sync completes or fails"
            nm.createNotificationChannel(this)
        }
    }

    private fun cancelProgress() = manager.cancel(NOTIF_ID_PROGRESS)

    // ─── Unsynced data reminder ───────────────────────────────────────────────

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notifyUnsyncedData(count: Long) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setContentTitle("Unsynced data")
            .setContentText("$count record(s) are waiting to be uploaded.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOnlyAlertOnce(true)  // only vibrate/ring on the first post; silent on count updates
            .setAutoCancel(false)    // stays in the tray until sync succeeds
            .build()

        manager.notify(NOTIF_ID_UNSYNCED, notification)
    }

    fun dismissUnsyncedNotification() = manager.cancel(NOTIF_ID_UNSYNCED)

    // ─── Sync progress ────────────────────────────────────────────────────────

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notifyPending(count: Long) {
        val notification = NotificationCompat.Builder(context, CHANNEL_PROGRESS)
            .setContentTitle("Sync starting")
            .setContentText("$count unsynced record(s) found, uploading now…")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setSilent(true)
            .build()

        manager.notify(NOTIF_ID_PROGRESS, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notifyProgress(message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_PROGRESS)
            .setContentTitle("Syncing data")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setSilent(true)
            .build()

        manager.notify(NOTIF_ID_PROGRESS, notification)
    }

    // ─── Sync results ─────────────────────────────────────────────────────────

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notifySuccess(syncedCount: Int) {
        cancelProgress()

        val body = if (syncedCount == 0) {
            "Everything is already up to date."
        } else {
            "$syncedCount record(s) uploaded successfully."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setContentTitle("Sync complete")
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIF_ID_ALERT, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notifyNoInternet() {
        cancelProgress()

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setContentTitle("Sync paused")
            .setContentText("No internet connection. Will retry automatically when you're back online.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIF_ID_ALERT, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notifyFailure(reason: String) {
        cancelProgress()

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setContentTitle("Sync failed")
            .setContentText(reason)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIF_ID_ALERT, notification)
    }
}