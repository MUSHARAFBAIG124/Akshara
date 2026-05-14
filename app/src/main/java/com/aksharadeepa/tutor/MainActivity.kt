package com.aksharadeepa.tutor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aksharadeepa.tutor.presentation.navigation.AksharaDeepaNavHost
import com.aksharadeepa.tutor.presentation.theme.AksharaDeepaTheme
import com.aksharadeepa.tutor.reminder.DailyGoalReminderScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        DailyGoalReminderScheduler.schedule(this)

        val container = (application as TutorApplication).container
        setContent {
            AksharaDeepaTheme {
                AksharaDeepaNavHost(container = container)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 42)
        }
    }
}
