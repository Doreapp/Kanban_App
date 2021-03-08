package com.mandin.antoine.kanbanapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Label(
    @PrimaryKey(autoGenerate = true) val labelId: Int = 0,
    var title:String,
    var color: Int
)
