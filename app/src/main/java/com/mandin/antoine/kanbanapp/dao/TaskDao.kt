package com.mandin.antoine.kanbanapp.dao

import androidx.room.*
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskWithLabels

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM Task")
    fun getPlaylistsWithSongs(): List<TaskWithLabels>

    @Insert
    fun insertTasks(vararg tasks: Task)

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTasks(vararg task: Task)
}