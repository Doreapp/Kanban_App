package com.mandin.antoine.kanbanapp.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.utils.Utils
import kotlinx.android.synthetic.main.dialog_edit_label.*

/**
 * Dialog used to create or edit a label
 */
class DialogEditLabel(
    context: Context,
    val label: Label?,
    val modificationSaver: LabelModificationSaver
) : Dialog(context), ColorPicker.OnColorPickedListener {
    var listener: DialogEditLabelListener? = null

    /**
     * Init views of the dialog
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("DialogEditLabel","onCreate")
        setContentView(R.layout.dialog_edit_label)

        // Listeners
        btnCancel.setOnClickListener { onCancelClick() }
        btnSave.setOnClickListener { onSaveClick() }
        colorPicker.listener = this

        // Display
        displayLabelData()
    }

    private fun displayLabelData(){
        if(label !== null){
            etLabelName.setText(label.title)
            colorPicker.selectColor(label.color)
        }
    }

    /**
     * Called when the user click on "cancel"
     */
    fun onCancelClick() {
        cancel()
        listener?.onCanceled()
    }

    /**
     * Called on click on "save" button
     */
    fun onSaveClick() {
        modificationSaver.save(
            label,
            etLabelName.text.toString(),
            colorPicker.pickedColor
        )
        cancel()
        listener?.onSaved()
    }

    /**
     * Interface used to persist actions of the dialog
     */
    interface LabelModificationSaver {
        /**
         * Save the changes about the [label].
         * [title] is the new title of the label.
         * [color] is the new color of the label. Must be one referenced in [R.array.labelColors].
         * If [label] is null, then it reference the creation of a new label
         */
        fun save(label: Label?, title: String, color: Int)
    }

    interface DialogEditLabelListener {
        fun onCanceled()
        fun onSaved()
    }

    override fun onColorPicked(color: Int) {
        Log.i("DialogEditLabel", "onColorPicked : $color")
        Utils.changeBackgroundColor(etLabelName, color)
    }
}