package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.utils.FILE_NAME
import com.udacity.utils.STATUS
import com.udacity.utils.cancelNotifications

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //Initialize Notification Manager
        notificationManager = ContextCompat.getSystemService(
            application,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelNotifications() //Cancel all current notifications

        val targetFile = intent.getStringExtra(FILE_NAME)
        val targetFileStatus = intent.getStringExtra(STATUS)

        binding.includedDetailContent.apply {
            fileNameDescription.text = getString(R.string.file_name_description, getFileName(targetFile))
            statusDescription.text = getString(R.string.status_description, targetFileStatus)
            statusDescription.setTextColor(
                if (targetFileStatus == "Success") Color.BLACK else Color.RED
            )
        }

        val intent = Intent(this, MainActivity::class.java)
        binding.includedDetailContent.okButton.setOnClickListener {
            startActivity(intent)
            finish()
        }
    }

    // Gets Repository name form intent extra and returns File name
    private fun getFileName(stringExtra: String?) : String {
        return when (stringExtra?.split(' ')?.first()) {
            "Glide" -> "Glide - Image Loading Library by BumpTech"
            "LoadApp" -> "LoadApp - Current repository by Udacity"
            else -> "Retrofit - Type-safe HTTP client for Android and Java by Square, Inc"
        }
    }

}
