package com.example.sandboxlog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

open class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val TAG = MainActivity::class.java.simpleName
    }
    private var exceptionFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity created")

        button.setOnClickListener {
            // creating an exception
            RequestBody.create(MultipartBody.FORM, exceptionFile!!)
        }

        buttonSend.setOnClickListener {
            (application as SandboxApp).uploadLogs()
        }

        buttonSecondActivity.setOnClickListener {
            Log.d(TAG, "Starting second activity")

            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}