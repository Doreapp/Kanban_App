package com.mandin.antoine.kanbanapp

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService
import com.mandin.antoine.kanbanapp.utils.Constants
import com.mandin.antoine.kanbanapp.views.PanelRemoteViewFactory


class PanelWidgetService : RemoteViewsService() {
    private fun log(str: String) {
        if (Constants.DEBUG)
            Log.i("PanelWidgetService", str)
    }

    @Override
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        log("onGetViewFactory")
        return PanelRemoteViewFactory(this.applicationContext)
    }
}
