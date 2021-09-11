package com.example.book.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntites)


    @Delete
    fun deleteBook(bookEntity: BookEntites)

    @Query("SELECT * from book")
    fun getAllBooks(): List<BookEntites>

    @Query("SELECT * from book WHERE book_id= :bookId")
    fun getBookById(bookId: String): BookEntites

}