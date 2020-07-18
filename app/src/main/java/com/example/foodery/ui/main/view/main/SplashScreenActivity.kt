package com.example.foodery.ui.main.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.foodery.R
import kotlinx.coroutines.*


class SplashScreenActivity : AppCompatActivity() {

    var TAG = "SplashScreenActivity"

    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Log.d(TAG, "Animation Called")
//        with(lottieAnimView) {
//            setMinAndMaxFrame(30, 60)
//        }

        activityScope.launch {
            delay(3000)

            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            finish()
        }


//        startActivity(Intent(this, MainActivity::class.java))

    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }

}