package com.mandin.antoine.kanbanapp.dao

import androidx.room.*
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task

@Dao
interface LabelDao {


    @Query("SELECT MAX(labelId) FROM Label")
    suspend fun getMaxLabelId(): Int

    @Query("SELECT * FROM Label")
    suspend fun getAllLabels(): List<Label>

    @Insert
    suspend fun insertLabel(vararg labels: Label)

    @Delete
    suspend fun deleteLabel(label: Label)

    @Update
    suspend fun updateLabel(vararg label: Label)

    @Query("DELETE FROM TaskLabelRelation WHERE labelId = :labelId")
    suspend fun deleteLabelRelations(labelId: Int)
}