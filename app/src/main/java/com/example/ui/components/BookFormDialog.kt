package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.BookEntity
import com.example.ui.theme.BookCoverColors

val PredefinedCategories = listOf(
    "Ficción",
    "No Ficción",
    "Ciencia Ficción",
    "Desarrollo Personal",
    "Clásicos",
    "Historia",
    "Filosofía",
    "Tecnología",
    "Novela",
    "Poesía"
)

@Composable
fun BookFormDialog(
    initialBook: BookEntity?,
    onDismiss: () -> Unit,
    onSave: (
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
    ) -> Unit
) {
    var title by remember { mutableStateOf(initialBook?.title ?: "") }
    var author by remember { mutableStateOf(initialBook?.author ?: "") }
    var category by remember { mutableStateOf(initialBook?.category ?: "Ficción") }
    var status by remember { mutableStateOf(initialBook?.status ?: "READING") }
    var totalPagesText by remember { mutableStateOf(if ((initialBook?.totalPages ?: 0) > 0) initialBook?.totalPages.toString() else "") }
    var currentPageText by remember { mutableStateOf(if ((initialBook?.currentPage ?: 0) > 0) initialBook?.currentPage.toString() else "") }
    var rating by remember { mutableFloatStateOf(initialBook?.rating ?: 0f) }
    var notes by remember { mutableStateOf(initialBook?.notes ?: "") }
    var coverColorHex by remember { mutableLongStateOf(initialBook?.coverColorHex ?: BookCoverColors.first()) }
    var isFavorite by remember { mutableStateOf(initialBook?.isFavorite ?: false) }

    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f)
                .testTag("book_form_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialBook == null) "Registrar Nuevo Libro" else "Editar Libro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("form_close_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Título del libro *") },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("El título es obligatorio") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_book_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Author Input
                OutlinedTextField(
                    value = author,
                    onValueChange = {
                        author = it
                        if (it.isNotBlank()) authorError = false
                    },
                    label = { Text("Autor *") },
                    isError = authorError,
                    supportingText = if (authorError) {
                        { Text("El autor es obligatorio") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_book_author")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Status Selector
                Text(
                    text = "Estado de lectura",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "READING" to "Leyendo",
                        "COMPLETED" to "Leído",
                        "WANT_TO_READ" to "Por leer",
                        "ABANDONED" to "Pausado"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = status == key,
                            onClick = { status = key },
                            label = { Text(label) },
                            modifier = Modifier.testTag("chip_status_$key")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selector & Chips
                Text(
                    text = "Categoría o Género",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PredefinedCategories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("chip_cat_$cat")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría personalizada") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_book_category")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pages Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = totalPagesText,
                        onValueChange = { totalPagesText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Total págs") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_total_pages")
                    )

                    OutlinedTextField(
                        value = currentPageText,
                        onValueChange = { currentPageText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Pág actual") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_current_page")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Color Swatches for Book Cover
                Text(
                    text = "Color de portada / distintivo",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BookCoverColors.forEach { colorHex ->
                        val isSelected = coverColorHex == colorHex
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { coverColorHex = colorHex }
                                .testTag("color_picker_$colorHex"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rating
                Text(
                    text = "Calificación (estrellas)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                RatingStars(
                    rating = rating,
                    starSize = 30,
                    onRatingChanged = { rating = it },
                    modifier = Modifier.testTag("form_rating_stars")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notes / Reseña
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas, citas o reseña personal") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_book_notes")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("form_cancel_btn")
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            var hasError = false
                            if (title.isBlank()) {
                                titleError = true
                                hasError = true
                            }
                            if (author.isBlank()) {
                                authorError = true
                                hasError = true
                            }
                            if (!hasError) {
                                val total = totalPagesText.toIntOrNull() ?: 0
                                val current = currentPageText.toIntOrNull() ?: 0
                                onSave(
                                    title,
                                    author,
                                    category,
                                    status,
                                    total,
                                    current,
                                    rating,
                                    notes,
                                    coverColorHex,
                                    isFavorite
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("form_save_btn")
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
