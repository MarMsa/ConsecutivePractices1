package com.example.consecutivepractices.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookResponse(
    @Json(name = "items") val items: List<BookDto>?,
    @Json(name = "totalItems") val totalItems: Int
)

@JsonClass(generateAdapter = true)
data class BookDto(
    @Json(name = "id") val id: String,
    @Json(name = "volumeInfo") val volumeInfo: VolumeInfo?
) {
    fun toBook(): com.example.consecutivepractices.domain.models.Book {
        return com.example.consecutivepractices.domain.models.Book(
            id = this.id,
            title = volumeInfo?.title ?: "Неизвестное название",
            year = volumeInfo?.publishedDate?.substring(0, 4)?.toIntOrNull() ?: 0,
            rating = volumeInfo?.averageRating ?: 0.0,
            genre = volumeInfo?.categories?.joinToString() ?: "Неизвестный жанр",
            author = volumeInfo?.authors?.joinToString() ?: "Неизвестный автор",
            synopsis = volumeInfo?.description ?: "Описание отсутствует",
            imageUrl = volumeInfo?.imageLinks?.thumbnail ?: ""
        )
    }
}


@JsonClass(generateAdapter = true)
data class VolumeInfo(
    @Json(name = "title") val title: String?,
    @Json(name = "authors") val authors: List<String>?,
    @Json(name = "publishedDate") val publishedDate: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "categories") val categories: List<String>?,
    @Json(name = "averageRating") val averageRating: Double?,
    @Json(name = "imageLinks") val imageLinks: ImageLinks?
)

@JsonClass(generateAdapter = true)
data class ImageLinks(
    @Json(name = "thumbnail") val thumbnail: String?,
    @Json(name = "smallThumbnail") val smallThumbnail: String?
)
