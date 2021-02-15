package com.mandin.antoine.kanbanapp.views

import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.TaskWithLabels


/**
 * Listener of task edit end
 */
interface TaskObserver {
    fun onTaskEditCanceled(task: TaskWithLabels?)
    fun onTaskEditSaved(task: TaskWithLabels?, title: String, description: String, labels: List<Label>)
    fun onTaskRemoved(task: TaskWithLabels?)
    fun onTaskMoveRight(task: TaskWithLabels)
    fun onTaskMoveLeft(task: TaskWithLabels)
}