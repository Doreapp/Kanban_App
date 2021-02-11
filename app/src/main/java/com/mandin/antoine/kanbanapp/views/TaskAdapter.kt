package com.mandin.antoine.kanbanapp.views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import kotlinx.android.synthetic.main.view_task.view.*

/**
 * Adapter displaying task views
 */
class TaskAdapter
    (
    private val tasks: List<TaskWithLabels>,
    private val modificationSaver: ModificationSaver
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    /**
     * Current editing item index in list [tasks], -1 if none
     */
    private var editingItem = -1

    /**
     * Is the current in-editing item ([editingItem]) new one ?
     * Must [editingItem] = 0 if true
     */
    private var newOne = false

    /**
     * Implementation on listener of task editing end
     */
    val onTaskEditEndListener = object : OnTaskEditEndListener {

        /**
         * Callback for cancellation of task edit
         */
        override fun onTaskEditCanceled(task: TaskWithLabels?) {
            log("onTaskEditCanceled (task=$task, editingItem=$editingItem, newOne=$newOne)")
            val oldEditingItem = editingItem
            editingItem = -1
            if (newOne) {
                newOne = false
                notifyItemRemoved(oldEditingItem)
            } else {
                notifyItemChanged(oldEditingItem)
            }
        }

        /**
         * Callback for validation of task edit
         */
        override fun onTaskEditSaved(
            task: TaskWithLabels?, title: String, description: String,
            labels: List<Label>
        ) {
            log("onTaskEditSaved (task=$task, editingItem=$editingItem, newOne=$newOne, title=$title, description=$description, labels=$labels)")
            if (task == null) {
                //Creating
                    val priority = if (tasks.isEmpty())  0 else  tasks[0].task.priority + 1
                val task = modificationSaver.createNewTask(title, description, labels, priority)
                (tasks as ArrayList).add(0, task)

                newOne = false
                editingItem = -1
                notifyItemChanged(0)
            } else {
                task.task.title = title
                task.task.description = description
                task.labels = labels
                modificationSaver.saveTaskChanges(task)

                val oldEditingItem = editingItem
                editingItem = -1
                notifyItemChanged(oldEditingItem)
            }
        }

    }

    private fun log(str: String) {
        Log.i("TaskAdapter", str)
    }

    init {
        tasks.sortedBy {
            -it.task.priority
        }
    }

    class TaskViewHolder(
        itemView: View,
        val onTaskEditEndListener: OnTaskEditEndListener
    ) : RecyclerView.ViewHolder(itemView) {
        private var task: TaskWithLabels? = null
        var editing = false
            set(value) {
                field = value
                itemView.etDescription.isEnabled = value
                itemView.etTitle.isEnabled = value
                if (value) {
                    itemView.etDescription.visibility = VISIBLE
                    itemView.layoutButtons.visibility = VISIBLE
                } else {
                    itemView.layoutButtons.visibility = GONE
                }
            }

        init {
            itemView.btnCancel.setOnClickListener {
                onTaskEditEndListener.onTaskEditCanceled(task)
            }
            itemView.btnSave.setOnClickListener {
                onTaskEditEndListener.onTaskEditSaved(
                    task,
                    itemView.etTitle.text.toString(),
                    itemView.etDescription.text.toString(),
                    ArrayList() // TODO labels here
                )
            }
        }

        fun update(newTask: TaskWithLabels?) {
            this.task = newTask
            if (task != null) {
                itemView.etTitle.setText(task!!.task.title)
                if (task!!.task.description.isNotBlank()) {
                    itemView.etDescription.visibility = VISIBLE
                    itemView.etDescription.setText(task!!.task.description)
                } else {
                    itemView.etDescription.visibility = GONE
                }
            } else {
                // New one
                itemView.etTitle.setText("")
                itemView.etDescription.visibility = VISIBLE
                itemView.etDescription.setText("")
                editing = true
            }

            // TODO labels
        }

    }

    fun addNewTask() {
        newOne = true
        editingItem = 0
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_task, parent, false)
        return TaskViewHolder(itemView, onTaskEditEndListener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (newOne && position == 0) {
            holder.update(null)
        } else {
            holder.update(tasks[position])
            holder.editing = position == editingItem
        }
        holder.itemView.btnEdit.setOnClickListener {
            if (position != editingItem && !newOne && editingItem < 0) {
                editingItem = position
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size + if (newOne) 1 else 0
    }

    /**
     * Listener of task edit end
     */
    interface OnTaskEditEndListener {
        fun onTaskEditCanceled(task: TaskWithLabels?)
        fun onTaskEditSaved(task: TaskWithLabels?, title: String, description: String, labels: List<Label>)
    }

    interface ModificationSaver {
        fun saveTaskChanges(task: TaskWithLabels)
        fun createNewTask(title: String, description: String, labels: List<Label>, priority: Int = 0): TaskWithLabels
    }

}