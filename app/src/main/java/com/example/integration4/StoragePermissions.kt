package com.example.integration4

import ActivityUtils
import LOGGING
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException

class StoragePermissions : AppCompatActivity() {

    private val contextTAG = "StoragePermissions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage_permissions)

        setupFilesAndDirectories()
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupFilesAndDirectories() {
        val directory = File(
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            ActivityUtils.directoryName
        )

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.d(contextTAG, "Directory created: ${directory.absolutePath}")
            } else {
                Log.e(contextTAG, "Failed to create directory: ${directory.absolutePath}")
                Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            Log.d(contextTAG, "Directory already exists: ${directory.absolutePath}")
        }

        // Create required files
        createFileIfNotExists(File(directory, ActivityUtils.reportedLogsFileName))
        createFileIfNotExists(File(directory, ActivityUtils.reportedReadmeLogsFileName))
        createFileIfNotExists(File(directory, ActivityUtils.userDataFileName))
    }

    private fun createFileIfNotExists(file: File) {
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Log.i(contextTAG, "File created: ${file.absolutePath}")
                    LOGGING.INFO(contextTAG, "File created: ${file.absolutePath}")
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