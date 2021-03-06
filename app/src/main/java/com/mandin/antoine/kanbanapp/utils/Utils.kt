package com.mandin.antoine.kanbanapp.utils

import android.graphics.PorterDuff
import android.view.View

/**
 * Utils functions
 */
object Utils {

    /**
     * Change the color of the background drawable of the [view].
     * Set it to [color].
     */
    fun changeBackgroundColor(view: View, color:Int){
        view.background.setColorFilter(color, PorterDuff.Mode.SRC)
    }
}