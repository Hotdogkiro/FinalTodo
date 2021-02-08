package com.example.todo_app.activitys

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo_app.model.Importance
import com.example.todo_app.model.Task
import com.orm.SugarContext


class TaskAdapter(private val tasksInCurrentList:  MutableList<Task>, val mainActivity: MainActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.title)
        val description = itemView.findViewById<TextView>(R.id.description)
        val item = itemView.findViewById<View>(R.id.swipableFragment)
        init{
            itemView.setOnClickListener {
                val fm: FragmentManager = mainActivity.supportFragmentManager
                val editTaskDialog = EditTaskDialog()
                val b = Bundle()
                //passing arguments to fragment
                b.putLong("id", getId(layoutPosition))
                editTaskDialog.arguments = b
                editTaskDialog.show(fm, "edit_task_dialog")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View
        var context = parent.context
        val inflater = LayoutInflater.from(context)
        SugarContext.init(context)
        view = inflater.inflate(R.layout.task_fragment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val task: Task = tasksInCurrentList[position]
        val titleView = holder.title
        val description = holder.description
        val background = holder.item
        titleView.text = task.title
        description.text = task.description
        when (task.importance) {
            // alternate design
            /*Importance.LOW -> background.setBackgroundResource(R.drawable.stripesgreensmall)
            Importance.MEDIUM -> background.setBackgroundResource(R.drawable.stripesorangesmall)
            Importance.HIGH -> background.setBackgroundResource(R.drawable.stripesredsmall)*/
            Importance.LOW -> background.setBackgroundColor(Color.GREEN)
            Importance.MEDIUM -> background.setBackgroundColor(Color.YELLOW)
            Importance.HIGH -> background.setBackgroundColor(Color.RED)
        }
        background.background.alpha = 100
    }

    override fun getItemCount(): Int {
        return tasksInCurrentList.size
    }

    fun removeItem(position: Int) {
        tasksInCurrentList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getData(): MutableList<Task> {
        return tasksInCurrentList
    }

    fun getId(position: Int): Long {
        return tasksInCurrentList[position].id
    }
}



