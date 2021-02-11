package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.dao.Service
import com.mandin.antoine.kanbanapp.dao.AppDatabase
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import kotlinx.android.synthetic.main.view_panel.view.*

class PanelView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs), TaskAdapter.ModificationSaver {
    private lateinit var adapter: TaskAdapter
    private var database: AppDatabase? = null
    private val service = Service(context)
    private val index: Int

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
            adapter.addNewTask()
        }
    }

    var title: String = ""
        set(value) {
            field = value
            tvTitle.text = title
        }

    var tasks: List<TaskWithLabels> = ArrayList()
        set(value) {
            field = ArrayList(value)
            adapter = TaskAdapter(field, this)
            recyclerView.adapter = adapter
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

}