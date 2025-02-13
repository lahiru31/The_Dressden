package com.example.advancedandroidapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.advancedandroidapp.R
import com.example.advancedandroidapp.ui.activities.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager,
    private val preferencesManager: PreferencesManager
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_GENERAL,
                    context.getString(R.string.channel_general_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.channel_general_description)
                },
                NotificationChannel(
                    CHANNEL_LOCATION,
                    context.getString(R.string.channel_location_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.channel_location_description)
                },
                NotificationChannel(
                    CHANNEL_MESSAGES,
                    context.getString(R.string.channel_messages_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.channel_messages_description)
                }
            )
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun showNotification(
        title: String,
        content: String,
        channelId: String = CHANNEL_GENERAL,
        notificationId: Int = System.currentTimeMillis().toInt(),
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        autoCancel: Boolean = true,
        largeIcon: Bitmap? = null,
        actions: List<NotificationAction> = emptyList(),
        data: Map<String, String> = emptyMap()
    ) {
        if (!preferencesManager.notificationsEnabled) return
        if (!permissionManager.hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data?.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(autoCancel)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .apply {
                largeIcon?.let { setLargeIcon(it) }
                actions.forEach { action ->
                    addAction(
                        action.icon,
                        action.title,
                        action.pendingIntent
                    )
                }
            }
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun showProgressNotification(
        title: String,
        channelId: String = CHANNEL_GENERAL,
        notificationId: Int = System.currentTimeMillis().toInt(),
        maxProgress: Int = 100,
        indeterminate: Boolean = false
    ): ProgressNotificationUpdater {
        if (!preferencesManager.notificationsEnabled) {
            return object : ProgressNotificationUpdater {
                override fun updateProgress(progress: Int) {}
                override fun complete(content: String) {}
                override fun error(content: String) {}
            }
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setProgress(maxProgress, 0, indeterminate)

        notificationManager.notify(notificationId, builder.build())

        return object : ProgressNotificationUpdater {
            override fun updateProgress(progress: Int) {
                builder.setProgress(maxProgress, progress, indeterminate)
                notificationManager.notify(notificationId, builder.build())
            }

            override fun complete(content: String) {
                builder
                    .setContentText(content)
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(notificationId, builder.build())
            }

            override fun error(content: String) {
                builder
                    .setContentText(content)
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(notificationId, builder.build())
            }
        }
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    companion object {
        const val CHANNEL_GENERAL = "general"
        const val CHANNEL_LOCATION = "location"
        const val CHANNEL_MESSAGES = "messages"
    }
}

data class NotificationAction(
    val icon: Int,
    val title: String,
    val pendingIntent: PendingIntent
)

interface ProgressNotificationUpdater {
    fun updateProgress(progress: Int)
    fun complete(content: String)
    fun error(content: String)
}

sealed class NotificationResult {
    object Success : NotificationResult()
    data class Error(val message: String) : NotificationResult()
    object MissingPermission : NotificationResult()
    object NotificationsDisabled : NotificationResult()
}
