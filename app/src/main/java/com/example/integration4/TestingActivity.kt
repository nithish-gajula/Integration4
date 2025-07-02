package com.example.integration4

import ActivityUtils
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.imcloudfloating.markdown.MarkdownIt

class TestingActivity : AppCompatActivity() {

    private lateinit var markdownView: MarkdownIt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        markdownView = findViewById(R.id.markdown_view)
        markdownView.markdownString = loadFileFromInternalStorage()

    }

    private fun loadFileFromInternalStorage(): String {
        val file = ActivityUtils.reportedReadmeLogsFile
        return file.readText()
    }
}
