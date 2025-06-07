package com.example.epublibrary.ui

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import nl.siegmann.epublib.domain.SpineReference

@Composable
fun ChapterWebView(
    htmlContent: String,
    baseUrl: String,
    spine: List<SpineReference>,
    onChapterSelected: (href: String) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                settings.allowFileAccess = true
                settings.allowFileAccessFromFileURLs = true
                settings.allowUniversalAccessFromFileURLs = true

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if (url == null) return false

                        return when {
                            url.startsWith("file://") -> {
                                val uri = Uri.parse(url)
                                // Get the relative path from basePath
                                val baseUri = Uri.parse(baseUrl)

                                val href = baseUri.relativize(uri) ?: uri.lastPathSegment ?: url

                                if (spine.any { it.resource.href == href }) {
                                    onChapterSelected(href)
                                    true
                                } else {
                                    view?.loadUrl(url)
                                    true
                                }
                            }
                            url.startsWith("http://") || url.startsWith("https://") -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                view?.context?.startActivity(intent)
                                true
                            }
                            else -> super.shouldOverrideUrlLoading(view, url)
                        }
                    }
                }
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null)
        }
    )
}

private fun Uri.relativize(other: Uri): String? {
    val baseSegments = this.pathSegments
    val otherSegments = other.pathSegments

    if (other.scheme != this.scheme) return null
    if (other.authority != this.authority) return null
    if (otherSegments.size < baseSegments.size) return null

    for (i in baseSegments.indices) {
        if (baseSegments[i] != otherSegments[i]) return null
    }

    val relativeSegments = otherSegments.drop(baseSegments.size)
    return relativeSegments.joinToString("/")
}
