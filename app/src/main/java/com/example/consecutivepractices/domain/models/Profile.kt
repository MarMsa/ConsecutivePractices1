package com.example.consecutivepractices.domain.models

data class Profile(
    val id: Long = 1,
    val fullName: String = "",
    val avatarUri: String = "",
    val resumeUrl: String = "",
    val position: String = "",
    val email: String = "",
    val favoritePairTime: String = ""
) {
    fun isEmpty(): Boolean {
        return fullName.isBlank() && avatarUri.isBlank() && resumeUrl.isBlank()
    }

    companion object {
        val EMPTY = Profile()
    }
}