package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.udacity.databinding.ActivityMainBinding
import com.udacity.utils.sendNotification


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var loadingButton: LoadingButton
    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Initialize Loading button
        loadingButton = binding.includedMainContent.customButton

        // Initialize Notification Manager
        notificationManager = ContextCompat.getSystemService(
            application,
            NotificationManager::class.java
        ) as NotificationManager

        // Initialize Download manager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        // Initialize Notification channel
        createChannel(getString(R.string.notification_channel_id), getString(R.string.app_name))

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.includedMainContent.customButton.setOnClickListener {
            radioButtonSelector() // Picks the correct URL to download from based on selected Radio button
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var cursor: Cursor? = null
            var status: Int? = null
            val statusMessage: String?
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val query = DownloadManager.Query().setFilterById(id) //filters DownloadManager queries

                try {
                    cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    }
                } finally {
                    cursor?.close()
                }
            }

            when (status) {
                // sends success Notification & re-enables Radio Buttons for next download
                DownloadManager.STATUS_SUCCESSFUL -> {
                    statusMessage = getString(R.string.status_success)
                    loadingButton.buttonStateManager(ButtonState.Completed)

                    notificationManager.sendNotification(
                        application
                            .getString(
                                R.string.notification_description,
                                getRadioButtonString()
                            ),
                        statusMessage,
                        application)
                    radioButtonsState(true)
                }

                // sends failure Notification & re-enables Radio Buttons for next download
                DownloadManager.STATUS_FAILED -> {
                    statusMessage = getString(R.string.status_failure)
                    loadingButton.buttonStateManager(ButtonState.Completed)

                    notificationManager.sendNotification(
                        application
                            .getString(
                                R.string.notification_description,
                                getRadioButtonString()
                            ),
                        statusMessage,
                        application)
                    radioButtonsState(true)
                }
            }
        }
    }

    private fun download(url: String) {
        loadingButton.buttonStateManager(ButtonState.Loading) //Triggers the animations
        radioButtonsState(false) // Disables the radio buttons

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }

    // Create notification channel
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    description = getString(R.string.app_description)
                }

            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Sets the correct URL based on selected radio button
    private fun radioButtonSelector() {
        val radioGroup = binding.includedMainContent.radioGroup
        when (radioGroup.checkedRadioButtonId) {
            R.id.option_one -> {
                download(GLIDE_URL)
            }
            R.id.option_two -> {
                download(UDACITY_URL)
            }
            R.id.option_three -> {
                download(RETROFIT_URL)
            }
            else -> {
                loadingButton.buttonStateManager(ButtonState.Completed)
                Toast.makeText(this, getString(R.string.error),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Enable / Disable Radio buttons while State.Loading
    private fun radioButtonsState(status: Boolean) {
        val radioGroup = binding.includedMainContent.radioGroup
        radioGroup.children.forEach {
            it.isEnabled = status
        }
    }

    // Gets the first word form selected radio button text
    private fun getRadioButtonString() : String {
        val id = binding.includedMainContent.radioGroup.checkedRadioButtonId
        val checkedOption = findViewById<RadioButton>(id)
        val title = checkedOption.text.toString()
        return title.split(' ').first()
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

}
