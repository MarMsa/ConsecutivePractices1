package com.example.consecutivepractices.domain.models

data class FilterPreferences(
    val genre: String = "",
    val minRating: Double = 0.0,
    val author: String = ""
)
