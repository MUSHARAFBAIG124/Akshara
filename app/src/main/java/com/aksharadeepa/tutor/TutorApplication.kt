package com.aksharadeepa.tutor

import android.app.Application
import com.aksharadeepa.tutor.reminder.DailyGoalReminderScheduler

class TutorApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        DailyGoalReminderScheduler.schedule(this)
    }
}
