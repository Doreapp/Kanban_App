package com.mandin.antoine.kanbanapp.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithLabels(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "labelId",
        associateBy = Junction(TaskLabelRelation::class)
    )
    var labels: List<Label>
)

