package com.mandin.antoine.kanbanapp.views.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.views.view_holders.*

/**
 * Adapter displaying task views.
 *
 * TODO introduce tow View Type and separate te view holders.
 */
class TaskAdapter
    (
    private val tasks: ArrayList<TaskWithLabels>,
    private val modificationSaver: ModificationSaver,
    private val startDragListener: TaskTouchHelperCallback.OnStartDragListener,
    private val allLabels: List<Label>
) :
    RecyclerView.Adapter<TaskViewHolder>(),
    TaskTouchHelperCallback.TaskTouchHelperAdapter,
    TaskViewHolderObserver {
    /**
     * Current editing item index in list [tasks], -1 if none
     */
    private var editingItem = -1

    /**
     * Is the current in-editing item ([editingItem]) new one ?
     * Must [editingItem] = 0 if true
     */
    private var newOne = false

    private fun log(str: String) {
        Log.i("TaskAdapter", str)
    }

    init {
        tasks.sortBy {
            -it.task.priority
        }
    }

    fun addNewTask() {
        log("addNewTask")
        if (newOne)
            return

        newOne = true
        editingItem = 0
        notifyItemInserted(0)
    }

    fun insertOnTop(task: TaskWithLabels) {
        log("insertOnTop (task=$task)")

        val priority = if (tasks.isEmpty()) 0 else (tasks[0].task.priority + 1)
        task.task.priority = priority
        tasks.add(0, task)
        if (editingItem >= 0 && !newOne) {
            // We are editing an item already persisted. The task will be inserted before.
            editingItem += 1
        }
        notifyItemInserted(if (newOne) 1 else 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return when (viewType) {
            VIEW_TYPE_EDIT -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_edit_task, parent, false)
                TaskViewHolder.EditViewHolder(itemView, this, allLabels)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_display_task, parent, false)
                TaskViewHolder.DisplayViewHolder(itemView, this, allLabels)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (newOne && position == 0) {
            holder.update(null)
        } else {
            holder.update(tasks[position + if (newOne) -1 else 0])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == editingItem) VIEW_TYPE_EDIT else VIEW_TYPE_DISPLAY
    }

    override fun getItemCount(): Int {
        return tasks.size + if (newOne) 1 else 0
    }

    override fun onItemMove(
        viewHolder: TaskViewHolder.DisplayViewHolder,
        toPosition: Int
    ): Boolean {
        log("onItemMove viewHolder=$viewHolder, toPosition=$toPosition")
        if (editingItem >= 0) {
            log("onItemMove, try to move items while editing : abort")
            return false
        }
        val fromPosition = viewHolder.adapterPosition
        val movedItem = viewHolder.task!!
        val toItem = tasks[toPosition]
        movedItem.task.priority = toItem.task.priority
        val changedTasks: Array<Task>

        log("onItemMove, before list edit: tasks=$tasks")
        if (fromPosition < toPosition) {
            for (i in fromPosition + 1..toPosition) {
                tasks[i].task.priority++
            }
            tasks.removeAt(fromPosition)
            tasks.add(toPosition, movedItem)
            changedTasks = Array(toPosition - fromPosition + 1) {
                tasks[it + fromPosition].task
            }
        } else {
            for (i in toPosition until fromPosition) {
                tasks[i].task.priority--
            }
            tasks.removeAt(fromPosition)
            tasks.add(toPosition, movedItem)
            changedTasks = Array(fromPosition - toPosition + 1) {
                tasks[it + toPosition].task
            }
        }
        log("onItemMove, after list edit: tasks=$tasks")

        notifyItemMoved(fromPosition, toPosition)

        modificationSaver.savePrioritiesChanges(changedTasks)

        return true
    }

    override fun onItemDismiss(viewHolder: TaskViewHolder.DisplayViewHolder) {
        log("onItemDismiss (viewHolder=$viewHolder)")
        viewHolder.onItemClear()
    }

    override fun onItemSwiped(viewHolder: TaskViewHolder.DisplayViewHolder, direction: Int){
        log("onItemSwiped viewHolder=$viewHolder, direction=$direction")
        when (direction) {
            ItemTouchHelper.END -> moveTaskRight(viewHolder)
            ItemTouchHelper.START -> moveTaskLeft(viewHolder)
        }
    }

    override fun startSwipingTask(taskViewHolder: TaskViewHolder.DisplayViewHolder) {
        if (editingItem >= 0) {
            log("startSwipingTask, try to move items while editing : abort")
            return
        }
        log("startSwipingTask taskViewHolder=$taskViewHolder")
        taskViewHolder.onItemSelected()
        startDragListener.onStartSwipe(taskViewHolder)
    }

    override fun startReorderingTask(taskViewHolder: TaskViewHolder.DisplayViewHolder) {
        log("startReorderingTask taskViewHolder=$taskViewHolder")
        taskViewHolder.onItemSelected()
        startDragListener.onStartDrag(taskViewHolder)
    }

    override fun startEditingTask(taskViewHolder: TaskViewHolder.DisplayViewHolder) {
        log("startEditingTask taskViewHolder=$taskViewHolder")
        val position = taskViewHolder.adapterPosition
        if (editingItem != -1) {
            log("startEditingTask : try to edit a 2nd task, abort")
            return
        }
        editingItem = position
        notifyItemChanged(position)
    }

    /**
     * Callback for cancellation of task edit
     */
    override fun cancelTaskEdition(taskViewHolder: TaskViewHolder.EditViewHolder) {
        log("onTaskEditCanceled (taskViewHolder=$taskViewHolder, editingItem=$editingItem, newOne=$newOne)")
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
    override fun saveTaskEdition(
        taskViewHolder: TaskViewHolder.EditViewHolder,
        title: String, description: String,
        labels: List<Label>
    ) {
        log("onTaskEditSaved (taskViewHolder=$taskViewHolder, editingItem=$editingItem, newOne=$newOne, title=$title, description=$description, labels=$labels)")
        if (newOne) {
            //Creating
            val priority = if (tasks.isEmpty()) 0 else tasks[0].task.priority + 1
            val task = modificationSaver.createNewTask(title, description, labels, priority)
            tasks.add(0, task)

            newOne = false
            editingItem = -1
            notifyItemChanged(0)
        } else {
            val task = taskViewHolder.task!!
            task.task.title = title
            task.task.description = description
            task.labels = labels
            modificationSaver.saveTaskChanges(task)

            val oldEditingItem = editingItem
            editingItem = -1
            notifyItemChanged(oldEditingItem)
        }
    }

    override fun removeTask(taskViewHolder: TaskViewHolder.EditViewHolder) {
        log("onTaskRemoved task=$taskViewHolder")
        taskViewHolder.task?.let {
            modificationSaver.deleteTask(it)
            tasks.remove(it)
            val oldPosition = editingItem
            editingItem = -1
            notifyItemRemoved(oldPosition)
        }
    }

    override fun moveTaskRight(taskViewHolder: TaskViewHolder.DisplayViewHolder) {
        log("onTaskMoveRight taskViewHolder=$taskViewHolder")
        taskViewHolder.task?.let {
            if (modificationSaver.moveTaskRight(it)) {
                tasks.remove(it)
                notifyItemRemoved(taskViewHolder.adapterPosition)
            }
        }
    }

    override fun moveTaskLeft(taskViewHolder: TaskViewHolder.DisplayViewHolder) {
        log("onTaskMoveLeft task=$taskViewHolder")
        taskViewHolder.task?.let {
            if (modificationSaver.moveTaskLeft(it)) {
                tasks.remove(it)
                notifyItemRemoved(taskViewHolder.adapterPosition)
            }
        }
    }

    interface ModificationSaver {
        fun savePrioritiesChanges(tasks: Array<Task>)
        fun saveTaskChanges(task: TaskWithLabels)
        fun createNewTask(title: String, description: String, labels: List<Label>, priority: Int = 0): TaskWithLabels
        fun deleteTask(task: TaskWithLabels)
        fun moveTaskRight(task: TaskWithLabels): Boolean
        fun moveTaskLeft(task: TaskWithLabels): Boolean
    }

    companion object {
        const val VIEW_TYPE_DISPLAY = 0
        const val VIEW_TYPE_EDIT = 1
    }
}