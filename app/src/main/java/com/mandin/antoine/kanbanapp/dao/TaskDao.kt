package com.mandin.antoine.kanbanapp.dao

import androidx.room.*
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskLabelRelation
import com.mandin.antoine.kanbanapp.model.TaskWithLabels

/**
 * Data Access object for [Task] objects
 */
@Dao
interface TaskDao {

    /**
     * Return all [TaskWithLabels]
     */
    @Transaction
    @Query("SELECT * FROM Task")
    suspend fun getTaskWithLabels(): List<TaskWithLabels>

    /**
     * Return label-task relations [TaskWithLabels] for a [taskId]
     */
    @Query("SELECT * FROM TaskLabelRelation WHERE taskId=:taskId")
    suspend fun getTaskLabelRelationsForTask(taskId: Int) : List<TaskLabelRelation>

    /**
     * Delete some [TaskLabelRelation]
     */
    @Delete
    suspend fun deleteTaskLabelRelations(taskLabelRelations: List<TaskLabelRelation>)

    /**
     * Insert some [TaskLabelRelation]
     */
    @Insert
    suspend fun insertTaskLabelRelations(taskLabelRelations: List<TaskLabelRelation>)

    /**
     * Insert some [Task]
     */
    @Insert
    suspend fun insertTasks(vararg tasks: Task)

    /**
     * Delete a [Task]
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Update some [Task]
     */
    @Update
    suspend fun updateTasks(vararg task: Task)
}