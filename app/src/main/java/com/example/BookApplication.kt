package com.example

import android.app.Application
import com.example.data.local.BookDatabase
import com.example.data.repository.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BookApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: BookDatabase by lazy {
        BookDatabase.getDatabase(this, applicationScope)
    }

    val repository: BookRepository by lazy {
        BookRepository(database.bookDao())
    }
}
