package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.mandin.antoine.kanbanapp.R

/**
 * View allowing to choose a color into the array of color [R.array.labelColors]
 */
class ColorPicker(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    /**
     * Array of colors
     */
    private val colors: IntArray

    /**
     * Array of views, linkable to [colors].
     * The [ColorView] i display the color i in [colors].
     */
    private val colorViews: Array<ColorView>

    /**
     * Currently picked color, referenced by index.
     * When a color is picked, its view is scaled by 2 in width.
     */
    private var pickedColorIndex = -1

    var listener: OnColorPickedListener? = null

    /**
     * The currently picked color.
     * -1 if None
     */
    val pickedColor: Int
        get() {
            return if (pickedColorIndex >= 0) colors[pickedColorIndex] else -1
        }

    init {
        orientation = HORIZONTAL
        colors = context.resources.getIntArray(R.array.labelColors)
        colorViews = Array(colors.size) { i ->
            ColorView(context, colors[i])
        }
        for (v in colorViews) {
            addView(v)
            v.setOnClickListener {
                onPicked(it as ColorView)
            }
        }
        onPicked(colorViews[0])
    }

    /**
     * Select [color], if referenced into the array [colors]
     */
    fun selectColor(color: Int?) {
        if(color === null){
            onPicked(colorViews[0])
            return
        }
        val index = colors.indexOf(color)
        if (index >= 0) {
            onPicked(colorViews[index])
        }
    }

    /**
     * Called when a color is picked.
     * If may be by an user action (click on [ColorView]) or programmatically by [selectColor].
     */
    private fun onPicked(view: ColorView) {
        Log.i("ColorPicker", "onPicked : $view, listener=$listener")
        if (pickedColorIndex >= 0)
            colorViews[pickedColorIndex].unSelect()
        view.select()
        pickedColorIndex = colorViews.indexOf(view)
        listener?.onColorPicked(view.color)
        requestLayout()
    }

    /**
     * View class displaying a color
     */
    private class ColorView(
        context: Context,
        val color: Int
    ) : View(context) {
        init {
            Log.i("ColorView", "<init>")
            setBackgroundColor(color)
            val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
            lp.setMargins(1, 0, 1, 0)
            layoutParams = lp
        }

        /**
         * Select the view : scale it *2
         */
        fun select() {
            Log.i("ColorView", "select")
            (layoutParams as LinearLayout.LayoutParams).weight = 2f
        }

        /**
         * Un-Select the view : reset it scale to normal
         */
        fun unSelect() {
            Log.i("ColorView", "unSelect")
            (layoutParams as LinearLayout.LayoutParams).weight = 1f
        }

        override fun toString(): String {
            return "ColorPicker{color=$color}"
        }
    }

    /**
     * Interface notified of changes upon selected color
     */
    interface OnColorPickedListener {
        /**
         * Called when the selected color change, programmatically of from an user input
         */
        fun onColorPicked(color: Int)
    }
}