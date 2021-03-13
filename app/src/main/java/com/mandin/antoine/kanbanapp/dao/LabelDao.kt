package com.mandin.antoine.kanbanapp.dao

import androidx.room.*
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task

/**
 * Data Access Object used to access/update Label objects into the database
 */
@Dao
interface LabelDao {

    /**
     * Return the current max label id
     */
    @Query("SELECT MAX(labelId) FROM Label")
    suspend fun getMaxLabelId(): Int

    /**
     * Return all labels
     */
    @Query("SELECT * FROM Label")
    suspend fun getAllLabels(): List<Label>

    /**
     * Insert a new label and update its [Label.labelId]
     */
    @Insert
    suspend fun insertLabel(label: Label) : Long

    /**
     * Delete a [Label]
     */
    @Delete
    suspend fun deleteLabel(label: Label)

    /**
     * Update some labels
     */
    @Update
    suspend fun updateLabel(vararg label: Label)

    /**
     * Delete every relations Label-Task linked from a label
     */
    @Query("DELETE FROM TaskLabelRelation WHERE labelId = :labelId")
    suspend fun deleteLabelRelations(labelId: Int)
}