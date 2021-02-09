package com.mandin.antoine.kanbanapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task

@Dao
interface LabelDao {

    @Insert
    fun insertLabel(vararg labels: Label)

    @Delete
    fun deleteLabel(label: Label)

    @Update
    fun updateLabel(vararg label: Label)
}