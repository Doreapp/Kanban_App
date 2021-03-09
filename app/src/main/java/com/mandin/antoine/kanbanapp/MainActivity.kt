package com.mandin.antoine.kanbanapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mandin.antoine.kanbanapp.dao.Service
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.model.Task
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.utils.Constants
import com.mandin.antoine.kanbanapp.views.PanelView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_manage_labels.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

// TODO retrieve labels when come back from mangeLabelsActivity, and update the display
/**
 * Main Activity : used to manage tasks, into [PanelView]s
 *
 * TODO improvements : Info bar, showing what to do. Cancel possibility.
 */
class MainActivity : AppCompatActivity(), PanelView.PanelManager {
    lateinit var service: Service

    private fun log(str: String) {
        Log.i("MainActivity", str)
    }

    private fun e(str: String) {
        Log.e("MainActivity", str, Throwable())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log("onCreate")

        service = Service(this)

        //Hide the keyboard usually opening on app begin
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        fetchAndDisplayData()

        initToolBar()
    }

    override fun onDestroy() {
        log("onDestroy")
        panelList.onDestroy()
        panelToDo.onDestroy()
        panelDoing.onDestroy()
        panelDone.onDestroy()
        service.close()
        super.onDestroy()
    }

    /**
     * Called when an item of the toolbar is pressed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        log("onOptionsItemSelected item=$item")
        when(item.itemId){
            R.id.item_manage_labels -> {
                // open ManageLabelsActivity
                manageLabels()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Inflate the buttons on the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        log("onCreateOptionsMenu menu=$menu")
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log("onActivityResult requestCode=$requestCode, resultCode=$requestCode, data=$data")
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_MANAGE_LABELS -> {
                fetchAndDisplayData()
            }
        }
    }

    private fun fetchAndDisplayData(){
        Executors.newSingleThreadExecutor().submit {
            runBlocking {
                val tasks = loadTasks()
                val labels = loadLabels()
                runOnUiThread { displayTasks(tasks, labels) }
            }
        }

    }

    /**
     * Init the toolbar, and the back arrow on it
     */
    private fun initToolBar(){
        log("initToolBar")
        setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true);
        //supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    private fun testSetOfTasks(): List<TaskWithLabels> {
        val result = ArrayList<TaskWithLabels>()
        result.add(
            TaskWithLabels(
                Task(
                    0,
                    "Task1",
                    "descriptu ofrebf urufb jifhbrhe ibrfh eg gthy t erhgiehfry brufhy gfurzhfyu rehfrhbey gfhrufh yugfugr ti",
                    0,
                    0
                ),
                ArrayList()
            )
        )
        result.add(
            TaskWithLabels(
                Task(1, "Task2", "", 0, 2),
                ArrayList()
            )
        )
        result.add(
            TaskWithLabels(
                Task(2, "Task3", "descriptu ofrebf urufb jifhbrhe ibrfh egti", 2, 0),
                ArrayList()
            )
        )
        return result
    }

    private fun testSetOfLabels(): List<Label> {
        val result = ArrayList<Label>()
        result.add(
            Label(0, "Label 1", 0xffe57373.toInt())
        )
        result.add(
            Label(0, "Home", 0xfff06292.toInt())
        )
        result.add(
            Label(0, "Priority High", 0xff909090.toInt())
        )
        result.add(
            Label(0, "Un truc un peu plus long pour voir quoi", 0xffffb74d.toInt())
        )
        return result
    }

    /**
     * Open [ManageLabelsActivity] to mange labels
     */
    fun manageLabels(){
        log("manageLabels")
        val intent = Intent(this, ManageLabelsActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_MANAGE_LABELS)
    }

    /**
     * Load [Task]s from the storage
     */
    suspend fun loadTasks(): List<TaskWithLabels> {
        log("loadTasks")
        return service.getAllTasksWithLabels()
    }

    /**
     * Load [Label] from the storage
     */
    suspend fun loadLabels(): List<Label> {
        log("loadLabels")
        return ArrayList(service.getAllLabels())
    }

    /**
     * Display the [tasks]
     */
    fun displayTasks(tasks: List<TaskWithLabels>, allLabels: List<Label>) {
        log("displayTasks tasksCount=${tasks.size}, allLabelsCount=${allLabels.size}")
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

        panelList.setValues(panelTasks[Constants.Panels.LIST]!!, allLabels)
        panelToDo.setValues(panelTasks[Constants.Panels.TODO]!!, allLabels)
        panelDoing.setValues(panelTasks[Constants.Panels.DOING]!!, allLabels)
        panelDone.setValues(panelTasks[Constants.Panels.DONE]!!, allLabels)
        panelList.panelManager = this
        panelToDo.panelManager = this
        panelDoing.panelManager = this
        panelDone.panelManager = this
    }

    override fun moveTaskToPanel(task: TaskWithLabels, panelIndex: Int) {
        log("moveTaskToPanel task=$task, panelIndex=$panelIndex")
        when (panelIndex) {
            Constants.Panels.LIST -> panelList.insertOnTop(task)
            Constants.Panels.TODO -> panelToDo.insertOnTop(task)
            Constants.Panels.DOING -> panelDoing.insertOnTop(task)
            Constants.Panels.DONE -> panelDone.insertOnTop(task)
        }
    }

    companion object {
        const val REQUEST_CODE_MANAGE_LABELS = 1001
    }
}