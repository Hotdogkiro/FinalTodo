package com.example.todo_app.activitys

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo_app.data.DatabaseReader
import com.example.todo_app.model.Importance
import com.example.todo_app.model.Status
import com.example.todo_app.model.Task
import com.journaldev.androidrecyclerviewswipetodelete.SwipeToDeleteCallback
import com.orm.SugarContext
import kotlinx.android.synthetic.main.recycler_fragment.*
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executor

//Interface to get notification on Data changed
interface ISelectedData {
    fun onSelectedData(string: String)
}

class MainActivity : AppCompatActivity(), ISelectedData  {
    var currentStatus: Status = Status.TODO
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var databaseReader: DatabaseReader
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication_page)
        SugarContext.init(this)
        databaseReader = DatabaseReader(this)
        actionToSignInButton()
        //createTasks(20)
        setup(applicationContext, this)
        //setupWithoutBiometric()
    }

    override fun onSelectedData(returnValue: String) {
        refreshData()
    }

    private fun actionToFAB() {
        val FAB = findViewById<View>(R.id.floatingActionButton)
        FAB.setOnClickListener {
            showNewTaskDialog()
        }
    }

    private fun actionToSignInButton() {
        val signIn = findViewById<View>(R.id.signInButton)
        signIn.setOnClickListener {
            signIn()
        }
    }

    private fun actionsToMenu() {
        val todo = findViewById<View>(R.id.todo) as Button
        todo.setTextColor(Color.CYAN)
        val inProgress = findViewById<View>(R.id.inProgress) as Button
        val done = findViewById<View>(R.id.done) as Button
        val onClickListener = View.OnClickListener {
            it as Button
            if (it == todo) {
                currentStatus = Status.TODO
            }
            if (it == inProgress) {
                currentStatus = Status.INPROGRESS
            }
            if (it == done) {
                currentStatus = Status.DONE
            }
            todo.setTextColor(Color.WHITE)
            inProgress.setTextColor(Color.WHITE)
            done.setTextColor(Color.WHITE)
            it.paintFlags = it.paintFlags
            it.setTextColor(Color.CYAN)
            refreshData()
        }
        todo.setOnClickListener(onClickListener)
        inProgress.setOnClickListener(onClickListener)
        done.setOnClickListener(onClickListener)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        enableSwipeToDeleteAndUndo()
    }

    private fun refreshData(){
        val currentTasks = databaseReader.readTasksWithStatus(currentStatus)
        adapter = TaskAdapter(currentTasks, this)
        setupRecyclerView()
    }

    private fun authenticationSuccessful(){
        setContentView(R.layout.activity_main)
        refreshData()
        actionsToMenu()
        actionToFAB()
    }

    private fun showNewTaskDialog() {
        val fm: FragmentManager = supportFragmentManager
        val newTaskDialog = NewTaskDialog()
        newTaskDialog.show(fm, "new_task_dialog")
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val item: Task = adapter.getData()[position]
                adapter.removeItem(position)
                item.delete()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    //Login

    private fun setupWithoutBiometric(){
        authenticationSuccessful()
    }

    private fun setup(context: Context, fragment: FragmentActivity) {
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(
            fragment, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        context,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    val encryptedInfo: ByteArray? = result.cryptoObject?.cipher?.doFinal(
                        "Test".toByteArray(Charset.defaultCharset())
                    )
                    Log.d(
                        "MY_APP_TAG", "Encrypted information: " +
                                Arrays.toString(encryptedInfo)
                    )
                    authenticationSuccessful()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        signIn()
    }

    private fun signIn(){
        promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build()

        biometricPrompt.authenticate(promptInfo)
    }

    //Test Data

    private fun createTasks(amount: Int){
        createTasks(amount, Status.TODO)
        createTasks(amount, Status.INPROGRESS)
        createTasks(amount, Status.DONE)
    }

    private fun createTasks(amount : Int, status: Status){
        for(i in 1..amount){
            Task("This is Todo nr. $i","This Todo is for test purposes", Importance.LOW.toString(), status.toString()).save()
        }
    }
}