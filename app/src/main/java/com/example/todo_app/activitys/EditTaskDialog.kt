package com.example.todo_app.activitys

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.todo.R


class EditTaskDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_task_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get field from view
        // mEditText = view.findViewById<View>(R.id.) as EditText
        // Fetch arguments from bundle and set title
        // val title = arguments!!.getString("title", "Enter Name")
        // dialog!!.setTitle(title)
        // Show soft keyboard automatically and request focus to field
        // mEditText.requestFocus()
        // dialog!!.window!!.setSoftInputMode(
        //   WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        //)
    }


    private fun setup() {
        val lowString = SpannableString("LOW")
        val mediumString = SpannableString("MEDIUM")
        val highString = SpannableString("HIGH")
        lowString.setSpan(BackgroundColorSpan(Color.WHITE), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mediumString.setSpan(
            BackgroundColorSpan(Color.WHITE),
            0,
            10,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        highString.setSpan(
            BackgroundColorSpan(Color.WHITE),
            0,
            10,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }


}

