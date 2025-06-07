package com.example.epublibrary.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.File
import java.io.FileOutputStream

class EpubRepository(private val context: Context) {

    suspend fun loadEpub(assetFileName: String): Book = withContext(Dispatchers.IO) {
        val input = context.assets.open(assetFileName)
        val epubBook = EpubReader().readEpub(input)
        input.close()

        val baseDir = File(context.cacheDir, "epub")
        if (baseDir.exists()) baseDir.deleteRecursively()
        baseDir.mkdirs()

        epubBook.resources.all.forEach { resource ->
            val href = resource.href ?: return@forEach
            val outFile = File(baseDir, href)
            outFile.parentFile?.mkdirs()

            resource.inputStream.use { inputStream ->
                FileOutputStream(outFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        epubBook
    }
}