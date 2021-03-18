package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.dao.Service
import com.mandin.antoine.kanbanapp.model.TaskWithLabels
import com.mandin.antoine.kanbanapp.utils.Constants
import kotlinx.coroutines.runBlocking

internal class PanelRemoteViewFactory(
    private val context: Context
) :
    RemoteViewsService.RemoteViewsFactory {

    private val widgetItems: MutableList<TaskWithLabels> = ArrayList()

    private fun log(str: String) {
        if (Constants.DEBUG)
            Log.i("PanelRemoteViewFactory", str)
    }

    override fun onCreate() {
        log("onCreate")
        val service = Service(context)
        runBlocking {
            val tasks = service.getAllTasksWithLabels()
            widgetItems.clear()
            for (task in tasks) {
                if (task.task.panel == Constants.Panels.TODO)
                    widgetItems.add(task)
            }
            log("onCreate, tasks got (${widgetItems.size})")
        }
        log("onCreate end")
    }

    override fun onDestroy() {
        log("onDestroy")
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        widgetItems.clear()
    }

    override fun getCount(): Int {
        return widgetItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        log("get View at $position")
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        val rv = RemoteViews(context.packageName, R.layout.view_task_widget)
        rv.setTextViewText(R.id.tvTitle, widgetItems[position].task.title)
        if (widgetItems[position].task.description.isNotBlank()) {
            rv.setViewVisibility(R.id.tvDescription, View.VISIBLE)
            rv.setTextViewText(R.id.tvDescription, widgetItems[position].task.description)
        } else {
            rv.setViewVisibility(R.id.tvDescription, View.GONE)
        }

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        //val fillInIntent =  Intent()
        //rv.setOnClickFillInIntent(R.id.itemLayout, fillInIntent);

        return rv
    }

    override fun getLoadingView(): RemoteViews {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return RemoteViews(context.packageName, R.layout.view_task_widget)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
    }
}