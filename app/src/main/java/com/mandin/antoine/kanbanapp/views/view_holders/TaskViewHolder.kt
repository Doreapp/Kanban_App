package com.mandin.antoine.kanbanapp.views.view_holders

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import kotlinx.android.synthetic.main.view_holder_display_task.view.*
import kotlinx.android.synthetic.main.view_holder_display_task.view.layoutLabels
import kotlinx.android.synthetic.main.view_holder_edit_task.view.*
import java.util.*
import kotlin.collections.ArrayList

abstract class TaskViewHolder(
    itemView: View,
    val taskViewHolderObserver: TaskViewHolderObserver
) : RecyclerView.ViewHolder(itemView) {
    var task: TaskWithLabels? = null

    protected fun log(str: String) {
        Log.i("TaskViewHolder", str)
    }

    open fun update(newTask: TaskWithLabels?) {
        task = newTask
    }

    class DisplayViewHolder(
        itemView: View,
        taskViewHolderObserver: TaskViewHolderObserver,
        allLabels: List<Label>
    ) : TaskViewHolder(itemView, taskViewHolderObserver),
        TaskTouchHelperCallback.ItemTouchHelperViewHolder {

        init {
            // Init label layout
            itemView.layoutLabels.labels = allLabels
            itemView.layoutLabels.editing = false

            // On cancel edit click
            itemView.btnEdit.setOnClickListener {
                edit()
            }
            setTouchListeners()
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setTouchListeners() {
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
         * Update the display with the new task ([newTask]) to show.
         * If [newTask] is null, then the view holder represent a new task, not persisted in the database yet
         */
        override fun update(newTask: TaskWithLabels?) {
            super.update(newTask)

            if (newTask == task) {
                log("update() position=$adapterPosition, task stays the same")
                //return
            }
            log("update() position=$adapterPosition, task become $newTask")
            if (task === null) {
                log("update() task is null")
                return
            }
            itemView.tvTitle.text = task!!.task.title
            if (task!!.task.description.isNotBlank()) {
                itemView.tvDescription.visibility = View.VISIBLE
                itemView.tvDescription.text = task!!.task.description
            } else {
                itemView.tvDescription.visibility = View.GONE
            }

            itemView.layoutLabels.selectedLabels = task!!.labels
        }

        /**
         * Start editing the task
         * should be call only when [editing] is false
         */
        fun edit() {
            log("edit(). task=$task")
            taskViewHolderObserver.startEditingTask(this)
        }

        /**
         * Start reordering the task
         * should be call only when [editing] is false
         */
        fun startReordering() {
            log("startReordering(). task=$task")
            taskViewHolderObserver.startReorderingTask(this)
        }

        /**
         * Start swiping the task holder
         * should be call only when [editing] is false
         */
        fun startSwiping() {
            log("startSwiping(). task=$task")
            taskViewHolderObserver.startSwipingTask(this)
        }

        override fun onItemSelected() {
            log("onItemSelected (position=$adapterPosition)")
        }

        override fun onItemClear() {
            log("onItemClear (position=$adapterPosition)")
        }
    }

    class EditViewHolder(
        itemView: View,
        taskViewHolderObserver: TaskViewHolderObserver,
        allLabels: List<Label>
    ) : TaskViewHolder(itemView, taskViewHolderObserver) {

        init {
            // Init label layout
            itemView.layoutLabels.labels = allLabels
            itemView.layoutLabels.editing = true

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
        }

        /**
         * Update the display with the new task ([newTask]) to show.
         * If [newTask] is null, then the view holder represent a new task, not persisted in the database yet
         */
        override fun update(newTask: TaskWithLabels?) {
            if (newTask == task) {
                log("update() position=$adapterPosition, task stays the same")
                //return
            } else {
                log("update() position=$adapterPosition, task become $newTask")
                focusTitleEditText()
            }

            this.task = newTask
            if (task != null) {
                itemView.etTitle.setText(task!!.task.title)
                itemView.etDescription.setText(task!!.task.description)
                itemView.layoutLabels.selectedLabels = task!!.labels
            } else {
                // New task
                itemView.etTitle.setText("")
                itemView.etDescription.visibility = View.VISIBLE
                itemView.etDescription.setText("")
                itemView.layoutLabels.selectedLabels = null
            }

        }

        /**
         * Cancel the modifications made during edition
         */
        fun cancel() {
            log("cancel()")
            unFocusEditText()
            taskViewHolderObserver.cancelTaskEdition(this)
        }

        /**
         * Cancel the modifications made during edition
         */
        fun save() {
            log("save()")
            unFocusEditText()
            taskViewHolderObserver.saveTaskEdition(
                this,
                itemView.etTitle.text.toString(),
                itemView.etDescription.text.toString(),
                itemView.layoutLabels.selectedLabels ?: ArrayList()
            )
        }

        /**
         * Remove the task
         */
        fun remove() {
            log("remove().")
            unFocusEditText()
            taskViewHolderObserver.removeTask(this)
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
}