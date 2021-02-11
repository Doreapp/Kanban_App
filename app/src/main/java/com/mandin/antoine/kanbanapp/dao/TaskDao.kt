package com.mandin.antoine.kanbanapp.dao

import androidx.room.*
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskLabelRelation
import com.mandin.antoine.kanbanapp.model.TaskWithLabels

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM Task")
    fun getTaskWithLabels(): List<TaskWithLabels>

    @Query("SELECT MAX(taskId) FROM Task")
    fun getMaxTaskId(): Int

    @Query("SELECT * FROM TaskLabelRelation WHERE taskId=:taskId")
    fun getTaskLabelRelationsForTask(taskId: Int) : List<TaskLabelRelation>

    @Delete
    fun deleteTaskLabelRelations(taskLabelRelations: List<TaskLabelRelation>)

    @Insert
    fun insertTaskLabelRelations(taskLabelRelations: List<TaskLabelRelation>)

    @Insert
    fun insertTasks(vararg tasks: Task)

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTasks(vararg task: Task)
}