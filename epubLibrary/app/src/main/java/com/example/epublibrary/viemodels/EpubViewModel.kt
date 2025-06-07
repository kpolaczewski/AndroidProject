package com.example.epublibrary.viemodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.SpineReference
import com.example.epublibrary.data.EpubRepository

class EpubViewModel(private val epubRepository: EpubRepository) : ViewModel() {

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book

    private val _spine = MutableStateFlow<List<SpineReference>>(emptyList())
    val spine: StateFlow<List<SpineReference>> = _spine

    fun loadBook(assetFileName: String) {
        viewModelScope.launch {
            try {
                val epubBook = epubRepository.loadEpub(assetFileName)
                _book.value = epubBook
                _spine.value = epubBook.spine.spineReferences
            } catch (e: Exception) {
                e.printStackTrace()
                _spine.value = emptyList()
            }
        }
    }
}