package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import kotlinx.android.synthetic.main.view_task.view.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * View holder of a [TaskWithLabels]
 */
class TaskViewHolder(
    itemView: View,
    private val taskViewHolderObserver: TaskViewHolderObserver
) : RecyclerView.ViewHolder(itemView),
    TaskTouchHelperCallback.ItemTouchHelperViewHolder {
    var task: TaskWithLabels? = null

    /**
     * Setter of [editing]
     * If currently editing the task
     */
    var editing = false
        set(value) {
            field = value
            // Enable or disable in function
            itemView.etDescription.isEnabled = value
            itemView.etDescription.setText(task?.task?.description)
            itemView.etTitle.isEnabled = value
            itemView.etTitle.setText(task?.task?.title)
            if (value) {
                // Show description edit (even if empty) + buttons of save/cancel modifications + delete button
                itemView.etDescription.visibility = View.VISIBLE
                itemView.layoutButtons.visibility = View.VISIBLE
                itemView.btnDelete.visibility = View.VISIBLE
                // Hide `edit`, `move horizontally` and `ReOrder` buttons
                itemView.btnEdit.visibility = View.GONE
                itemView.btnMoveHorizontally.visibility = View.GONE
                itemView.btnReorder.visibility = View.GONE
                // Focus the edittext
                focusTitleEditText()
            } else {
                // Hide buttons of save/cancel modifications + delete button
                itemView.layoutButtons.visibility = View.GONE
                itemView.btnDelete.visibility = View.GONE
                // Show `edit`, `move horizontally` and `ReOrder` buttons
                itemView.btnEdit.visibility = View.VISIBLE
                itemView.btnMoveHorizontally.visibility = View.VISIBLE
                itemView.btnReorder.visibility = View.VISIBLE
            }
        }

    private fun log(str: String) {
        Log.i("TaskViewHolder", str)
    }

    init {
        // On cancel edit click
        itemView.btnCancel.setOnClickListener {
            cancel()
        }
        itemView.btnSave.setOnClickListener {
            save()
        }
        itemView.btnDelete.setOnClickListener {
            remove()
        }
        itemView.btnEdit.setOnClickListener {
            edit()
        }
        itemView.btnReorder.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startReordering()
                }
            }
            false
        }
        itemView.btnMoveHorizontally.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startSwiping()
                }
            }
            false
        }

    }

    /**
     * Cancel the modifications made during edition,
     * should be call only when [editing] is true
     */
    fun cancel() {
        log("cancel(). Editing=$editing")
        unFocusEditText()
        taskViewHolderObserver.cancelTaskEdition(this)
    }

    /**
     * Cancel the modifications made during edition,
     * should be call only when [editing] is true
     * TODO -> Give labels to function, in order to save those
     */
    fun save() {
        log("save(). Editing=$editing")
        unFocusEditText()
        taskViewHolderObserver.saveTaskEdition(
            this,
            itemView.etTitle.text.toString(),
            itemView.etDescription.text.toString(),
            ArrayList()
        )
    }

    /**
     * Remove the task
     * should be call only when [editing] is true
     */
    fun remove() {
        log("remove(). Editing=$editing")
        unFocusEditText()
        taskViewHolderObserver.removeTask(this)
    }

    /**
     * Start editing the task
     * should be call only when [editing] is false
     */
    fun edit() {
        log("edit(). Editing=$editing, task=$task")
        taskViewHolderObserver.startEditingTask(this)
    }

    /**
     * Start reordering the task
     * should be call only when [editing] is false
     */
    fun startReordering() {
        log("startReordering(). Editing=$editing, task=$task")
        taskViewHolderObserver.startReorderingTask(this)
    }

    /**
     * Start swiping the task holder
     * should be call only when [editing] is false
     */
    fun startSwiping() {
        log("startSwiping(). Editing=$editing, task=$task")
        taskViewHolderObserver.startSwipingTask(this)

    }

    /**
     * Update the display with the new task ([newTask]) to show.
     * If [newTask] is null, then the view holder represent a new task, not persisted in the database yet
     */
    fun update(newTask: TaskWithLabels?) {
        if (newTask == task) {
            log("update() position=$adapterPosition, task stays the same")
            return
        }
        log("update() position=$adapterPosition, task become $newTask")
        this.task = newTask
        if (task != null) {
            itemView.etTitle.setText(task!!.task.title)
            if (task!!.task.description.isNotBlank()) {
                itemView.etDescription.visibility = View.VISIBLE
                itemView.etDescription.setText(task!!.task.description)
            } else {
                itemView.etDescription.visibility = View.GONE
            }
        } else {
            // New task
            itemView.etTitle.setText("")
            itemView.etDescription.visibility = View.VISIBLE
            itemView.etDescription.setText("")
        }

        // TODO labels
    }

    override fun toString(): String {
        return "TaskViewHolder{editing=$editing, task=$task, adapterPosition=$adapterPosition}"
    }

    override fun onItemSelected() {
        log("onItemSelected (position=$adapterPosition)")
        //itemView.isSelected = true
        //itemView.btnReorder.isPressed = true
    }

    override fun onItemClear() {
        log("onItemClear (position=$adapterPosition)")
        //itemView.isSelected = false
        //itemView.btnReorder.isPressed = false
    }

    private fun focusTitleEditText() {
        log("focusTitleEditText()")
        itemView.etTitle.requestFocus()
        itemView.etTitle.isFocusableInTouchMode = true
        Timer("Focus", false).schedule(object : TimerTask() {
            override fun run() {
                val imm = itemView.context.getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                imm.showSoftInput(
                    itemView.etTitle,
                    InputMethodManager.SHOW_IMPLICIT
                )
                log("focusTitleEditText() : imm=$imm")
            }
        }, 100L)
    }

    private fun unFocusEditText() {
        log("unFocusEditText()")
        val imm = itemView.context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(itemView.etTitle.windowToken, 0)
        //imm.hideSoftInputFromWindow(itemView.etDescription.windowToken,0)
    }
}