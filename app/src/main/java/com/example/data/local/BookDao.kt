package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Long): Flow<BookEntity?>

    @Query("""
        SELECT * FROM books 
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%')
          AND (:status = 'ALL' OR status = :status)
          AND (:onlyFavorites = 0 OR isFavorite = 1)
        ORDER BY isFavorite DESC, createdAt DESC
    """)
    fun getFilteredBooks(query: String, status: String, onlyFavorites: Int): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE status = 'READING' ORDER BY createdAt DESC")
    fun getCurrentlyReading(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: Long)

    @Query("SELECT COUNT(*) FROM books")
    fun getTotalBooksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE status = 'COMPLETED'")
    fun getCompletedBooksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE status = 'READING'")
    fun getReadingBooksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE status = 'WANT_TO_READ'")
    fun getWantToReadBooksCount(): Flow<Int>

    @Query("SELECT SUM(currentPage) FROM books")
    fun getTotalPagesRead(): Flow<Int?>

    @Query("SELECT AVG(rating) FROM books WHERE rating > 0")
    fun getAverageRating(): Flow<Float?>
}
