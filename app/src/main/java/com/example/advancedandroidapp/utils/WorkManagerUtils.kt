package com.example.advancedandroidapp.utils

import android.content.Context
import androidx.work.*
import com.example.advancedandroidapp.data.models.Location
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val workManager = WorkManager.getInstance(context)

    // Schedule periodic data sync
    fun schedulePeriodicSync(intervalMinutes: Long = 15) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
            intervalMinutes,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }

    // Schedule one-time data upload
    fun scheduleDataUpload(data: Any, tag: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf(
            KEY_UPLOAD_DATA to gson.toJson(data),
            KEY_UPLOAD_TYPE to tag
        )

        val uploadWorkRequest = OneTimeWorkRequestBuilder<DataUploadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(tag)
            .build()

        workManager.enqueue(uploadWorkRequest)
    }

    // Schedule location updates processing
    fun scheduleLocationProcessing(location: Location) {
        val constraints = Constraints.Builder()
            .setRequiresDeviceIdle(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val inputData = workDataOf(
            KEY_LOCATION_DATA to gson.toJson(location)
        )

        val locationWorkRequest = OneTimeWorkRequestBuilder<LocationProcessingWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(locationWorkRequest)
    }

    // Schedule image processing
    fun scheduleImageProcessing(
        imageUri: String,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 80
    ) {
        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()

        val inputData = workDataOf(
            KEY_IMAGE_URI to imageUri,
            KEY_MAX_WIDTH to maxWidth,
            KEY_MAX_HEIGHT to maxHeight,
            KEY_QUALITY to quality
        )

        val imageWorkRequest = OneTimeWorkRequestBuilder<ImageProcessingWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(imageWorkRequest)
    }

    // Schedule notification
    fun scheduleNotification(
        title: String,
        message: String,
        delayMinutes: Long
    ) {
        val inputData = workDataOf(
            KEY_NOTIFICATION_TITLE to title,
            KEY_NOTIFICATION_MESSAGE to message
        )

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

        workManager.enqueue(notificationWorkRequest)
    }

    // Cancel all work by tag
    fun cancelWorkByTag(tag: String) {
        workManager.cancelAllWorkByTag(tag)
    }

    // Cancel unique work by name
    fun cancelUniqueWork(name: String) {
        workManager.cancelUniqueWork(name)
    }

    // Get work info by tag
    fun getWorkInfoByTag(tag: String) = workManager.getWorkInfosByTag(tag)

    // Get work info by unique work name
    fun getWorkInfoByName(name: String) = workManager.getWorkInfosForUniqueWork(name)

    companion object {
        private const val SYNC_WORK_NAME = "data_sync_work"
        
        // Input Data Keys
        private const val KEY_UPLOAD_DATA = "upload_data"
        private const val KEY_UPLOAD_TYPE = "upload_type"
        private const val KEY_LOCATION_DATA = "location_data"
        private const val KEY_IMAGE_URI = "image_uri"
        private const val KEY_MAX_WIDTH = "max_width"
        private const val KEY_MAX_HEIGHT = "max_height"
        private const val KEY_QUALITY = "quality"
        private const val KEY_NOTIFICATION_TITLE = "notification_title"
        private const val KEY_NOTIFICATION_MESSAGE = "notification_message"
    }
}

// Worker Classes
class DataSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            // Implement sync logic here
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

class DataUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val data = inputData.getString(KEY_UPLOAD_DATA)
            val type = inputData.getString(KEY_UPLOAD_TYPE)
            // Implement upload logic here
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val KEY_UPLOAD_DATA = "upload_data"
        private const val KEY_UPLOAD_TYPE = "upload_type"
    }
}

class LocationProcessingWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val locationData = inputData.getString(KEY_LOCATION_DATA)
            // Implement location processing logic here
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val KEY_LOCATION_DATA = "location_data"
    }
}

class ImageProcessingWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val imageUri = inputData.getString(KEY_IMAGE_URI)
            val maxWidth = inputData.getInt(KEY_MAX_WIDTH, 1024)
            val maxHeight = inputData.getInt(KEY_MAX_HEIGHT, 1024)
            val quality = inputData.getInt(KEY_QUALITY, 80)
            // Implement image processing logic here
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val KEY_IMAGE_URI = "image_uri"
        private const val KEY_MAX_WIDTH = "max_width"
        private const val KEY_MAX_HEIGHT = "max_height"
        private const val KEY_QUALITY = "quality"
    }
}

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val title = inputData.getString(KEY_NOTIFICATION_TITLE)
            val message = inputData.getString(KEY_NOTIFICATION_MESSAGE)
            // Implement notification logic here
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val KEY_NOTIFICATION_TITLE = "notification_title"
        private const val KEY_NOTIFICATION_MESSAGE = "notification_message"
    }
}
