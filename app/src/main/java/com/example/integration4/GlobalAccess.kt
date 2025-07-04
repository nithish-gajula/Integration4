package com.example.integration4

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GlobalAccess {

    var isUserAddedNewData: Boolean = false
    var isRoomLengthLessThanOne: Boolean = false
    var navigateToLoginActivity: Boolean = false

    lateinit var userId: String
    lateinit var userName: String
    lateinit var age: String
    lateinit var email: String
    lateinit var phoneNumber: String
    lateinit var loginTime: String
    lateinit var roomId: String
    lateinit var adminStatus: String
    lateinit var profileId: String
    private val contextTAG: String = "GlobalAccess"

    fun convertDateFormat(dateString: String): String {
        // Parsing the input date string
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date: Date = dateFormat.parse(dateString) ?: return ""

        // Formatting the date to "dd MMM yyyy" format
        val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return outputDateFormat.format(date)
    }

    fun loadUserData(context: Context) {

        Log.i(contextTAG, "entered loadUserData function")

        if (!ActivityUtils.getUserDataFile(context).exists()) {
            ActivityUtils.getUserDataFile(context).createNewFile()
        }

        try {

            Log.i(contextTAG, "entered loadUserData - try clause")

            val content = ActivityUtils.getUserDataFile(context).readText()
            val userData = JSONObject(content)

            Log.i(contextTAG, "entered loadUserData - userData : $userData")

            userId = userData.getString("id")
            userName = userData.getString("userName")
            age = userData.getString("age")
            email = userData.getString("email")
            phoneNumber = userData.getString("phoneNumber")
            loginTime = userData.getString("loginTime")
            roomId = userData.getString("roomId")
            adminStatus = userData.getString("adminStatus")
            profileId = userData.getString("profileId")

            Log.i(contextTAG, "entered loadUserData - userData values assigned to variables")

            val info = """
                userid : $userId
                userName :  $userName
                age :  $age
                email :  $email
                phoneNumber :  $phoneNumber
                loginTime :  $loginTime
                roomId :  $roomId
                adminStatus :  $adminStatus
                profileId :  $profileId
            """.trimIndent()

            LOGGING.INFO(context, contextTAG, "Application Launched , User Info found - $info")
            Log.i(contextTAG, "info = $info")

            if (roomId.length <= 1) {
                isRoomLengthLessThanOne = true
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            navigateToLoginActivity = true
        } catch (e: IOException) {
            e.printStackTrace()
            navigateToLoginActivity = true
        }
    }

}