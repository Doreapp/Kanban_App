package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection.ROW
import com.google.android.flexbox.FlexWrap.WRAP
import com.google.android.flexbox.FlexboxLayout
import com.mandin.antoine.kanbanapp.model.Label

/**
 * Layout display labels following one-another.
 * Must only contain [LabelView]
 */
class LabelLayout(context: Context, attrs: AttributeSet) :
    FlexboxLayout(context, attrs),
    LabelView.OnLabelSelectionChange {

    /**
     * Map of view by label
     */
    val views = HashMap<Label, LabelView>()

    /**
     * Every labels that may be displayed
     */
    var labels: List<Label>? = null
        set(value) {
            log("set labels -> $value")
            field = value
            removeAllViews()
            value?.let {
                val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                params.setMargins(4,2,4,2)
                for (label in value) {
                    val view = LabelView(context)
                    view.label = label
                    view.onLabelSelectionChange = this
                    views[label] = view
                    addView(view, params)
                }
                updateDisplay()
            }
        }


    /**
     * Currently selected labels.
     * Unselected labels are hidden if [showOnlySelectedLabels], otherwise their shown
     * as unselected.
     */
    var selectedLabels: List<Label>? = null
        set(value) {
            log("set selectedLabels -> $value")
            field = value
            updateDisplay()
        }

    /**
     * Do we only display selected labels
     */
    var editing = true
        set(value) {
            field = value
            updateDisplay()
        }

    private fun log(str: String) {
        Log.i("LabelLayout", str)
    }

    init {
        flexDirection = ROW
        flexWrap = WRAP
    }

    /**
     * Update the views displayed
     */
    fun updateDisplay() {
        log("updateDisplay")
        for ((label, view) in views.entries) {
            when {
                selectedLabels !== null && label in selectedLabels!! -> {
                    view.visibility = View.VISIBLE
                    view.isSelected = true
                }
                !editing -> {
                    view.visibility = View.GONE
                }
                else -> {
                    view.visibility = VISIBLE
                    view.isSelected = false
                }
            }
            view.isEnabled = editing
        }
    }

    override fun onLabelSelectionChange(label: Label, selected: Boolean) {
        log("onLabelSelectionChange label=$label, selected=$selected")
        if (selectedLabels === null)
            selectedLabels = ArrayList()

        if (selected)
            (selectedLabels as ArrayList).add(label)
        else
            (selectedLabels as ArrayList).remove(label)

        updateDisplay()
        //views[label]?.visibility = if (selected || editing) VISIBLE else GONE
    }
}