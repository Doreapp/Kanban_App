package com.mandin.antoine.kanbanapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey val taskId: Int,
    val title:String,
    val description:String,
    val panel:Int,
    val order:Int
)
