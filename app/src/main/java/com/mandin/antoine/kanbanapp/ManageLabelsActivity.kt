package com.mandin.antoine.kanbanapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.mandin.antoine.kanbanapp.dao.Service
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.views.adapters.LabelAdapter
import kotlinx.android.synthetic.main.activity_manage_labels.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

/**
 * Activity used to manage labels.
 */
class ManageLabelsActivity :
    AppCompatActivity(), LabelAdapter.ModificationSaver {
    lateinit var service: Service

    private var adapter: LabelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_labels)

        setResult(RESULT_OK)

        service = Service(this)

        Executors.newSingleThreadExecutor().submit {
            runBlocking {
                val labels = loadLabels()
                runOnUiThread { displayLabels(labels) }
            }
        }

        initToolBar()
    }

    /**
     * Called when an item of the toolbar is pressed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Back arrow pressed
                onBackPressed()
            }
            R.id.item_add_label -> {
                // Create a new label
                addLabel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Inflate the buttons on the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_mange_labels, menu)
        return true
    }

    /**
     * Init the toolbar, and the back arrow on it
     */
    private fun initToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    /**
     * Add a new label and start editing it
     */
    fun addLabel() {
        adapter?.addNewLabel()
    }

    /**
     * Load every labels
     */
    suspend fun loadLabels(): ArrayList<Label> {
        return ArrayList(service.getAllLabels())
    }

    /**
     * Displays [labels] using the recycler view and [LabelAdapter]
     */
    fun displayLabels(labels: ArrayList<Label>) {
        adapter = LabelAdapter(labels, this)
        recyclerView.adapter = adapter
    }

    override fun saveLabelChanges(label: Label) = runBlocking {
        service.updateLabel(label)
    }

    override fun createNewLabel(title: String, color: Int): Label = runBlocking {
        service.insertLabel(Label(title = title, color = color))
    }

    override fun deleteLabel(label: Label) = runBlocking {
        service.deleteLabel(label)
    }
}