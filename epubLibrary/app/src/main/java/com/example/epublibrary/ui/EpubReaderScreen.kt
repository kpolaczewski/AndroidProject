package com.example.epublibrary.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.epublibrary.util.readAsString
import nl.siegmann.epublib.domain.SpineReference

@Composable
fun EpubReaderScreen(
    modifier: Modifier = Modifier,
    spine: List<SpineReference>,
    basePath: String
) {
    var currentChapterIndex by remember { mutableStateOf(0) }

    if (spine.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No chapters found.")
        }
        return
    }

    val linkCss = """
        a {
            color: #1E88E5 !important;
            text-decoration: underline !important;
        }
    """.trimIndent()

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            val rawHtml = spine[currentChapterIndex].resource.readAsString()
            val styledHtml = injectCssIntoHtml(rawHtml, linkCss)

            ChapterWebView(
                htmlContent = styledHtml,
                baseUrl = basePath,
                spine = spine,
                onChapterSelected = { href ->
                    val index = spine.indexOfFirst { it.resource.href == href }
                    if (index != -1 && index != currentChapterIndex) {
                        currentChapterIndex = index
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { if (currentChapterIndex > 0) currentChapterIndex-- },
                enabled = currentChapterIndex > 0
            ) {
                Text("Previous")
            }
            Text(
                text = "Chapter ${currentChapterIndex + 1} / ${spine.size}",
                modifier = Modifier.alignByBaseline()
            )
            Button(
                onClick = { if (currentChapterIndex < spine.size - 1) currentChapterIndex++ },
                enabled = currentChapterIndex < spine.size - 1
            ) {
                Text("Next")
            }
        }
    }
}

fun injectCssIntoHtml(html: String, css: String): String {
    val styleTag = "<style>$css</style>"
    return if (html.contains("<head>", ignoreCase = true)) {
        html.replaceFirst(Regex("(?i)</head>"), "$styleTag</head>")
    } else if (html.contains("<html>", ignoreCase = true)) {
        html.replaceFirst(Regex("(?i)(<html[^>]*>)"), "$1<head>$styleTag</head>")
    } else {
        "$styleTag$html"
    }
}
