package com.example.epublibrary.util

import nl.siegmann.epublib.domain.Resource

fun Resource.readAsString(): String {
    return inputStream.bufferedReader().use { it.readText() }
}