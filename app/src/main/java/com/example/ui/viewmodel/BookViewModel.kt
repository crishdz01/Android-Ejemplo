package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookEntity
import com.example.data.repository.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    BOOKS,
    STATS,
    FAVORITES
}

data class ReadingStats(
    val totalBooks: Int = 0,
    val completedBooks: Int = 0,
    val readingBooks: Int = 0,
    val wantToReadBooks: Int = 0,
    val totalPagesRead: Int = 0,
    val averageRating: Float = 0f
)

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _currentTab = MutableStateFlow(MainTab.BOOKS)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow("ALL")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    // Filter criteria flow
    private val filterCriteria = combine(
        _searchQuery,
        _selectedStatus,
        _onlyFavorites,
        _currentTab
    ) { query, status, favoritesOnly, tab ->
        val effectiveFavoritesOnly = favoritesOnly || (tab == MainTab.FAVORITES)
        Triple(query, status, effectiveFavoritesOnly)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> = filterCriteria.flatMapLatest { (query, status, favoritesOnly) ->
        repository.getFilteredBooks(query, status, favoritesOnly)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val currentlyReading: StateFlow<List<BookEntity>> = repository.currentlyReadingBooks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats: StateFlow<ReadingStats> = repository.allBooks.map { all ->
        val completed = all.count { it.status == "COMPLETED" }
        val reading = all.count { it.status == "READING" }
        val wantToRead = all.count { it.status == "WANT_TO_READ" }
        val pagesRead = all.sumOf { it.currentPage }
        val ratedBooks = all.filter { it.rating > 0 }
        val avgRating = if (ratedBooks.isNotEmpty()) ratedBooks.map { it.rating }.average().toFloat() else 0f
        ReadingStats(
            totalBooks = all.size,
            completedBooks = completed,
            readingBooks = reading,
            wantToReadBooks = wantToRead,
            totalPagesRead = pagesRead,
            averageRating = avgRating
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReadingStats()
    )

    // Form & detail states
    private val _selectedBookForDetail = MutableStateFlow<BookEntity?>(null)
    val selectedBookForDetail: StateFlow<BookEntity?> = _selectedBookForDetail.asStateFlow()

    private val _bookToEdit = MutableStateFlow<BookEntity?>(null)
    val bookToEdit: StateFlow<BookEntity?> = _bookToEdit.asStateFlow()

    private val _isFormOpen = MutableStateFlow(false)
    val isFormOpen: StateFlow<Boolean> = _isFormOpen.asStateFlow()

    private val _bookToDelete = MutableStateFlow<BookEntity?>(null)
    val bookToDelete: StateFlow<BookEntity?> = _bookToDelete.asStateFlow()

    fun setCurrentTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
    }

    fun setOnlyFavorites(favorites: Boolean) {
        _onlyFavorites.value = favorites
    }

    fun openCreateBookDialog() {
        _bookToEdit.value = null
        _isFormOpen.value = true
    }

    fun openEditBookDialog(book: BookEntity) {
        _bookToEdit.value = book
        _isFormOpen.value = true
    }

    fun closeFormDialog() {
        _bookToEdit.value = null
        _isFormOpen.value = false
    }

    fun selectBookDetail(book: BookEntity?) {
        _selectedBookForDetail.value = book
    }

    fun requestDeleteBook(book: BookEntity) {
        _bookToDelete.value = book
    }

    fun cancelDeleteBook() {
        _bookToDelete.value = null
    }

    fun confirmDeleteBook() {
        val book = _bookToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteBook(book)
            if (_selectedBookForDetail.value?.id == book.id) {
                _selectedBookForDetail.value = null
            }
            _bookToDelete.value = null
        }
    }

    fun toggleFavorite(book: BookEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(book)
            if (_selectedBookForDetail.value?.id == book.id) {
                _selectedBookForDetail.value = book.copy(isFavorite = !book.isFavorite)
            }
        }
    }

    fun updateProgress(book: BookEntity, newPage: Int) {
        viewModelScope.launch {
            repository.updateProgress(book, newPage)
            if (_selectedBookForDetail.value?.id == book.id) {
                val boundedPage = newPage.coerceIn(0, if (book.totalPages > 0) book.totalPages else Int.MAX_VALUE)
                val newStatus = if (book.totalPages > 0 && boundedPage >= book.totalPages) "COMPLETED" else book.status
                _selectedBookForDetail.value = book.copy(currentPage = boundedPage, status = newStatus)
            }
        }
    }

    fun saveBook(
        id: Long = 0,
        title: String,
        author: String,
        category: String,
        status: String,
        totalPages: Int,
        currentPage: Int,
        rating: Float,
        notes: String,
        coverColorHex: Long,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            val boundedCurrentPage = currentPage.coerceIn(0, if (totalPages > 0) totalPages else Int.MAX_VALUE)
            val effectiveStatus = if (totalPages > 0 && boundedCurrentPage >= totalPages) {
                "COMPLETED"
            } else {
                status
            }

            val entity = BookEntity(
                id = id,
                title = title.trim(),
                author = author.trim(),
                category = category.trim().ifEmpty { "General" },
                status = effectiveStatus,
                totalPages = totalPages.coerceAtLeast(0),
                currentPage = boundedCurrentPage,
                rating = rating.coerceIn(0f, 5f),
                notes = notes.trim(),
                coverColorHex = coverColorHex,
                isFavorite = isFavorite
            )

            if (id == 0L) {
                repository.insertBook(entity)
            } else {
                repository.updateBook(entity)
                if (_selectedBookForDetail.value?.id == id) {
                    _selectedBookForDetail.value = entity
                }
            }
            closeFormDialog()
        }
    }
}

class BookViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
