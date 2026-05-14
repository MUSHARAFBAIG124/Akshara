package com.aksharadeepa.tutor

import android.content.Context
import com.aksharadeepa.tutor.data.local.TutorDatabase
import com.aksharadeepa.tutor.data.repository.SettingsRepository
import com.aksharadeepa.tutor.data.repository.TutorRepository

class AppContainer(context: Context) {
    private val database = TutorDatabase.getInstance(context)
    val tutorRepository = TutorRepository(database.tutorDao())
    val settingsRepository = SettingsRepository(context)
}
