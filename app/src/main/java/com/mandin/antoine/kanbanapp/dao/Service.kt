package com.mandin.antoine.kanbanapp.dao

import android.content.Context
import androidx.room.Room
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskLabelRelation
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.utils.Constants
import android.service.autofill.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.coroutines.CoroutineContext

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

    /**
     * Update a task with labels.
     * Change [Task] data and links task-label([TaskLabelRelation])
     */
    suspend fun updateTaskWithLabels(taskWithLabels: TaskWithLabels) {
        taskDao.updateTasks(taskWithLabels.task)

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
        taskDao.insertTasks(task)

        val taskLabelRelations = ArrayList<TaskLabelRelation>()
        for (label in labels)
            taskLabelRelations.add(
                TaskLabelRelation(
                    task.taskId,
                    label.labelId
                )
            )

        taskDao.insertTaskLabelRelations(taskLabelRelations)

        return TaskWithLabels(task, labels)
    }

    /**
     * Insert a [Label] into the database
     */
    suspend fun insertLabel(label: Label): Label {
        labelDao.insertLabel(label)
        return label
    }

    /**
     * Close the service and the database
     */
    fun close() {
        database.close()
    }
}