package com.mandin.antoine.kanbanapp.views

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.mandin.antoine.kanbanapp.MainActivity
import com.mandin.antoine.kanbanapp.PanelWidgetService
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.utils.Constants


class PanelAppWidgetProvider : AppWidgetProvider() {

    private fun log(str: String) {
        if (Constants.DEBUG)
            Log.i("PanelAppWidgetProvider", str)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        log("onReceive")
        if (intent != null && context != null) {
            if (intent.action.equals(OPEN_ACTIVITY)) {
                val openIntent = Intent(context, MainActivity::class.java)
                context.startActivity(openIntent)
            }
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        log("onUpdate")
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            val intent = Intent(context, PanelWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val rv = RemoteViews(context.packageName, R.layout.widget_panel)
            rv.setRemoteAdapter(appWidgetId, R.id.listView, intent)
            rv.setEmptyView(R.id.listView, R.id.emptyView)


            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            /* val clickIntent = Intent(context, MainActivity::class.java)
             val clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0)
             rv.setPendingIntentTemplate(R.id.itemLayout, clickPendingIntent)

             val openAppIntent = Intent(context, PanelAppWidgetProvider::class.java)
             openAppIntent.action = OPEN_ACTIVITY
             openAppIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
             intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
             val openAppPendingIntent = PendingIntent.getBroadcast(
                 context, 0, openAppIntent,
                 PendingIntent.FLAG_UPDATE_CURRENT
             )
             rv.setPendingIntentTemplate(R.id.itemLayout, openAppPendingIntent)*/

            // Create an Intent to launch MainActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { launcherIntent ->
                    PendingIntent.getActivity(context, 0, launcherIntent, 0)
                }

            rv.setOnClickPendingIntent(R.id.mainLayout, pendingIntent)

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        const val OPEN_ACTIVITY = "open_activity"
    }
}
