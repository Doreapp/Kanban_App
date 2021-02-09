package com.mandin.antoine.kanbanapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Label(
    @PrimaryKey val labelId: Int,
    val title:String,
    val color: Int
)
