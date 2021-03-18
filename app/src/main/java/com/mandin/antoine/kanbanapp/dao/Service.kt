package com.mandin.antoine.kanbanapp.dao

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskLabelRelation
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.utils.Constants

/**
 * TODO : Function to display the whole content of the DB.
 * Service object, contains useful methods to access/update data base objects
 *
 * @see LabelDao
 * @see TaskDao
 */
class Service(
    context: Context
) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, Constants.DATABASE_NAME
    ).build()
    private val taskDao = database.taskDao()
    private val labelDao = database.labelDao()

    private fun log(str: String) {
        if (Constants.DEBUG)
            Log.i("Service", str)
    }

    /**
     * Update a task with labels.
     * Change [Task] data and links task-label([TaskLabelRelation])
     */
    suspend fun updateTaskWithLabels(taskWithLabels: TaskWithLabels) {
        taskDao.updateTask(taskWithLabels.task)

        val oldLabels = taskDao.getTaskLabelRelationsForTask(taskWithLabels.task.taskId)
        val currentLabels = ArrayList<TaskLabelRelation>()
        for (label in taskWithLabels.labels)
            currentLabels.add(
                TaskLabelRelation(
                    taskWithLabels.task.taskId,
                    label.labelId
                )
            )

        val toRemove = ArrayList<TaskLabelRelation>(oldLabels)
        toRemove.removeAll(currentLabels)
        val toAdd = ArrayList<TaskLabelRelation>(currentLabels)
        toAdd.removeAll(oldLabels)

        taskDao.deleteTaskLabelRelations(toRemove)
        taskDao.insertTaskLabelRelations(toAdd)

    }

    /**
     * Update some [Task]s
     */
    suspend fun updateTasks(tasks: Array<Task>) {
        taskDao.updateTasks(tasks)
    }

    /**
     * Update a [Label]
     */
    suspend fun updateLabel(label: Label) {
        labelDao.updateLabel(label)
    }

    /**
     * Return all task with their labels
     */
    suspend fun getAllTasksWithLabels(): List<TaskWithLabels> {
        return taskDao.getTaskWithLabels()
    }

    /**
     * Return all labels in the database
     */
    suspend fun getAllLabels(): List<Label> {
        return labelDao.getAllLabels()
    }

    /**
     * Delete a [TaskWithLabels]
     */
    suspend fun deleteTaskWithLabels(taskWithLabels: TaskWithLabels) {
        val relations = ArrayList<TaskLabelRelation>()
        for (label in taskWithLabels.labels)
            relations.add(TaskLabelRelation(taskWithLabels.task.taskId, label.labelId))
        taskDao.deleteTaskLabelRelations(relations)
        taskDao.deleteTask(taskWithLabels.task)
    }

    /**
     * Delete a [Label] and all its relations to tasks ([TaskLabelRelation])
     */
    suspend fun deleteLabel(label: Label) {
        labelDao.deleteLabelRelations(label.labelId)

        labelDao.deleteLabel(label)
    }

    /**
     * Insert a [TaskWithLabels] into the database.
     * Returns its persisted value
     */
    suspend fun insertTaskWithLabels(task: Task, labels: List<Label>): TaskWithLabels {
        log("call to insertTaskWithLabels w/ task=$task, labels=${labels.joinToString()}")

        val id = taskDao.insertTask(task).toInt()

        log("insertTaskWithLabels: persisted tasks=$task (id=${task.taskId}//$id)")

        val taskLabelRelations = ArrayList<TaskLabelRelation>()
        for (label in labels)
            taskLabelRelations.add(
                TaskLabelRelation(
                    id,
                    label.labelId
                )
            )

        log("insertTaskWithLabels: relations to persist = ${taskLabelRelations.joinToString()}")
        taskDao.insertTaskLabelRelations(taskLabelRelations)

        task.taskId = id
        return TaskWithLabels(task, labels)
    }

    /**
     * Insert a [Label] into the database
     */
    suspend fun insertLabel(label: Label): Label {
        val nId = labelDao.insertLabel(label)
        label.labelId = nId.toInt()
        return label
    }

    /**
     * Close the service and the database
     */
    fun close() {
        database.close()
    }

    suspend fun clearLabelsRelations() {
        taskDao.deleteTaskLabelRelations()
    }
}