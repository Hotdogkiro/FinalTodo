package com.example.todo_app.activitys

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo_app.model.Task
import com.google.android.material.snackbar.Snackbar


class TaskAdapter(private val tasksInCurrentList:  MutableList<Task>, val mainActivity: MainActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val description = itemView.findViewById<TextView>(R.id.description)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View
        var context = parent.context
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.task_fragment, parent, false)
        return ViewHolder(view)
    }

    fun deleteItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
        val mRecentlyDeletedItem = tasksInCurrentList.get(position)
        val mRecentlyDeletedItemPosition = position
        tasksInCurrentList.removeAt(position)
        notifyItemRemoved(position)
        showUndoSnackbar(mRecentlyDeletedItem, mRecentlyDeletedItemPosition)
    }

    private fun showUndoSnackbar(mRecentlyDeletedItem : Task, mRecentlyDeletedItemPosition : Int) {
        val view: View =  mainActivity.findViewById(R.id.coordinatorLayout)
        val snackbar: Snackbar = Snackbar.make(
            view, R.string.snack_bar_text,
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(R.string.snack_bar_text) { v -> undoDelete(mRecentlyDeletedItem,mRecentlyDeletedItemPosition) }
        snackbar.show()
    }

    private fun undoDelete(mRecentlyDeletedItem : Task, mRecentlyDeletedItemPosition : Int) {
        tasksInCurrentList.add(
            mRecentlyDeletedItemPosition,
            mRecentlyDeletedItem
        )
        notifyItemInserted(mRecentlyDeletedItemPosition)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val task: Task = tasksInCurrentList[position] as Task
        val titleView = holder.title
        val description = holder.description
        titleView.text = task.title
        description.text = task.description
    }


    override fun getItemCount(): Int {
        return tasksInCurrentList.size
    }
}

class SwipeToDeleteCallback(adapter: TaskAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val mAdapter: TaskAdapter = adapter
    private val background: ColorDrawable = ColorDrawable(Color.BLACK)
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // used for up and down movements
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        mAdapter.deleteItem(position, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset =
            20 //so background is behind the rounded corners of itemView
        val iconTop =
        if (dX > 0) { // Swiping to the right
            background.color = Color.GREEN
            background.setBounds(
                itemView.left, itemView.top,
                itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
            )
        } else if (dX < 0) { // Swiping to the left
            background.color = Color.RED
            background.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset,
                itemView.top, itemView.right, itemView.bottom
            )
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
    }
}