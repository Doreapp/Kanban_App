package com.mandin.antoine.kanbanapp.model

import androidx.room.Entity

@Entity(primaryKeys = ["taskId", "labelId"])
data class TaskLabelRelation(
    val taskId: Int,
    val labelId: Int
)
