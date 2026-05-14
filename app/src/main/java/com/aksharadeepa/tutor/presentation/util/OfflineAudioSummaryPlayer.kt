package com.aksharadeepa.tutor.presentation.util

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Stable
class OfflineAudioSummaryPlayer(private val context: Context) : TextToSpeech.OnInitListener {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var textToSpeech: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready by mutableStateOf(false)
    private var pendingSpeech: PendingSpeech? = null

    var speakingChapterId by mutableLongStateOf(0L)
        private set

    override fun onInit(status: Int) {
        val engine = textToSpeech ?: return
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            val languageResult = engine.setLanguage(Locale("en", "IN"))
            if (languageResult == TextToSpeech.LANG_MISSING_DATA ||
                languageResult == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                engine.language = Locale.ENGLISH
            }
            engine.setSpeechRate(0.9f)
            engine.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = Unit

                    override fun onDone(utteranceId: String?) {
                        mainHandler.post { speakingChapterId = 0L }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        mainHandler.post { speakingChapterId = 0L }
                    }
                }
            )
            pendingSpeech?.let {
                pendingSpeech = null
                speak(it.chapterId, it.title, it.summary)
            }
        } else {
            Toast.makeText(context, "Offline audio engine unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun speak(chapterId: Long, title: String, summary: String) {
        if (!ready) {
            pendingSpeech = PendingSpeech(chapterId, title, summary)
            Toast.makeText(context, "Preparing offline audio summary", Toast.LENGTH_SHORT).show()
            return
        }

        val engine = textToSpeech ?: return
        val text = "$title. $summary"
        speakingChapterId = chapterId
        engine.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            Bundle(),
            "chapter_summary_$chapterId"
        )
    }

    fun stop() {
        pendingSpeech = null
        speakingChapterId = 0L
        textToSpeech?.stop()
    }

    fun release() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    private data class PendingSpeech(
        val chapterId: Long,
        val title: String,
        val summary: String
    )
}

@Composable
fun rememberOfflineAudioSummaryPlayer(): OfflineAudioSummaryPlayer {
    val context = LocalContext.current
    val player = remember { OfflineAudioSummaryPlayer(context) }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    return player
}
