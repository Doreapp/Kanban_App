package com.mandin.antoine.kanbanapp.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.utils.Constants
import com.mandin.antoine.kanbanapp.views.view_holders.LabelViewHolder

/**
 * RecyclerView Adapter used to display [Label] horizontally.
 * Allow the user to create a [Label] with [addNewLabel] and to edit an existing
 * [Label] with [onEditClick].
 *
 * * [modificationSaver] is the class used to save modification to labels in a database
 * * [labels] are all the labels to display
 */
class LabelAdapter(
    val labels: ArrayList<Label>,
    val modificationSaver: ModificationSaver
) :
    RecyclerView.Adapter<LabelViewHolder>(),
    LabelViewHolder.Listener {

    /**
     * Currently editing Item position. -1 if none.
     */
    private var editingItemPosition = -1

    /**
     * Boolean saying if we are creating a [Label] or not. false by default.
     */
    private var creating = false

    private fun log(str: String) {
        if (Constants.DEBUG)
            Log.i("LabelAdapter", str)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_EDIT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_edit_label, parent, false)
                LabelViewHolder.EditViewHolder(view, this)
            }
            else /*ITEM_VIEW_TYPE_DISPLAY*/ -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_display_label, parent, false)
                LabelViewHolder.DisplayViewHolder(view, this)
            }
        }
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        if (creating && position == editingItemPosition) {
            holder.update(null)
        } else {
            holder.update(
                labels[position + if (creating) 1 else 0]
            )
        }
    }

    override fun getItemCount(): Int {
        return labels.size + if (creating) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == editingItemPosition)
            ITEM_VIEW_TYPE_EDIT
        else
            ITEM_VIEW_TYPE_DISPLAY
    }

    /**
     * Callback of the want to edit the [label], from the view holder
     * at the position [position].
     * If we are already editing an item (eg. [editingItemPosition] >= 0), abort.
     */
    override fun onEditClick(label: Label, position: Int) {
        log("onEditClick(label=$label, position=$position)")
        if (editingItemPosition == -1) {
            // We aren't editing yet
            editingItemPosition = position
            notifyItemChanged(editingItemPosition)
        }
    }

    /**
     * Callback of the want to save the changes done on [label].
     * [title] and [color] are the new attributes to the label.
     *
     * We assume that this callback is called by the currently editing item and
     * therefore quit editing mode afterward.
     *
     * If the function is called in creation mode (eg. [creating] is true) then create a new label.
     */
    override fun onSaveChanges(label: Label?, title: String, color: Int) {
        log("onSaveChanges(label=$label, title=$title, color=$color, editingItemPosition=$editingItemPosition, creating=$creating)")

        if (creating) {
            val nLabel = modificationSaver.createNewLabel(title, color)
            labels.add(0, nLabel)
            creating = false
            editingItemPosition = -1
            notifyItemChanged(0)
        } else {
            val currentlyEditing = editingItemPosition
            editingItemPosition = -1
            label!!.title = title
            label.color = color
            modificationSaver.saveLabelChanges(label)
            notifyItemChanged(currentlyEditing)
        }
    }

    /**
     * Callback of the want to cancel the changes done on [label].
     *
     * We assume that the callback is called by the currently editing item and therefore
     * quit editing mode afterwards.
     *
     * If the function is called in creation mode (eg. [creating] is true) then we do not create a new [Label].
     */
    override fun onCancelChanges(label: Label?) {
        log("onCancelChanges(label=$label, editingItemPosition=$editingItemPosition, creating=$creating)")
        if (creating) {
            // We were creating a new item
            creating = false
            editingItemPosition = -1
            notifyItemRemoved(0)
        } else {
            val position = editingItemPosition
            editingItemPosition = -1
            notifyItemChanged(position)
        }
    }

    /**
     * Callback of the want to delete [label].
     *
     * We assume that the callback is called by the currently editing item and therefore
     * quit editing mode afterwards.
     */
    override fun onDeleteLabel(label: Label) {
        log("onDeleteLabel(label=$label, editingItemPosition=$editingItemPosition, creating=$creating)")
        val position = editingItemPosition
        editingItemPosition = -1
        labels.remove(label)
        notifyItemRemoved(position)

        modificationSaver.deleteLabel(label)
    }

    /**
     * Add a new label to the list and start editing it.
     * It start the creating mode.
     *
     * If the editing mode was on (eg. [editingItemPosition] >=0) then abort.
     */
    fun addNewLabel() {
        if (editingItemPosition >= 0) {
            return // We already are editing an item
        }
        editingItemPosition = 0
        creating = true
        notifyItemInserted(0)
    }

    companion object {
        const val ITEM_VIEW_TYPE_DISPLAY = 0
        const val ITEM_VIEW_TYPE_EDIT = 1
    }

    /**
     * Interface used to save modifications done on labels
     */
    interface ModificationSaver {
        /**
         * Save update on an existing [Label]
         */
        fun saveLabelChanges(label: Label)

        /**
         * Create a new [Label] with the attributes [title] and [color].
         * Returns the persisted label
         */
        fun createNewLabel(title: String, color: Int): Label

        /**
         * Delete [label] in the storage
         */
        fun deleteLabel(label: Label)
    }
}