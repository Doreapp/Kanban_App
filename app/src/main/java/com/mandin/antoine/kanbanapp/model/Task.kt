package com.mandin.antoine.kanbanapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Object Task.
 * Described by its [title] and its [description].
 * [panel] represent the state of the task (in list, to-do, doing, done).
 * [priority] represent the importance of the task, therefore its order into the panel.
 */
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    var title:String,
    var description:String,
    var panel:Int,
    var priority:Int
)
