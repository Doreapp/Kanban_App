package com.mandin.antoine.kanbanapp.dao

import androidx.room.*
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task

@Dao
interface LabelDao {


    @Query("SELECT MAX(labelId) FROM Label")
    fun getMaxLabelId(): Int

    @Insert
    fun insertLabel(vararg labels: Label)

    @Delete
    fun deleteLabel(label: Label)

    @Update
    fun updateLabel(vararg label: Label)
}