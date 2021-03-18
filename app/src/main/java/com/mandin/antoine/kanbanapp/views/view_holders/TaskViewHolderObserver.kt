package com.mandin.antoine.kanbanapp.views.view_holders

import com.mandin.antoine.kanbanapp.model.Label


/**
 * Listener of task edit end
 */
interface TaskViewHolderObserver {
    fun startSwipingTask(taskViewHolder: TaskViewHolder.DisplayViewHolder)
    fun startReorderingTask(taskViewHolder: TaskViewHolder.DisplayViewHolder)
    fun startEditingTask(taskViewHolder: TaskViewHolder.DisplayViewHolder)
    fun cancelTaskEdition(taskViewHolder: TaskViewHolder.EditViewHolder)
    fun saveTaskEdition(taskViewHolder: TaskViewHolder.EditViewHolder, title: String, description: String, labels: List<Label>)
    fun removeTask(taskViewHolder: TaskViewHolder.EditViewHolder)
    fun moveTaskRight(taskViewHolder: TaskViewHolder.DisplayViewHolder)
    fun moveTaskLeft(taskViewHolder: TaskViewHolder.DisplayViewHolder)
}