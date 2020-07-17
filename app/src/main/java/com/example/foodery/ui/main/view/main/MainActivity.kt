package com.example.foodery.ui.main.view.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodery.R
import com.example.foodery.databinding.ActivityMainBinding
import com.example.foodery.utils.NetworkUtils
import com.example.foodery.utils.getColorRes
import com.example.foodery.utils.hide
import com.example.foodery.utils.show
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
                        show()
                        setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                    }
                } else {
                    mBinding.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                    mBinding.networkStatusLayout.apply {
                        setBackgroundColor(getColorRes(R.color.colorStatusConnected))
                        animate()
                            .alpha(1f)
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

    companion object {
        const val ANIMATION_DURATION = 1000.toLong()
    }

}