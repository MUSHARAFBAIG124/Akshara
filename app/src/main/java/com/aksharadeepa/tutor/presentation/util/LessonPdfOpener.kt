package com.aksharadeepa.tutor.presentation.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun openLessonPdf(context: Context, chapterId: Long, title: String) {
    val appContext = context.applicationContext
    val output = File(File(appContext.cacheDir, "lessons"), "chapter_$chapterId.pdf")

    if (!output.exists() || output.length() == 0L) {
        Toast.makeText(context, "Preparing offline textbook...", Toast.LENGTH_SHORT).show()
    }

    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
        val pdfFile = try {
            withContext(Dispatchers.IO) {
                prepareLessonPdf(appContext, chapterId)
            }
        } catch (_: IOException) {
            Toast.makeText(context, "Textbook could not be opened", Toast.LENGTH_SHORT).show()
            return@launch
        }

        openPreparedPdf(context, pdfFile, title)
    }
}

private fun prepareLessonPdf(context: Context, chapterId: Long): File {
    val assetPath = "lessons/chapter_$chapterId.pdf"
    val cacheDir = File(context.cacheDir, "lessons").apply { mkdirs() }
    val output = File(cacheDir, "chapter_$chapterId.pdf")

    if (!output.exists() || output.length() == 0L) {
        context.assets.open(assetPath).use { input ->
            output.outputStream().use { outputStream ->
                input.copyTo(outputStream)
            }
        }
    }
    return output
}

private fun openPreparedPdf(context: Context, output: File, title: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        output
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(Intent.createChooser(intent, title))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
    }
}
