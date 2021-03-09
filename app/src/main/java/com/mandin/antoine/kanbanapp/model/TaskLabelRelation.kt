package com.mandin.antoine.kanbanapp.model

import androidx.room.Entity

/**
 * Class representing the relation between a [Label] and a [Task].
 * Its a n-n relation, meaning that a [Task] may have from 0 to n [Label]s and
 * a [Label] may been related to 0 to n [Task]s
 */
@Entity(primaryKeys = ["taskId", "labelId"])
data class TaskLabelRelation(
    val taskId: Int,
    val labelId: Int
)
