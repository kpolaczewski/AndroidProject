package com.example.epublibrary.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.epublibrary.data.EpubRepository
import com.example.epublibrary.ui.theme.EpubLibraryTheme
import com.example.epublibrary.viemodels.EpubViewModel

class MainActivity : ComponentActivity() {

    // Provide ViewModel with a simple factory for the repository
    private val epubViewModel: EpubViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val repo = EpubRepository(applicationContext)
                @Suppress("UNCHECKED_CAST")
                return EpubViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val book by epubViewModel.book.collectAsState()
            val spine by epubViewModel.spine.collectAsState()

            LaunchedEffect(Unit) {
                epubViewModel.loadBook("fleming-goldfinger.epub")
            }

            EpubLibraryTheme {
                Scaffold { innerPadding ->
                    if (book != null) {
                        EpubReaderScreen(
                            modifier = Modifier.padding(innerPadding),
                            spine = spine,
                            basePath = "file://${applicationContext.cacheDir.absolutePath}/epub/"
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Loading EPUB...")
                        }
                    }
                }
            }
        }
    }
}