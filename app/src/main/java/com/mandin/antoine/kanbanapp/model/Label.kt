package com.mandin.antoine.kanbanapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Object : A label.
 * Used to add some information to a [Task], using [TaskLabelRelation].
 * Contains a [title], unique describer of the label, and a [color].
 */
@Entity
data class Label(
    @PrimaryKey(autoGenerate = true) val labelId: Int = 0,
    var title:String,
    var color: Int
)
