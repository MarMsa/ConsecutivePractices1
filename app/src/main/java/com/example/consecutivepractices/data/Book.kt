package com.example.consecutivepractices.data

data class Book(
    val id: Int,
    val title: String,
    val year: Int,
    val rating: Double,
    val genre: String,
    val author: String,
    val synopsis: String
)