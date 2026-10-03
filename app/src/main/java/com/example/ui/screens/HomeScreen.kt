package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.BookEntity
import com.example.ui.components.BookCard
import com.example.ui.components.BookCoverMockup
import com.example.ui.components.BookDetailDialog
import com.example.ui.components.BookFormDialog
import com.example.ui.viewmodel.BookViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: BookViewModel) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val currentlyReading by viewModel.currentlyReading.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()

    val selectedBookForDetail by viewModel.selectedBookForDetail.collectAsStateWithLifecycle()
    val isFormOpen by viewModel.isFormOpen.collectAsStateWithLifecycle()
    val bookToEdit by viewModel.bookToEdit.collectAsStateWithLifecycle()
    val bookToDelete by viewModel.bookToDelete.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Base de datos Room",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentTab == MainTab.BOOKS,
                    onClick = { viewModel.setCurrentTab(MainTab.BOOKS) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Mis Libros") },
                    label = { Text("Mis Libros") },
                    modifier = Modifier.testTag("nav_books")
                )
                NavigationBarItem(
                    selected = currentTab == MainTab.FAVORITES,
                    onClick = { viewModel.setCurrentTab(MainTab.FAVORITES) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") },
                    modifier = Modifier.testTag("nav_favorites")
                )
                NavigationBarItem(
                    selected = currentTab == MainTab.STATS,
                    onClick = { viewModel.setCurrentTab(MainTab.STATS) },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Estadísticas") },
                    label = { Text("Estadísticas") },
                    modifier = Modifier.testTag("nav_stats")
                )
            }
        },
        floatingActionButton = {
            if (currentTab != MainTab.STATS) {
                FloatingActionButton(
                    onClick = { viewModel.openCreateBookDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_book_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Registrar nuevo libro"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.STATS -> {
                    StatsScreen(stats = stats, books = books)
                }
                MainTab.BOOKS, MainTab.FAVORITES -> {
                    BookListContent(
                        books = books,
                        currentlyReading = if (currentTab == MainTab.BOOKS && searchQuery.isEmpty() && selectedStatus == "ALL") currentlyReading else emptyList(),
                        searchQuery = searchQuery,
                        selectedStatus = selectedStatus,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onStatusSelect = { viewModel.setSelectedStatus(it) },
                        onBookClick = { viewModel.selectBookDetail(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onQuickAddPages = { viewModel.updateProgress(it, it.currentPage + 10) },
                        onAddNewBook = { viewModel.openCreateBookDialog() }
                    )
                }
            }
        }
    }

    // Detail Dialog
    selectedBookForDetail?.let { book ->
        BookDetailDialog(
            book = book,
            onDismiss = { viewModel.selectBookDetail(null) },
            onEdit = {
                viewModel.selectBookDetail(null)
                viewModel.openEditBookDialog(book)
            },
            onDelete = {
                viewModel.requestDeleteBook(book)
            },
            onToggleFavorite = { viewModel.toggleFavorite(book) },
            onUpdateProgress = { newPage -> viewModel.updateProgress(book, newPage) }
        )
    }

    // Form Dialog (Create / Edit)
    if (isFormOpen) {
        BookFormDialog(
            initialBook = bookToEdit,
            onDismiss = { viewModel.closeFormDialog() },
            onSave = { title, author, cat, status, totalPages, currentPage, rating, notes, color, fav ->
                viewModel.saveBook(
                    id = bookToEdit?.id ?: 0L,
                    title = title,
                    author = author,
                    category = cat,
                    status = status,
                    totalPages = totalPages,
                    currentPage = currentPage,
                    rating = rating,
                    notes = notes,
                    coverColorHex = color,
                    isFavorite = fav
                )
            }
        )
    }

    // Delete Confirmation Dialog
    bookToDelete?.let { book ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeleteBook() },
            title = { Text("¿Eliminar libro?") },
            text = { Text("Se eliminará «${book.title}» de tu base de datos Room de manera permanente.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteBook() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_btn")
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelDeleteBook() },
                    modifier = Modifier.testTag("cancel_delete_btn")
                ) {
                    Text("Cancelar")
                }
            },
            modifier = Modifier.testTag("delete_confirmation_dialog")
        )
    }
}

@Composable
fun BookListContent(
    books: List<BookEntity>,
    currentlyReading: List<BookEntity>,
    searchQuery: String,
    selectedStatus: String,
    onSearchChange: (String) -> Unit,
    onStatusSelect: (String) -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onToggleFavorite: (BookEntity) -> Unit,
    onQuickAddPages: (BookEntity) -> Unit,
    onAddNewBook: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("books_lazy_column"),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar por título, autor o categoría...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_books_input")
            )
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "Todos",
                    "READING" to "Leyendo",
                    "COMPLETED" to "Leídos",
                    "WANT_TO_READ" to "Por leer",
                    "ABANDONED" to "Pausados"
                ).forEach { (statusKey, label) ->
                    FilterChip(
                        selected = selectedStatus == statusKey,
                        onClick = { onStatusSelect(statusKey) },
                        label = { Text(label) },
                        modifier = Modifier.testTag("filter_chip_$statusKey")
                    )
                }
            }
        }

        // "Currently Reading" Highlights section
        if (currentlyReading.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Leyendo Ahora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentlyReading, key = { "reading_${it.id}" }) { book ->
                        Card(
                            onClick = { onBookClick(book) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .width(220.dp)
                                .testTag("reading_card_${book.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BookCoverMockup(
                                        title = book.title,
                                        author = book.author,
                                        coverColor = Color(book.coverColorHex),
                                        modifier = Modifier
                                            .width(46.dp)
                                            .height(64.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = book.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2
                                        )
                                        Text(
                                            text = "${book.progressPercent}%",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Section header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Libros (${books.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Empty State or Books List
        if (books.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No se encontraron libros" else "No hay libros registrados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Prueba buscando con otro término o limpiando los filtros." else "Comienza agregando tu primera lectura con el botón +",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        if (searchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onAddNewBook,
                                modifier = Modifier.testTag("empty_add_book_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Registrar Libro")
                            }
                        }
                    }
                }
            }
        } else {
            items(books, key = { it.id }) { book ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    BookCard(
                        book = book,
                        onClick = { onBookClick(book) },
                        onToggleFavorite = { onToggleFavorite(book) },
                        onQuickAddPages = { onQuickAddPages(book) }
                    )
                }
            }
        }
    }
}
