package com.mandin.antoine.kanbanapp.views

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import kotlinx.android.synthetic.main.view_task.view.*

class TaskAdapter
    (private val tasks: List<TaskWithLabels>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    init {
        tasks.sortedBy {
            it.task.order
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var task:TaskWithLabels

        fun update(newTask: TaskWithLabels){
            this.task = newTask
            itemView.etTitle.setText(task.task.title)
            if(task.task.description.isNotBlank()) {
                itemView.etDescription.visibility = VISIBLE
                itemView.etDescription.setText(task.task.description)
            } else {
                itemView.etDescription.visibility = GONE
            }

            // TODO labels

            // TODO edit + button "save changes"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.update(tasks[position])
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}