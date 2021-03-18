package com.mandin.antoine.kanbanapp.views

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.mandin.antoine.kanbanapp.R
import com.mandin.antoine.kanbanapp.model.Label
import com.mandin.antoine.kanbanapp.utils.Constants
import com.mandin.antoine.kanbanapp.utils.Utils

/**
 * View for a single label
 */
class LabelView(context: Context, attributeSet: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(context, attributeSet),
    View.OnClickListener {

    constructor(context: Context) : this(context, null)

    var label: Label? = null
        set(value) {
            field = value
            if (value === null) {
                Utils.changeBackgroundColor(this, Color.TRANSPARENT)
                text = ""
            } else {
                Utils.changeBackgroundColor(this, label!!.color)
                text = label!!.title
            }
        }


    var onLabelSelectionChange: OnLabelSelectionChange? = null


    override fun setSelected(selected: Boolean) {
        if (label === null) {
            super.setSelected(false)
            return
        }
        Utils.changeBackgroundColor(this, if (selected) label!!.color else Color.LTGRAY)
        super.setSelected(selected)
    }


    private fun log(str: String) {
        if (Constants.DEBUG)
            Log.i("LabelView", str)
    }

    init {
        log("<init>")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(R.style.AppTheme_Label)
        } else {
            setTextAppearance(context, R.style.AppTheme_Label)
        }

        setBackgroundResource(R.drawable.background_label)

        setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        log("onClick")
        val beforeSelected = isSelected
        isSelected = !isSelected
        if (beforeSelected != isSelected && label !== null)
            onLabelSelectionChange?.onLabelSelectionChange(label!!, isSelected)
    }

    interface OnLabelSelectionChange {
        fun onLabelSelectionChange(label: Label, selected: Boolean)
    }
}