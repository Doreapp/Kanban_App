package com.mandin.antoine.kanbanapp.views.view_holders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.utils.Constants
import com.mandin.antoine.kanbanapp.utils.Utils
import com.mandin.antoine.kanbanapp.views.ColorPicker
import kotlinx.android.synthetic.main.view_holder_display_label.view.*
import kotlinx.android.synthetic.main.view_holder_edit_label.view.*

/**
 * View Holder, in a [RecyclerView], for a [Label]
 */
abstract class LabelViewHolder(
    itemView: View,
    val listener: Listener
) : RecyclerView.ViewHolder(itemView) {

    /**
     * Label to display and/or edit
     */
    protected var label: Label? = null


    protected fun log(str: String) {
        if (Constants.DEBUG)
            Log.i("LabelViewHolder", str)
    }

    /**
     * Update the view holder, with the new [label].
     * [label] is null if we are creating a new label.
     * @See EditViewHolder
     */
    open fun update(label: Label?) {
        this.label = label
    }

    /**
     * View holder used only for displaying a label.
     */
    class DisplayViewHolder(itemView: View, listener: Listener) :
        LabelViewHolder(itemView, listener) {

        /**
         * Init view, listeners
         */
        init {
            itemView.btnEdit.setOnClickListener {
                label?.let {
                    listener.onEditClick(it, adapterPosition)
                }
            }
        }

        override fun update(label: Label?) {
            super.update(label)
            itemView.labelView.label = label
            log("update : label=$label")
        }

        /**
         * Stringify the ViewHolder
         */
        override fun toString(): String {
            return "DisplayViewHolder{label=$label, adapterPosition=$adapterPosition}"
        }
    }

    /**
     * View holder used to edit a lebel, or create a new one
     */
    class EditViewHolder(itemView: View, listener: Listener) :
        LabelViewHolder(itemView, listener), ColorPicker.OnColorPickedListener {

        /**
         * Init views, listeners
         */
        init {
            itemView.btnCancel.setOnClickListener { onCancelClick() }
            itemView.btnSave.setOnClickListener { onSaveClick() }
            itemView.btnDelete.setOnClickListener { onDeleteClick() }
            itemView.colorPicker.listener = this
        }

        override fun update(label: Label?) {
            super.update(label)
            itemView.etLabelName.setText(label?.title ?: "")
            itemView.colorPicker.selectColor(label?.color)
            itemView.btnDelete.visibility = if (label === null) View.GONE else View.VISIBLE
        }

        /**
         * Called when the user click on "cancel"
         */
        fun onCancelClick() {
            listener.onCancelChanges(label)
        }

        /**
         * Called on click on "save" button
         */
        fun onSaveClick() {
            listener.onSaveChanges(
                label,
                itemView.etLabelName.text.toString().trim(),
                itemView.colorPicker.pickedColor
            )
        }

        /**
         * Called on click on "delete" button
         */
        fun onDeleteClick() {
            label?.let {
                listener.onDeleteLabel(it)
            }
        }

        /**
         * Called when a color is picked on the [ColorPicker]
         */
        override fun onColorPicked(color: Int) {
            log("onColorPicked : $color")
            Utils.changeBackgroundColor(itemView.etLabelName, color)
        }

        /**
         * Stringify the ViewHolder
         */
        override fun toString(): String {
            return "EditViewHolder{label=$label, adapterPosition=$adapterPosition}"
        }
    }

    /**
     * Listener of changes upon a [LabelViewHolder]
     */
    interface Listener {
        /**
         * Called when a the user request to edit a [DisplayViewHolder]
         */
        fun onEditClick(label: Label, position: Int)

        /**
         * Called when the user saves its changes on an [EditViewHolder]
         */
        fun onSaveChanges(label: Label?, title: String, color: Int)

        /**
         * Called when the user cancels its changes on an [EditViewHolder]
         */
        fun onCancelChanges(label: Label?)

        /**
         * Call when the user wish to delete the editing label
         */
        fun onDeleteLabel(label: Label)
    }
}