package com.example.sandboxlog

import android.os.Bundle
import android.util.Log

class SecondActivity : MainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        Log.d("SecondActivity", "SecondActivity created")
    }
}
