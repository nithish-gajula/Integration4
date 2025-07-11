package com.example.integration4

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date

class SplashScreenActivity : AppCompatActivity() {

    private var contextTAG : String = "SplashScreenActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        GlobalAccess.loadUserData(this)

        setupFilesAndDirectories() // Call to set up files and directories
        ActivityUtils.navigateToActivity(this, Intent(this, MainActivity::class.java), "SplashScreenActivity navigated to MainActivity after checking Files")

        val lottieView = findViewById<LottieAnimationView>(R.id.lottieView)
        lottieView.playAnimation()

        lottieView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (GlobalAccess.navigateToLoginActivity) {
                    ActivityUtils.navigateToActivity(this@SplashScreenActivity, Intent(this@SplashScreenActivity, LoginActivity::class.java), "SplashScreenActivity Received navigateToLoginActivity = true from GlobalAccess Object")
                } else if (!GlobalAccess.isRoomLengthLessThanOne) {
                    ActivityUtils.navigateToActivity(this@SplashScreenActivity, Intent(this@SplashScreenActivity, RoomActivity::class.java),"SplashScreenActivity Received isRoomLengthLessThanOne = false from GlobalAccess Object")
                }

                finish()
            }
        })

    }

    private fun setupFilesAndDirectories() {

        // Create files if they don't exist
        createFileIfNotExists(ActivityUtils.getReportedLogsFile(this))
        createFileIfNotExists(ActivityUtils.getReportedReadmeLogsFile(this))
        createFileIfNotExists(ActivityUtils.getUserDataFile(this))


    }

    private fun createFileIfNotExists(file: File) {
        if (!file.exists()) {
            try {
                val fileCreated = file.createNewFile()
                if (fileCreated) {
                    LOGGING.INFO(this, contextTAG, "File created: ${file.absolutePath}")
                } else {
                    Log.e(contextTAG, "Failed to create file: ${file.absolutePath}")
                }
            } catch (e: IOException) {
                Log.e(contextTAG, "Error creating file: ${file.absolutePath}", e)
            }
        } else {
            Log.d(contextTAG, "File already exists: ${file.absolutePath}")
        }
    }
}