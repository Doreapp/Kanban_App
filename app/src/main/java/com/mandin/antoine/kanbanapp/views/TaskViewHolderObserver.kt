package com.mandin.antoine.kanbanapp.views

import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.TaskWithLabels


/**
 * Listener of task edit end
 */
interface TaskViewHolderObserver {
    fun startSwipingTask(taskViewHolder: TaskViewHolder)
    fun startReorderingTask(taskViewHolder: TaskViewHolder)
    fun startEditingTask(taskViewHolder: TaskViewHolder)
    fun cancelTaskEdition(taskViewHolder: TaskViewHolder)
    fun saveTaskEdition(taskViewHolder: TaskViewHolder, title: String, description: String, labels: List<Label>)
    fun removeTask(taskViewHolder: TaskViewHolder)
    fun moveTaskRight(taskViewHolder: TaskViewHolder)
    fun moveTaskLeft(taskViewHolder: TaskViewHolder)
}