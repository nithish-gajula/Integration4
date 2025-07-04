package com.example.integration4

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.io.File

object ActivityUtils {

    private val contextTAG: String = "ActivityUtils"


    val avatars = intArrayOf(
        R.mipmap.avatar1,
        R.mipmap.avatar2,
        R.mipmap.avatar3,
        R.mipmap.avatar4,
        R.mipmap.avatar5,
        R.mipmap.avatar6,
        R.mipmap.avatar7,
        R.mipmap.avatar8,
        R.mipmap.avatar9,
        R.mipmap.avatar10,
        R.mipmap.avatar11,
        R.mipmap.avatar12,
    )

    fun navigateToActivity(activity: Activity, intent: Intent, message: String) {
        if (intent.component?.className == LoginActivity::class.java.name) {
            LOGGING.INFO(activity, contextTAG,
                "Deleting data files and navigating to LoginActivity : $message"
            )

            getUserDataFile(activity).delete()
            getUserExpensesFile(activity).delete()
            getRoomExpensesFile(activity).delete()
        }


        activity.startActivity(intent)
    }

    fun getReportedLogsFile(context: Context): File {
        return File(context.getExternalFilesDir(null), context.getString(R.string.reportedLogsFileName))
    }

    fun getReportedReadmeLogsFile(context: Context): File {
        return File(context.getExternalFilesDir(null), context.getString(R.string.reportedReadmeLogsFileName))
    }

    fun getUserDataFile(context: Context): File {
        return File(context.getExternalFilesDir(null), context.getString(R.string.userDataFileName))
    }

    fun getUserExpensesFile(context: Context): File {
        return File(context.getExternalFilesDir(null), context.getString(R.string.userExpensesFileName))
    }

    fun getRoomExpensesFile(context: Context): File {
        return File(context.getExternalFilesDir(null), context.getString(R.string.roomExpensesFileName))
    }

    fun showAboutDialog(activity: Activity) {
        val mView: View = activity.layoutInflater.inflate(R.layout.about, null)
        val tv = mView.findViewById<TextView>(R.id.app_version_id)
        val gmailIMG = mView.findViewById<ImageView>(R.id.gmail_img_id)
        val githubIMG = mView.findViewById<ImageView>(R.id.github_img_id)
        val instagramIMG = mView.findViewById<ImageView>(R.id.instagram_img_id)
        val linkedinIMG = mView.findViewById<ImageView>(R.id.linkedin_img_id)
        tv.text = activity.resources.getString(R.string.version)

        AlertDialog.Builder(activity)
            .setView(mView)
            .setCancelable(true)
            .show()

        gmailIMG.setOnClickListener {
            val uri = Uri.parse(activity.getString(R.string.email_info))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }

        githubIMG.setOnClickListener {
            val uri = Uri.parse(activity.getString(R.string.github_info))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }

        instagramIMG.setOnClickListener {
            val uri = Uri.parse(activity.getString(R.string.instagram_info))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }

        linkedinIMG.setOnClickListener {
            val uri = Uri.parse(activity.getString(R.string.linkedin_info))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }

    }

    fun relaunch(activity: Activity) {
        activity.finishAffinity()
        activity.startActivity(activity.intent)
    }


}