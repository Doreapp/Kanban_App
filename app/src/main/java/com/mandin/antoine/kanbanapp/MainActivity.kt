package com.mandin.antoine.kanbanapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mandin.antoine.kanbanapp.dao.Service
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private fun log(str: String) {
        Log.i("MainActivity", str)
    }

    private fun e(str: String) {
        Log.e("MainActivity", str, Throwable())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Service(this).clear()

        val tasks = loadTasks()
        displayTasks(tasks)
    }

    private fun testSetOfTasks(): List<TaskWithLabels> {
        val result = ArrayList<TaskWithLabels>()
        result.add(
            TaskWithLabels(
                Task(0, "Task1","descriptu ofrebf urufb jifhbrhe ibrfh eg gthy t erhgiehfry brufhy gfurzhfyu rehfrhbey gfhrufh yugfugr ti",0, 0),
                ArrayList()
            )
        )
        result.add(
            TaskWithLabels(
                Task(1, "Task2","",0, 2),
                ArrayList()
            )
        )
        result.add(
            TaskWithLabels(
                Task(2, "Task3","descriptu ofrebf urufb jifhbrhe ibrfh egti",2, 0),
                ArrayList()
            )
        )
        return result
    }

    fun loadTasks(): List<TaskWithLabels> {
        return Service(this).getAllTasksWithLabels()
    }

    fun displayTasks(tasks: List<TaskWithLabels>) {
        val panelTasks = HashMap<Int, ArrayList<TaskWithLabels>>()
        for (panel in Constants.Panels.EVERY_PANELS)
            panelTasks[panel] = ArrayList()

        for (task in tasks) {
            val panel = panelTasks[task.task.panel]
            if (panel == null)
                e("Panel is not in the list (${task.task.panel}). Task=$task")
            else
                panel.add(task)
        }

        panelList.tasks = panelTasks[Constants.Panels.LIST]!!
        panelToDo.tasks = panelTasks[Constants.Panels.TODO]!!
        panelDoing.tasks = panelTasks[Constants.Panels.DOING]!!
        panelDone.tasks = panelTasks[Constants.Panels.DONE]!!
    }
}