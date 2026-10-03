package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BookEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Registro de Libros", appName)
    }

    @Test
    fun `book entity calculates progress percent correctly`() {
        val book = BookEntity(
            title = "Don Quijote",
            author = "Cervantes",
            totalPages = 200,
            currentPage = 50
        )
        assertEquals(25, book.progressPercent)
        assertEquals(0.25f, book.progressFraction, 0.001f)
    }

    @Test
    fun `book entity handles completed bounds`() {
        val book = BookEntity(
            title = "Cien años de soledad",
            author = "García Márquez",
            totalPages = 100,
            currentPage = 100
        )
        assertEquals(100, book.progressPercent)
        assertEquals(1.0f, book.progressFraction, 0.001f)
    }
}
