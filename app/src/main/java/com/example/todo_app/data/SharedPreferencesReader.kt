package com.example.todo_app.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo_app.activitys.TaskAdapter
import com.example.todo_app.model.RecyclerViewItem
import com.example.todo_app.model.Status
import com.example.todo_app.model.Task
import javax.net.ssl.SSLEngineResult

class SharedPreferencesReader(private val activity: Activity){

    private fun  getSharedPreferences(sharedPreferencesName : String) : SharedPreferences{
        return activity?.getSharedPreferences(
            sharedPreferencesName, Context.MODE_PRIVATE)
    }

    public fun getAllTasksInDone() : MutableCollection<out Any?> {
        return getSharedPreferences("Done").all.values


    }

    public fun storeTask(task: Task){

    }
}