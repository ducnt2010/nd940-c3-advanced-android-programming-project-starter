package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var urlDownload = ""
    private var targetName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//        toolbar.title = getString(R.string.load_app)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            getUrl()
            if (urlDownload.isEmpty()) {
                Toast.makeText(this, R.string.please_select_file_message, Toast.LENGTH_SHORT).show()
            } else {
                (it as LoadingButton).setLoadingState(ButtonState.Loading)
                download()
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i(TAG, "onReceive: $id")

            if (id == downloadID) {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = status == DownloadManager.STATUS_SUCCESSFUL
                    Log.i(TAG, "downloaded: $id isSuccess= $isSuccess ")

//                    notificationManager?.cancelNotification()
                    notificationManager?.sendNotification(
                        targetName,
                        isSuccess,
                        context
                    )
                }
                cursor.close()
                custom_button.setLoadingState(ButtonState.Completed)
            }
        }
    }

    private fun getUrl() {
        when (radioGroupUrl.checkedRadioButtonId) {
            R.id.radioButtonGlide -> {
                urlDownload = GLIDE_URL
                targetName = getString(R.string.glide_description)
            }
            R.id.radioButtonLoadApp -> {
                urlDownload = LOAD_APP_URL
                targetName = getString(R.string.loadapp_description)
            }
            R.id.radioButtonRetrofit -> {
                urlDownload = RETROFIT_URL
                targetName = getString(R.string.retrofit_description)
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = getString(R.string.download)

            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun download() {
        Log.i(TAG, "download: $urlDownload")
        val request =
            DownloadManager.Request(Uri.parse(urlDownload))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"

        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"

    }

}
