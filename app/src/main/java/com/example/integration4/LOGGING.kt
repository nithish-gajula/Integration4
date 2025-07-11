package com.example.integration4

import android.content.Context
import android.util.Log
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LOGGING {

    fun INFO(context: Context, tag: String, msg: String) {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 3) {
            val methodName = stackTrace[3].methodName
            val lineNumber = stackTrace[3].lineNumber
            val fileName = stackTrace[3].fileName
            val logContent =
                "\n[ ${getCurrentDateTime()} ] [INFO] [$fileName] [$methodName] [$lineNumber] \n $tag \n$msg \n------------------------------------------"
            val markdownContent =
                "\n[ *${getCurrentDateTime()}* ] [<font color='blue'>**INFO**</font>] [$fileName] [$methodName] [$lineNumber] \n" + "`$tag` \n" +
                        ">$msg \n *** \n"
            reportLog(context, logContent, markdownContent)
        }
        Log.i(tag, msg)
    }

    fun DEBUG(context: Context, tag: String, msg: String) {

        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 3) {
            val methodName = stackTrace[3].methodName
            val lineNumber = stackTrace[3].lineNumber
            val fileName = stackTrace[3].fileName
            val logContent =
                "\n[ ${getCurrentDateTime()} ] [DEBUG] [$fileName] [$methodName] [$lineNumber] \n $context \n$msg \n-----------------------------------------"
            val markdownContent =
                "\n[ *${getCurrentDateTime()}* ] [<font color='orange'>**DEBUG**</font>] [$fileName] [$methodName] [$lineNumber] \n" + "`$context` \n" +
                        ">$msg \n *** \n"
            reportLog(context, logContent, markdownContent)
        }
        Log.d(tag, msg)
    }

    fun ERROR(context: Context, tag: String, msg: String) {

        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 3) {
            val methodName = stackTrace[3].methodName
            val lineNumber = stackTrace[3].lineNumber
            val fileName = stackTrace[3].fileName
            val logContent =
                "\n[ ${getCurrentDateTime()} ] [ERROR] [$fileName] [$methodName] [$lineNumber] \n $context \n$msg \n-----------------------------------------"
            val markdownContent =
                "\n[ *${getCurrentDateTime()}* ] [<font color='red'>**ERROR**</font>] [$fileName] [$methodName] [$lineNumber] \n" + "`$context` \n" +
                        ">$msg \n *** \n"
            reportLog(context, logContent, markdownContent)
        }
        Log.e(tag, msg)
    }

    private fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss.SSS")
        return currentDateTime.format(formatter)
    }

    private fun reportLog(context: Context, log: String, markdown: String) {
        val reportedLogsFile = File(context.getExternalFilesDir(null), context.getString(R.string.reportedLogsFileName))
        val reportedReadmeLogsFile = File(context.getExternalFilesDir(null), context.getString(R.string.reportedReadmeLogsFileName))

        reportedLogsFile.appendText(log)
        reportedReadmeLogsFile.appendText(markdown)
    }

}