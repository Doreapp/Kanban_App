package com.mandin.antoine.kanbanapp.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mandin.antoine.kanbanapp.model.Task

@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): TaskDao
}
