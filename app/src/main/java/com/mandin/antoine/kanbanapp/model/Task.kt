package com.mandin.antoine.kanbanapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    var title:String,
    var description:String,
    var panel:Int,
    var priority:Int
)
