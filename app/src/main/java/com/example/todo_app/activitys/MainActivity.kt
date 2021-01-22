package com.example.todo_app.activitys

import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.model.RecyclerViewItem
import com.example.todo.model.Status
import com.example.todo.model.Task
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.*
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.coroutines.coroutineContext


class MainActivity : AppCompatActivity() {
    lateinit var allTasks: MutableList<RecyclerViewItem>
    var currentStatus: Status = Status.TODO
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication_page)
        setup(applicationContext, this)
    }

    private fun actionToFAB() {
        val FAB = findViewById<View>(R.id.floatingActionButton)
        FAB.setOnClickListener {
            showNewTaskDialog()
        }
    }

    private fun actionsToMenu() {
        val todo = findViewById<View>(R.id.todo) as Button
        val inProgress = findViewById<View>(R.id.inProgress) as Button
        val done = findViewById<View>(R.id.done) as Button
        val onClickListener = View.OnClickListener() {
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
            setupRecyclerView(TaskAdapter(filterTasksByStatus(currentStatus, allTasks), this, this))
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

    private fun filterTasksByStatus(
        currentStatus: Status,
        tasksToFilter: MutableList<RecyclerViewItem>
    ): MutableList<RecyclerViewItem> {
        val currentTasks: MutableList<RecyclerViewItem> = mutableListOf()
        tasksToFilter.forEach { recyclerViewItem ->
            recyclerViewItem as Task
            if (recyclerViewItem.status == currentStatus) {
                currentTasks.add(recyclerViewItem)
            }
        }
        return currentTasks
    }

    private fun authenticationSuccessful(){
        setContentView(R.layout.activity_main)
        allTasks = createTasks(20)
        val currentTasks = filterTasksByStatus(currentStatus, allTasks)
        val adapter = TaskAdapter(currentTasks, this, this)
        setupRecyclerView(adapter)
        actionsToMenu()
        actionToFAB()
    }

    private fun showNewTaskDialog() {
        val fm: FragmentManager = supportFragmentManager
        val newTaskDialog = NewTaskDialog()
        newTaskDialog.show(fm, "new_task_dialog")
    }

    fun createTasks(amount: Int): MutableList<RecyclerViewItem> {
        var list = mutableListOf<RecyclerViewItem>()
        for (i in 1..amount / 4) {
            list.add(Task(title = "Number$i ${Status.DONE}", status = Status.DONE))
            list.add(Task(title = "Number$i ${Status.INPROGRESS}", status = Status.INPROGRESS))
        }
        return list
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
                // Invalidate the keys if the user has registered a new biometric
                // credential, such as a new fingerprint. Can call this method only
                // on Android 7.0 (API level 24) or higher. The variable
                // "invalidatedByBiometricEnrollment" is true by default.
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