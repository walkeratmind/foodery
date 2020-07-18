package com.example.foodery.ui.main.view.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodery.R
import com.example.foodery.databinding.ActivityMainBinding
import com.example.foodery.utils.NetworkUtils
import com.example.foodery.utils.getColorRes
import com.example.foodery.utils.hide
import com.example.foodery.utils.show
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        handleNetworkChanges()

        mBinding.bottomNavigationView.setupWithNavController(mainNavHostFragment.findNavController())
    }

    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(
            this,
            Observer { isConnected ->
                if (!isConnected) {
                    mBinding.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                    mBinding.networkStatusLayout.apply {
//                        setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                        setBackgroundResource(R.drawable.primary_gradient_bg)

                        alpha = 0f
                        animate()
                            .alpha(1f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setStartDelay(ANIMATION_DURATION)
                            .setDuration(ANIMATION_DURATION)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(animation: Animator?) {
                                    show()
                                }
                            })
                    }
                } else {
                    mBinding.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                    mBinding.networkStatusLayout.apply {
//                        setBackgroundColor(getColorRes(R.color.colorStatusConnected))
                        setBackgroundResource(R.drawable.primary_gradient_success)
                        alpha = 1f
                        animate()
                            .alpha(0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setStartDelay(ANIMATION_DURATION)
                            .setDuration(ANIMATION_DURATION)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    hide()
                                }
                            })

                    }
                }
            }
        )
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    companion object {
        const val ANIMATION_DURATION = 1000.toLong()
    }

}