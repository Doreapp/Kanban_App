package com.mandin.antoine.kanbanapp.dao

import androidx.room.*
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskLabelRelation
import com.mandin.antoine.kanbanapp.model.TaskWithLabels

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM Task")
    suspend fun getTaskWithLabels(): List<TaskWithLabels>

    @Query("SELECT MAX(taskId) FROM Task")
    suspend fun getMaxTaskId(): Int

    @Query("SELECT * FROM TaskLabelRelation WHERE taskId=:taskId")
    suspend fun getTaskLabelRelationsForTask(taskId: Int) : List<TaskLabelRelation>

    @Delete
    suspend fun deleteTaskLabelRelations(taskLabelRelations: List<TaskLabelRelation>)

    @Insert
    suspend fun insertTaskLabelRelations(taskLabelRelations: List<TaskLabelRelation>)

    @Insert
    suspend fun insertTasks(vararg tasks: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTasks(vararg task: Task)
}