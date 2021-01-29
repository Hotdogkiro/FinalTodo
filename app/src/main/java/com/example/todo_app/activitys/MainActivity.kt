package com.example.todo_app.activitys

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
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
import com.example.todo_app.model.Status
import com.example.todo_app.model.Task
import com.orm.SugarContext
import com.orm.SugarRecord
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.*
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


interface ISelectedData {
    fun onSelectedData(string: String)
}

class MainActivity : AppCompatActivity(),ISelectedData  {
    lateinit var allTasks: MutableList<Task>
    var currentStatus: Status = Status.TODO
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var databaseReader: DatabaseReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication_page)
        SugarContext.init(this)
        databaseReader = DatabaseReader(this)
        //setup(applicationContext, this)
        setupWithoutBiometric()
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

    private fun setupRecyclerView(adapter: TaskAdapter) {
        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun refreshData(){
        val currentTasks = databaseReader.readTasksWithStatus(currentStatus)
        val adapter = TaskAdapter(currentTasks, this)
        setupRecyclerView(adapter)
    }

    private fun authenticationSuccessful(){
        setContentView(R.layout.activity_main)
        refreshData()
        actionsToMenu()
        actionToFAB()
    }

    //Login

    private fun showNewTaskDialog() {
        val fm: FragmentManager = supportFragmentManager
        val newTaskDialog = NewTaskDialog()
        newTaskDialog.show(fm, "new_task_dialog")
    }

    private fun loadTasks(): MutableList<Task> {
        val tasks: List<Task> = SugarRecord.listAll(Task::class.java)
        return tasks.toMutableList()
    }

    private fun setupWithoutBiometric(){
        authenticationSuccessful()
    }

    private fun setup(context: Context, fragment: FragmentActivity) {
        generateSecretKey(
            KeyGenParameterSpec.Builder(
                "saveKey",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true)
                .build()
        )

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

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()


        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        biometricPrompt.authenticate(
            promptInfo,
            BiometricPrompt.CryptoObject(cipher)
        )
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey("saveKey", null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}