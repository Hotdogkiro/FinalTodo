package com.example.todo_app.activitys

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo_app.model.Importance
import com.example.todo_app.model.Status
import com.example.todo_app.model.Task
import com.orm.SugarContext


class EditTaskDialog : DialogFragment() {
    private lateinit var mCallback: ISelectedData
    private var taskId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        SugarContext.init(this.context)
        // Get task id from arguments to get Task from the database
        taskId = arguments?.getLong("id", -1)
        return inflater.inflate(R.layout.edit_task_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view)
    }

    override fun onResume() {
        // Sets the height and the width of the DialogFragment
        val width = ConstraintLayout.LayoutParams.MATCH_PARENT
        val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        super.onResume()
    }

    private fun setup(view: View) {
        // Set values of the Edit Task Dialog
        val task = Task.findById(Task::class.java, taskId)
        view.findViewById<TextView>(R.id.titleTask).text = task.title
        view.findViewById<TextView>(R.id.descriptionTask).text = task.description
        when (task.importance){
            Importance.LOW -> view.findViewById<RadioGroup>(R.id.priorityTask).check(R.id.radioButtonLow)
            Importance.MEDIUM -> view.findViewById<RadioGroup>(R.id.priorityTask).check(R.id.radioButtonMedium)
            Importance.HIGH -> view.findViewById<RadioGroup>(R.id.priorityTask).check(R.id.radioButtonHigh)
        }
        when (task.status){
            Status.TODO -> view.findViewById<RadioGroup>(R.id.statusTask).check(R.id.radioButtonTodo)
            Status.INPROGRESS -> view.findViewById<RadioGroup>(R.id.statusTask).check(R.id.radioButtonProgress)
            Status.DONE -> view.findViewById<RadioGroup>(R.id.statusTask).check(R.id.radioButtonDone)
        }
        val save = view.findViewById<View>(R.id.saveButton)
        save.setOnClickListener {
            checkForMissingInputs(view,task)
        }
    }

    private fun checkForMissingInputs(view : View, task: Task){
        if(view.findViewById<TextView>(R.id.titleTask).text.isEmpty()){
            displayError()
        }else{
            saveNewTask(view, task)
        }
    }
    private fun saveNewTask(view: View, task: Task) {
        task.importance = enumValueOf(view.findViewById<RadioButton>(view.findViewById<RadioGroup>(R.id.priorityTask).checkedRadioButtonId).text.toString())
        task.description = view.findViewById<TextView>(R.id.descriptionTask).text.toString()
        task.title = view.findViewById<TextView>(R.id.titleTask).text.toString()
        task.status = enumValueOf(view.findViewById<RadioButton>(view.findViewById<RadioGroup>(R.id.statusTask).checkedRadioButtonId).text.toString().replace("\\s".toRegex(), ""))
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