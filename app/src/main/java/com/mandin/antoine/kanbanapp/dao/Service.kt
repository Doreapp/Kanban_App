package com.mandin.antoine.kanbanapp.dao

import android.content.Context
import androidx.room.Room
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskLabelRelation
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.utils.Constants
import android.service.autofill.UserData
import java.lang.Exception
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future


class Service(
    context: Context
) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, Constants.DATABASE_NAME
    ).build()
    private val taskDao = database.taskDao()
    private val labelDao = database.labelDao()

    fun updateTaskWithLabels(taskWithLabels: TaskWithLabels) {
        val callable: Callable<Void> = Callable<Void> {
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

            null
        }
        val future: Future<Void> = Executors.newSingleThreadExecutor().submit(callable)
        future.get()
    }

    fun getAllTasksWithLabels(): List<TaskWithLabels> {
        val callable: Callable<List<TaskWithLabels>> = Callable<List<TaskWithLabels>> {
            taskDao.getTaskWithLabels()
        }

        val future: Future<List<TaskWithLabels>> = Executors.newSingleThreadExecutor().submit(callable)

        return future.get()
    }

    fun deleteTaskWithLabels(taskWithLabels: TaskWithLabels){
        val callable: Callable<Void> = Callable<Void> {
            val relations = ArrayList<TaskLabelRelation>()
            for(label in taskWithLabels.labels)
                relations.add(TaskLabelRelation(taskWithLabels.task.taskId, label.labelId))
            taskDao.deleteTaskLabelRelations(relations)
            taskDao.deleteTask(taskWithLabels.task)

            null
        }
        val future: Future<Void> = Executors.newSingleThreadExecutor().submit(callable)
        future.get()
    }

    fun insertTaskWithLabels(task: Task, labels: List<Label>): TaskWithLabels {
        val callable: Callable<TaskWithLabels> = Callable<TaskWithLabels> {
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

            TaskWithLabels(task, labels)
        }

        val future: Future<TaskWithLabels> = Executors.newSingleThreadExecutor().submit(callable)

        return future.get()
    }

    fun clear() {
        val callable: Callable<Void> = Callable<Void> {
            database.clearAllTables()
            null
        }

        val future: Future<Void> = Executors.newSingleThreadExecutor().submit(callable)

        future.get()
    }


    // TODOS
    /*
    * create label
    * edit label
    */
}