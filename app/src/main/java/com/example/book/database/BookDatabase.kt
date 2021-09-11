package com.example.book.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [BookEntites::class], version = 1)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}