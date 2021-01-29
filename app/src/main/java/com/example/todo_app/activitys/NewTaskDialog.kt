package com.example.todo_app.activitys

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo_app.model.Importance
import com.example.todo_app.model.Status
import com.example.todo_app.model.Task
import com.orm.SugarContext


class NewTaskDialog : DialogFragment() {
    private lateinit var mCallback: ISelectedData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (container != null) {
            SugarContext.init(this.context)
        }
        return inflater.inflate(R.layout.new_task_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view)
    }

    private fun setup(view: View) {
        val save = view.findViewById<View>(R.id.saveButton)
        save.setOnClickListener {
            checkForMissingInputs(view)
        }
    }

    private fun checkForMissingInputs(view : View){
        if(view.findViewById<TextView>(R.id.titleForNewTask).text.isEmpty()){
            displayError()
        }else{
            saveNewTask(view)
        }
    }
    private fun saveNewTask(view: View) {
        val importance : Importance = enumValueOf(view.findViewById<RadioButton>(view.findViewById<RadioGroup>(R.id.priorityForNewTask).checkedRadioButtonId).text.toString())
        val description : String = view.findViewById<TextView>(R.id.descriptionForNewTask).text.toString()
        val title : String = view.findViewById<TextView>(R.id.titleForNewTask).text.toString()
        val task = Task(title,description,importance.toString(), Status.TODO.toString())
        Log.d("test",title)
        task.save()
        mCallback.onSelectedData("save");
        this.dismiss()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mCallback = activity as ISelectedData
    }

    private fun displayError(){
        Toast.makeText(
                context, "Please enter a Title",
                Toast.LENGTH_SHORT
        ).show()
    }
}