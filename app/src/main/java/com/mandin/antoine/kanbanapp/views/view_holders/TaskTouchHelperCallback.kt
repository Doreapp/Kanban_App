package com.mandin.antoine.kanbanapp.views.view_holders

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.kanbanapp.utils.Constants
import com.mandin.antoine.kanbanapp.views.adapters.TaskAdapter

class TaskTouchHelperCallback(
    private val adapter: TaskAdapter,
    private val panelIndex: Int
) : ItemTouchHelper.Callback() {

    private fun log(str: String) {
        Log.i("TaskTouchHelperCallba1", str)
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
            //Constants.Panels.DONE -> ItemTouchHelper.START
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
        return adapter.onItemMove(viewHolder as TaskViewHolder.DisplayViewHolder, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        log("onSwiped")
        if (viewHolder is ItemTouchHelperViewHolder)
            viewHolder.onItemClear()
        adapter.onItemSwiped(viewHolder as TaskViewHolder.DisplayViewHolder, direction)
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
        fun onItemMove(viewHolder: TaskViewHolder.DisplayViewHolder, toPosition: Int): Boolean

        fun onItemDismiss(viewHolder: TaskViewHolder.DisplayViewHolder)

        fun onItemSwiped(viewHolder: TaskViewHolder.DisplayViewHolder, direction: Int)
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