package com.example.consecutivepractices.domain.models

data class Book(
    val id: String,
    val title: String,
    val year: Int,
    val rating: Double,
    val genre: String,
    val author: String,
    val synopsis: String,
    val imageUrl: String
)
