package com.mandin.antoine.kanbanapp.views

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.utils.Constants

class TaskTouchHelperCallback(
    private val adapter: TaskAdapter,
    private val panelIndex: Int
) : ItemTouchHelper.Callback() {

    private fun log(str: String) {
        Log.i("TaskTouchHelperCallback", str)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = when (panelIndex) {
            Constants.Panels.LIST -> ItemTouchHelper.END
            Constants.Panels.DONE -> ItemTouchHelper.START
            else -> ItemTouchHelper.END or ItemTouchHelper.START

        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        log("onMove")
        if (viewHolder is ItemTouchHelperViewHolder)
            viewHolder.onItemClear()
        adapter.onItemMove(viewHolder as TaskViewHolder, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        log("onSwiped")
        if (viewHolder is ItemTouchHelperViewHolder)
            viewHolder.onItemClear()
        adapter.onItemSwiped(viewHolder as TaskViewHolder, direction)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null && viewHolder is ItemTouchHelperViewHolder)
            viewHolder.onItemSelected()
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is ItemTouchHelperViewHolder)
            viewHolder.onItemClear()
    }

    interface TaskTouchHelperAdapter {
        fun onItemMove(viewHolder: TaskViewHolder, toPosition: Int): Boolean

        fun onItemDismiss(viewHolder: TaskViewHolder)

        fun onItemSwiped(viewHolder: TaskViewHolder, direction: Int)
    }

    interface ItemTouchHelperViewHolder {
        fun onItemSelected()

        fun onItemClear()
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
        fun onStartSwipe(viewHolder: RecyclerView.ViewHolder)
    }
}