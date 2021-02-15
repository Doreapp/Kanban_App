package com.mandin.antoine.kanbanapp.views

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
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
    private val tasks: ArrayList<TaskWithLabels>,
    private val modificationSaver: ModificationSaver,
    private val startDragListener: TaskTouchHelperCallback.OnStartDragListener
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(),
    TaskTouchHelperCallback.ItemTouchHelperAdapter,
    TaskObserver {
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
            val priority = if (tasks.isEmpty()) 0 else tasks[0].task.priority + 1
            val task = modificationSaver.createNewTask(title, description, labels, priority)
            tasks.add(0, task)

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

    override fun onTaskRemoved(task: TaskWithLabels?) {
        log("onTaskRemoved task=$task")
        task?.let {
            modificationSaver.deleteTask(it)
        }
    }

    override fun onTaskMoveRight(task: TaskWithLabels) {
        log("onTaskMoveRight task=$task")
        if (modificationSaver.moveTaskRight(task)) {
            val index = tasks.indexOf(task)
            tasks.removeAt(index)
            notifyItemRemoved(
                index + if (newOne) 1 else 0
            )
        }
    }

    override fun onTaskMoveLeft(task: TaskWithLabels) {
        log("onTaskMoveLeft task=$task")
        if (modificationSaver.moveTaskLeft(task)) {
            val index = tasks.indexOf(task)
            tasks.removeAt(index)
            notifyItemRemoved(
                index + if (newOne) 1 else 0
            )
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


    fun addNewTask() {
        log("addNewTask")
        if(newOne)
            return

        newOne = true
        editingItem = 0
        notifyItemInserted(0)
    }

    fun insertOnTop(task: TaskWithLabels) {
        log("insertOnTop (task=$task)")
        if (editingItem >= 0) {
            if (newOne) {
                newOne = false
                editingItem = -1
                notifyItemRemoved(0)
            } else {
                val oldEditingItem = editingItem
                editingItem = -1
                notifyItemChanged(oldEditingItem)
            }
        }

        val priority = if (tasks.isEmpty()) 0 else tasks[0].task.priority + 1
        task.task.priority = priority
        tasks.add(0, task)
        notifyItemInserted(if (newOne) 1 else 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_task, parent, false)
        return TaskViewHolder(itemView, this)
    }

    @SuppressLint("ClickableViewAccessibility")
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
        holder.itemView.btnReorder.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    holder.onItemSelected()
                    startDragListener.onStartDrag(holder)
                }
            }
            false
        }
        holder.itemView.btnMoveHorizontally.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    holder.onItemSelected()
                    startDragListener.onStartSwipe(holder)
                }
            }
            false
        }
    }

    override fun getItemCount(): Int {
        return tasks.size + if (newOne) 1 else 0
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        log("onItemMove fromPosition=$fromPosition, toPosition=$toPosition)")
        if (fromPosition < tasks.size && toPosition < tasks.size) {
            val offset = if (newOne) -1 else 0
            val movedItem = tasks[fromPosition + offset]
            val toItem = tasks[toPosition + offset]
            movedItem.task.priority = toItem.task.priority

            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    tasks[i].task.priority++
                }
                tasks.removeAt(fromPosition + offset)
                tasks.add(toPosition - 1 + offset, movedItem)
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    tasks[i].task.priority--
                }
                tasks.removeAt(fromPosition + offset)
                tasks.add(toPosition + offset, movedItem)
            }

            notifyItemMoved(fromPosition, toPosition)
        }
        return true
    }

    override fun onItemDismiss(position: Int) {
        log("onItemDismiss (position=$position)")
        //TODO, in function of the direction (right of left), we may move the item
    }

    override fun onItemSwiped(position: Int, direction: Int) {
        log("onItemSwiped position=$position, direction=$direction")
        if (position == 0 && newOne) {
            // Try to move the editing new One
            editingItem = -1
            newOne = false
            notifyItemRemoved(0)
        } else {
            if (position == editingItem)
                editingItem = -1

            val offset = if (newOne) -1 else 0
            when (direction) {
                ItemTouchHelper.END -> onTaskMoveRight(tasks[position+offset])
                ItemTouchHelper.START -> onTaskMoveLeft(tasks[position+offset])
            }
        }
    }


    class TaskViewHolder(
        itemView: View,
        val taskObserver: TaskObserver
    ) : RecyclerView.ViewHolder(itemView),
        TaskTouchHelperCallback.ItemTouchHelperViewHolder {
        private var task: TaskWithLabels? = null
        var editing = false
            set(value) {
                field = value
                itemView.etDescription.isEnabled = value
                itemView.etTitle.isEnabled = value
                if (value) {
                    itemView.etDescription.visibility = VISIBLE
                    itemView.layoutButtons.visibility = VISIBLE
                    itemView.btnDelete.visibility = VISIBLE
                    itemView.btnEdit.visibility = GONE
                    itemView.btnMoveHorizontally.visibility = GONE
                } else {
                    itemView.layoutButtons.visibility = GONE
                    itemView.btnDelete.visibility = GONE
                    itemView.btnEdit.visibility = VISIBLE
                    itemView.btnMoveHorizontally.visibility = VISIBLE
                }
            }

        init {
            itemView.btnCancel.setOnClickListener {
                taskObserver.onTaskEditCanceled(task)
            }
            itemView.btnSave.setOnClickListener {
                taskObserver.onTaskEditSaved(
                    task,
                    itemView.etTitle.text.toString(),
                    itemView.etDescription.text.toString(),
                    ArrayList() // TODO labels here
                )
            }
            itemView.btnDelete.setOnClickListener {
                taskObserver.onTaskRemoved(task)
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

        private fun log(str: String) {
            Log.i("TaskViewHolder", str)
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
    }

    interface ModificationSaver {
        fun saveTaskChanges(task: TaskWithLabels)
        fun createNewTask(title: String, description: String, labels: List<Label>, priority: Int = 0): TaskWithLabels
        fun deleteTask(task: TaskWithLabels)
        fun moveTaskRight(task: TaskWithLabels): Boolean
        fun moveTaskLeft(task: TaskWithLabels): Boolean
    }
}