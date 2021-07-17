package com.maxgol.favdish.view.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.maxgol.favdish.databinding.ActivitySplashBinding
import android.os.Handler
import com.maxgol.favdish.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        }

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        splashBinding.tvAppName.animation = splashAnimation

        splashAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
    }
}