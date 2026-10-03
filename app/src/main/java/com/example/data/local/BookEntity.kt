package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val category: String = "Ficción",
    val status: String = "READING", // "READING", "COMPLETED", "WANT_TO_READ", "ABANDONED"
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val rating: Float = 0f, // 0 to 5
    val notes: String = "",
    val coverColorHex: Long = 0xFF8B4513,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val progressFraction: Float
        get() = if (totalPages > 0) {
            (currentPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val progressPercent: Int
        get() = (progressFraction * 100).toInt()
}
