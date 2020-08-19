package com.example.sandboxlog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity created")

        button.setOnClickListener {
            throw NullPointerException()
        }

        buttonSend.setOnClickListener {
            Log.e(TAG, "BUTTON PRESSED")
            GlobalScope.launch {
                (application as SandboxApp).createUploadLogsTask()
                (application as SandboxApp).startLogging()
            }
        }

        buttonSecondActivity.setOnClickListener {
            Log.d(TAG, "Starting second activity")

            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}