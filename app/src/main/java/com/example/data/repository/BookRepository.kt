package com.example.data.repository

import com.example.data.local.BookDao
import com.example.data.local.BookEntity
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {

    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()

    val favoriteBooks: Flow<List<BookEntity>> = bookDao.getFavorites()

    val currentlyReadingBooks: Flow<List<BookEntity>> = bookDao.getCurrentlyReading()

    val totalBooksCount: Flow<Int> = bookDao.getTotalBooksCount()

    val completedBooksCount: Flow<Int> = bookDao.getCompletedBooksCount()

    val readingBooksCount: Flow<Int> = bookDao.getReadingBooksCount()

    val wantToReadBooksCount: Flow<Int> = bookDao.getWantToReadBooksCount()

    val totalPagesRead: Flow<Int?> = bookDao.getTotalPagesRead()

    val averageRating: Flow<Float?> = bookDao.getAverageRating()

    fun getFilteredBooks(query: String, status: String, onlyFavorites: Boolean): Flow<List<BookEntity>> {
        return bookDao.getFilteredBooks(
            query = query.trim(),
            status = status,
            onlyFavorites = if (onlyFavorites) 1 else 0
        )
    }

    fun getBookById(id: Long): Flow<BookEntity?> {
        return bookDao.getBookById(id)
    }

    suspend fun insertBook(book: BookEntity): Long {
        return bookDao.insertBook(book)
    }

    suspend fun updateBook(book: BookEntity) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }

    suspend fun deleteBookById(id: Long) {
        bookDao.deleteBookById(id)
    }

    suspend fun toggleFavorite(book: BookEntity) {
        bookDao.updateBook(book.copy(isFavorite = !book.isFavorite))
    }

    suspend fun updateProgress(book: BookEntity, newPage: Int) {
        val boundedPage = newPage.coerceIn(0, if (book.totalPages > 0) book.totalPages else Int.MAX_VALUE)
        val newStatus = if (book.totalPages > 0 && boundedPage >= book.totalPages) {
            "COMPLETED"
        } else if (boundedPage > 0 && book.status == "WANT_TO_READ") {
            "READING"
        } else {
            book.status
        }
        bookDao.updateBook(book.copy(currentPage = boundedPage, status = newStatus))
    }
}
