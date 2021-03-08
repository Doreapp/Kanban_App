package com.mandin.antoine.kanbanapp.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.views.view_holders.LabelViewHolder

class LabelAdapter(
    val labels: ArrayList<Label>,
    val modificationSaver: ModificationSaver
) :
    RecyclerView.Adapter<LabelViewHolder>(),
    LabelViewHolder.Listener {

    private var editingItemPosition = -1
    private var creating = false

    private fun log(str: String) {
        Log.i("LabelAdapter", str)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_EDIT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_edit_label, parent, false)
                return LabelViewHolder.EditViewHolder(view, this)
            }
            else /*ITEM_VIEW_TYPE_DISPLAY*/ -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_display_label, parent, false)
                return LabelViewHolder.DisplayViewHolder(view, this)
            }
        }
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        if(creating && position == editingItemPosition){
            holder.update(null)
        } else {
            holder.update(
                labels[position + if(creating) 1 else 0]
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


    override fun onEditClick(label: Label, position: Int) {
        log("onEditClick(label=$label, position=$position)")
        if (editingItemPosition != position) {
            // We aren't editing yet
            editingItemPosition = position
            notifyItemChanged(editingItemPosition)
        }
    }

    override fun onSaveChanges(label: Label?, title: String, color: Int) {
        log("onSaveChanges(label=$label, title=$title, color=$color, editingItemPosition=$editingItemPosition, creating=$creating)")

        if(creating){
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

    override fun onCancelChanges(label: Label?) {
        log("onCancelChanges(label=$label, editingItemPosition=$editingItemPosition, creating=$creating)")
        if(creating) {
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

    override fun onDeleteLabel(label: Label) {
        log("onDeleteLabel(label=$label, editingItemPosition=$editingItemPosition, creating=$creating)")
        val position = editingItemPosition
        editingItemPosition = -1
        labels.remove(label)
        notifyItemRemoved(position)

        modificationSaver.deleteLabel(label)
    }

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

    interface ModificationSaver {
        fun saveLabelChanges(label: Label)
        fun createNewLabel(title: String, color: Int): Label
        fun deleteLabel(label: Label)
    }
}