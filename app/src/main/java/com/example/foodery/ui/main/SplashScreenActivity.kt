package com.example.foodery.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.foodery.R
import com.example.foodery.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    var TAG = "SplashScreenActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        Log.d(TAG, "Animation Called")
//        with(lottieAnimView) {
//            setMinAndMaxFrame(30, 60)
//        }

        Log.d(TAG, "isAnmitaing: " + lottieAnimView.isAnimating)

//        startActivity(Intent(this, MainActivity::class.java))

    }

}