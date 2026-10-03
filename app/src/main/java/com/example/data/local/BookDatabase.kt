package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "books_database"
                )
                    .addCallback(BookDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class BookDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialLibrary(database.bookDao())
                    }
                }
            }

            suspend fun populateInitialLibrary(dao: BookDao) {
                dao.insertBook(
                    BookEntity(
                        title = "Cien años de soledad",
                        author = "Gabriel García Márquez",
                        category = "Realismo Mágico",
                        status = "COMPLETED",
                        totalPages = 471,
                        currentPage = 471,
                        rating = 5f,
                        notes = "La saga de los Buendía en Macondo. Una de las cumbres universales de la lengua española.",
                        coverColorHex = 0xFF8B4513, // Saddle Leather
                        isFavorite = true
                    )
                )
                dao.insertBook(
                    BookEntity(
                        title = "Hábitos Atómicos",
                        author = "James Clear",
                        category = "Desarrollo Personal",
                        status = "READING",
                        totalPages = 320,
                        currentPage = 215,
                        rating = 4.5f,
                        notes = "Enfocarse en los sistemas e identidad antes que en las metas a secas.",
                        coverColorHex = 0xFF065F46, // Emerald
                        isFavorite = true
                    )
                )
                dao.insertBook(
                    BookEntity(
                        title = "El Principito",
                        author = "Antoine de Saint-Exupéry",
                        category = "Ficción Filosófica",
                        status = "COMPLETED",
                        totalPages = 96,
                        currentPage = 96,
                        rating = 5f,
                        notes = "«Solo con el corazón se puede ver bien; lo esencial es invisible a los ojos».",
                        coverColorHex = 0xFF1E3A8A, // Indigo
                        isFavorite = false
                    )
                )
                dao.insertBook(
                    BookEntity(
                        title = "Don Quijote de la Mancha",
                        author = "Miguel de Cervantes",
                        category = "Clásicos",
                        status = "WANT_TO_READ",
                        totalPages = 863,
                        currentPage = 0,
                        rating = 0f,
                        notes = "Edición conmemorativa de la RAE con notas explicativas.",
                        coverColorHex = 0xFF991B1B, // Ruby Red
                        isFavorite = false
                    )
                )
                dao.insertBook(
                    BookEntity(
                        title = "Clean Architecture",
                        author = "Robert C. Martin",
                        category = "Tecnología",
                        status = "READING",
                        totalPages = 432,
                        currentPage = 145,
                        rating = 4.0f,
                        notes = "Guía esencial sobre desacoplamiento y arquitectura de software escalable.",
                        coverColorHex = 0xFF0F766E, // Teal
                        isFavorite = false
                    )
                )
            }
        }
    }
}
