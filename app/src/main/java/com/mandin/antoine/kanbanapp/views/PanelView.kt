package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.dao.Service
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.utils.Constants
import kotlinx.android.synthetic.main.view_panel.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.log

class PanelView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs), TaskAdapter.ModificationSaver,
    TaskTouchHelperCallback.OnStartDragListener {
    private lateinit var adapter: TaskAdapter
    private val service = Service(context)
    private val index: Int
    var panelManager: PanelManager? = null

    private fun log(str:String){
        Log.i("PanelView",str)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_panel, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PanelView,
            0, 0
        ).apply {
            try {
                title = getString(R.styleable.PanelView_title) ?: ""
                index = getInt(R.styleable.PanelView_index, -1)
            } finally {
                recycle()
            }
        }

        btnAdd.setOnClickListener {
            displayNewTask()
        }
    }

    var title: String = ""
        set(value) {
            field = value
            tvTitle.text = title
        }

    private var itemTouchHelper: ItemTouchHelper? = null

    var tasks: ArrayList<TaskWithLabels> = ArrayList()
        set(value) {
            field = ArrayList(value)
            adapter = TaskAdapter(field, this, this)
            val callback: ItemTouchHelper.Callback = TaskTouchHelperCallback(adapter, index)
            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper!!.attachToRecyclerView(recyclerView)
            recyclerView.adapter = adapter
        }

    override fun saveTasksUpdate(vararg task: Task) {
        service.updateTasks(*task)
    }


    override fun saveTaskChanges(task: TaskWithLabels) {
        service.updateTaskWithLabels(task)
    }

    override fun createNewTask(title: String, description: String, labels: List<Label>, priority: Int): TaskWithLabels {
        val taskToAdd = Task(
            title = title,
            description = description,
            panel = index,
            priority = priority
        )

        return service.insertTaskWithLabels(taskToAdd, labels)
    }

    override fun deleteTask(task: TaskWithLabels) {
        service.deleteTaskWithLabels(task)
    }

    override fun moveTaskRight(task: TaskWithLabels): Boolean {
        if (index == Constants.Panels.DONE)
            return false

        panelManager?.let {
            it.moveTaskToPanel(task, index + 1)
            return true
        }
        return false
    }

    override fun moveTaskLeft(task: TaskWithLabels): Boolean {
        if (index == Constants.Panels.LIST)
            return false

        panelManager?.let {
            it.moveTaskToPanel(task, index - 1)
            return true
        }
        return false
    }

    fun displayNewTask(){
        adapter.addNewTask()
        recyclerView.layoutManager?.scrollToPosition(0)
    }

    fun insertOnTop(task: TaskWithLabels) {
        task.task.panel = index
        adapter.insertOnTop(task)
        service.updateTaskWithLabels(task)
    }

    interface PanelManager {
        fun moveTaskToPanel(task: TaskWithLabels, panelIndex: Int)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        log("onStartDrag (position=${viewHolder.adapterPosition})")
        itemTouchHelper?.startDrag(viewHolder)
    }

    override fun onStartSwipe(viewHolder: RecyclerView.ViewHolder) {
        log("onStartSwipe (position=${viewHolder.adapterPosition})")
        itemTouchHelper?.startSwipe(viewHolder)
    }
}