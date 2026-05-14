package com.aksharadeepa.tutor.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "akshara_deepa_settings")

class SettingsRepository(context: Context) {
    private val dataStore = context.applicationContext.settingsDataStore

    val dailyGoalCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.dailyGoalDate] == LocalDate.now().toString() &&
            preferences[Keys.dailyGoalDone] == true
    }

    suspend fun recordChapterCompletedToday() {
        dataStore.edit { preferences ->
            preferences[Keys.dailyGoalDate] = LocalDate.now().toString()
            preferences[Keys.dailyGoalDone] = true
        }
    }

    private object Keys {
        val dailyGoalDate = stringPreferencesKey("daily_goal_date")
        val dailyGoalDone = booleanPreferencesKey("daily_goal_done")
    }
}
