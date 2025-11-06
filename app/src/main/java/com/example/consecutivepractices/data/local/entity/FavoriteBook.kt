package com.example.consecutivepractices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_books")
data class FavoriteBook(
    @PrimaryKey
    val id: String,
    val title: String,
    val year: Int,
    val rating: Double,
    val genre: String,
    val author: String,
    val synopsis: String,
    val imageUrl: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toBook(): com.example.consecutivepractices.domain.models.Book {
        return com.example.consecutivepractices.domain.models.Book(
            id = this.id,
            title = this.title,
            year = this.year,
            rating = this.rating,
            genre = this.genre,
            author = this.author,
            synopsis = this.synopsis,
            imageUrl = this.imageUrl
        )
    }
}

fun com.example.consecutivepractices.domain.models.Book.toFavoriteBook(): FavoriteBook {
    return FavoriteBook(
        id = this.id,
        title = this.title,
        year = this.year,
        rating = this.rating,
        genre = this.genre,
        author = this.author,
        synopsis = this.synopsis,
        imageUrl = this.imageUrl
    )
}
