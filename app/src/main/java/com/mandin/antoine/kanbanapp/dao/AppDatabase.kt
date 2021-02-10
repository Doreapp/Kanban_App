package com.mandin.antoine.kanbanapp.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskLabelRelation

@Database(entities = [Task::class, Label::class, TaskLabelRelation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun labelDao(): LabelDao
}
