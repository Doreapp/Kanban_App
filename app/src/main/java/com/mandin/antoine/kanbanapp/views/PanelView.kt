package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import kotlinx.android.synthetic.main.view_panel.view.*

class PanelView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private lateinit var adapter : TaskAdapter

    init {
        LayoutInflater.from(context).inflate(R.layout.view_panel, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PanelView,
            0, 0).apply {
            try {
                title = getString(R.styleable.PanelView_title)?:""
            } finally {
                recycle()
            }
        }

    }

    var title: String = ""
        set(value) {
            field = value
            tvTitle.text = title
        }

    var tasks: List<TaskWithLabels> = ArrayList()
        set(value){
            field = value
            adapter = TaskAdapter(tasks)
            recyclerView.adapter = adapter
        }

}