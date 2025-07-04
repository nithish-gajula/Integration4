package com.example.integration4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.imcloudfloating.markdown.MarkdownIt
import java.io.File

class TestingActivity : AppCompatActivity() {

    private lateinit var markdownView: MarkdownIt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        markdownView = findViewById(R.id.markdown_view)
        markdownView.markdownString = ActivityUtils.getReportedReadmeLogsFile(this).readText()

    }

}
