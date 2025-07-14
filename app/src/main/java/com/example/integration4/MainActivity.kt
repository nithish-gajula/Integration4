package com.example.integration4

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Calendar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var joinRoomBTN: Button
    private lateinit var createRoomBTN: Button
    private lateinit var resultTV: TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var animationView: LottieAnimationView
    private lateinit var alertDialog: AlertDialog
    private lateinit var customOverflowIcon: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var userprofileImageView: ShapeableImageView
    private lateinit var userFullNameTV: TextView
    private lateinit var userEmailTV: TextView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var latestProfileImage: Int = 1
    private lateinit var userFullName: String
    private lateinit var userEmail: String
    private val contextTAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalAccess.loadUserData(this)

        joinRoomBTN = findViewById(R.id.join_room_btn_id)
        createRoomBTN = findViewById(R.id.create_room_btn_id)
        resultTV = findViewById(R.id.result_tv_id)
        requestQueue = Volley.newRequestQueue(applicationContext)

        drawerLayout = findViewById(R.id.main_drawer_layout)
        navigationView = findViewById(R.id.main_nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Display application icon in the toolbar
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.app_icon_48)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.itemIconTintList = null
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black)

        // Find the custom overflow icon ImageView
        customOverflowIcon = toolbar.findViewById(R.id.custom_overflow_icon)
        customOverflowIcon.setOnClickListener { openCustomMenu() }

        val testing = findViewById<Button>(R.id.testing_btn_id)
        testing.setOnClickListener { startActivity(Intent(this, TestingActivity::class.java)) }

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.loading_box, null)
        animationView = dialogView.findViewById(R.id.lottie_animation)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)

        ActivityUtils.getReportedLogsFile(this).createNewFile()
        ActivityUtils.getReportedReadmeLogsFile(this).createNewFile()

        joinRoomBTN.setOnClickListener {
            resultTV.visibility = View.INVISIBLE
            joinRoomDialog()
        }

        createRoomBTN.setOnClickListener {
            resultTV.visibility = View.INVISIBLE
            createRoomFunction()
        }

        if (GlobalAccess.navigateToLoginActivity) {
            ActivityUtils.navigateToActivity(this, Intent(this, LoginActivity::class.java), "MainActivity Received navigateToLoginActivity = true from GlobalAccess Object")
        } else if (!GlobalAccess.isRoomLengthLessThanOne) {
            ActivityUtils.navigateToActivity(this, Intent(this, RoomActivity::class.java), "MainActivity Received isRoomLengthLessThanOne = false from GlobalAccess Object")
        } else {
            val headerView = navigationView.getHeaderView(0)
            userprofileImageView = headerView.findViewById(R.id.user_profile_pic)
            userFullNameTV = headerView.findViewById(R.id.user_full_name)
            userEmailTV = headerView.findViewById(R.id.user_email)
            latestProfileImage = GlobalAccess.profileId.toInt()
            userprofileImageView.setImageResource(ActivityUtils.avatars[GlobalAccess.profileId.toInt() - 1])
            userFullName = GlobalAccess.userName
            userEmail = GlobalAccess.email
            userFullNameTV.text = userFullName
            userEmailTV.text = userEmail
        }

        navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_profile) {
                ActivityUtils.navigateToActivity(this, Intent(this, EditDetailsActivity::class.java), "MainActivity received nav-profile action from user")
            } else if (item.itemId == R.id.nav_view_logs) {
                ActivityUtils.navigateToActivity(this, Intent(this, TestingActivity::class.java), "MainActivity received nav-view_logs action from user")
            } else if (item.itemId == R.id.nav_report) {
                ActivityUtils.navigateToActivity(this, Intent(this, ContactUsActivity::class.java), "MainActivity received nav-report action from user")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

    }

    private fun openCustomMenu() {
        val popupMenu = PopupMenu(this, customOverflowIcon)
        popupMenu.inflate(R.menu.toolbar_menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                R.id.menu_restart -> {
                    ActivityUtils.restart(this)
                    true
                }

                R.id.menu_about -> {
                    ActivityUtils.showAboutDialog(this)
                    true
                }

                R.id.menu_logout -> {
                    // Create a confirmation dialog
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout") { _, _ ->
                            // If the user confirms, proceed with logout
                            LOGGING.ERROR(this, contextTAG, "User Logged out from Menu")
                            ActivityUtils.navigateToActivity(this, Intent(this, LoginActivity::class.java), "MainActivity Received menu-logout action from user")
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            // If the user cancels, dismiss the dialog
                            dialog.dismiss()
                        }
                        .show()

                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun joinRoomDialog() {

        val builderR = AlertDialog.Builder(this)
        val inflaterR = layoutInflater
        val viewR = inflaterR.inflate(R.layout.join_room_dialog_layout, null)
        val roomId = viewR.findViewById<EditText>(R.id.roomId_et_id)
        val cancelBTN = viewR.findViewById<Button>(R.id.cancel_btn_id)
        val joinBTN = viewR.findViewById<Button>(R.id.join_room_btn_id)
        val roomIdTIL = viewR.findViewById<TextInputLayout>(R.id.roomId_til_id)
        roomIdTIL.setStartIconTintList(null)
        builderR.setView(viewR)
        val alertDialogR = builderR.create()

        joinBTN.setOnClickListener {
            val enteredText = roomId.text.toString()
            if (enteredText.isEmpty()) {
                roomId.error = getString(R.string.roomId_should_not_be_empty)
            } else {
                alertDialogR.dismiss()
                resultTV.visibility = View.VISIBLE
                resultTV.text = getString(R.string.you_entered, enteredText)
                joinRoomFunction(enteredText)
            }

        }

        cancelBTN.setOnClickListener {
            alertDialogR.dismiss()
        }
        alertDialogR.show()
    }

    private fun joinRoomFunction(roomID: String) {

        animationView.setAnimation(R.raw.profile_loading)
        animationView.playAnimation()
        alertDialog.show()
        val stringRequest = object : StringRequest(
            Method.POST, getString(R.string.spreadsheet_url),
            { response ->
                LOGGING.INFO(this, contextTAG, "Join Room, Got response = $response")
                extractRoomJoiningJsonData(response, roomID)
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
            },
            { error ->
                LOGGING.DEBUG(this, contextTAG, " Join Room, Error = $error")
                animationView.setAnimation(R.raw.error)
                animationView.playAnimation()
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
                resultTV.visibility = View.VISIBLE
                resultTV.text = error.toString()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "action" to "joinRoom",
                    "roomId" to roomID,
                    "userId" to GlobalAccess.userId
                )
            }
        }
        val socketTimeOut = 50000
        val policy = DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = policy
        requestQueue.add(stringRequest)
    }

    private fun extractRoomJoiningJsonData(jsonResponse: String, roomID: String) {

        try {
            val jsonObj = JSONObject(jsonResponse)
            val jsonArray = jsonObj.getJSONArray("items")

            if (jsonArray.length() > 0) {

                val roomIdStatus: String
                val result: String
                val jsonItem = jsonArray.getJSONObject(0)
                roomIdStatus = jsonItem.getBoolean("roomID_status").toString()
                result = jsonItem.getString("result").toString()

                when {
                    result.toBoolean() -> {
                        animationView.setAnimation(R.raw.protected_shield)
                        animationView.playAnimation()
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        LOGGING.INFO(this, contextTAG, "Room Joining Success RoomID = $roomID")
                        storeRoomId(roomID)
                        Handler(Looper.getMainLooper()).postDelayed({
                            ActivityUtils.navigateToActivity(
                                this,
                                Intent(this, RoomActivity::class.java), "MainActivity received room joining success from database"
                            )
                        }, 2000)
                    }

                    !roomIdStatus.toBoolean() -> {
                        LOGGING.DEBUG(this,
                            contextTAG,
                            "Room Joining Failed with Invalid RoomID = ${getString(R.string.invalid_roomId)}"
                        )
                        animationView.setAnimation(R.raw.error)
                        animationView.playAnimation()
                        Toast.makeText(this, "Invalid Room ID", Toast.LENGTH_SHORT).show()
                        resultTV.visibility = View.VISIBLE
                        resultTV.text = getString(R.string.invalid_roomId)

                    }

                    else -> {
                        LOGGING.DEBUG(this,
                            contextTAG,
                            " Room Joining Failed, Reason - ${getString(R.string.something_went_wrong)}"
                        )
                        animationView.setAnimation(R.raw.error)
                        animationView.playAnimation()
                        resultTV.visibility = View.VISIBLE
                        resultTV.text = getString(R.string.something_went_wrong)
                    }
                }

            } else {
                LOGGING.DEBUG(this,
                    contextTAG,
                    " Room Joining Failed, Reason - ${getString(R.string.no_data_found)}"
                )
                animationView.setAnimation(R.raw.error)
                animationView.playAnimation()
                resultTV.visibility = View.VISIBLE
                resultTV.text = getString(R.string.no_data_found)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun createRoomFunction() {

        animationView.setAnimation(R.raw.love_is_blind)
        animationView.playAnimation()
        alertDialog.show()

        val stringRequest = object : StringRequest(
            Method.POST, getString(R.string.spreadsheet_url),
            { response ->
                LOGGING.INFO(this, contextTAG, "Create Room, Got response = $response")
                extractRoomCreationJsonData(response)
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
            },
            { error ->
                LOGGING.DEBUG(this, contextTAG, "Create Room, Got Error = $error")
                animationView.setAnimation(R.raw.error)
                animationView.playAnimation()
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
                resultTV.visibility = View.VISIBLE
                resultTV.text = error.toString()

            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "action" to "createRoom",
                    "userId" to GlobalAccess.userId,
                    "roomId" to createRoomId(),
                )
            }
        }
        val socketTimeOut = 50000
        val policy = DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = policy
        requestQueue.add(stringRequest)
    }

    private fun createRoomId(): String {
        val calendar = Calendar.getInstance()

        // Extract date components
        val year = calendar.get(Calendar.YEAR) % 100 // Take last two digits
        val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based, so add 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val random = generateRandomString()

        val finalRoomId = "$minute$year${random[0]}$hour$month${random[1]}$day$second${random[2]}"
        LOGGING.INFO(this, contextTAG, "Created Room ID = $finalRoomId")

        return finalRoomId
    }

    private fun generateRandomString(): String {
        val alphabet = getString(R.string.alphabets)
        val random = Random(System.currentTimeMillis())

        return (1..3)
            .map { alphabet[random.nextInt(0, alphabet.length)] }
            .joinToString("")
    }

    private fun extractRoomCreationJsonData(jsonResponse: String) {
        val userIdStatus: String
        val createRoomStatus: String
        val roomID: String
        val adminStatus: String

        try {
            val jsonObj = JSONObject(jsonResponse)
            val jsonArray = jsonObj.getJSONArray("items")

            if (jsonArray.length() > 0) {

                val jsonItem = jsonArray.getJSONObject(0)
                userIdStatus = jsonItem.getBoolean("userID_status").toString()
                createRoomStatus = jsonItem.getBoolean("result").toString()
                roomID = jsonItem.getString("roomID").toString()
                adminStatus = jsonItem.getBoolean("adminStatus").toString()

                when {
                    createRoomStatus.toBoolean() -> {
                        LOGGING.INFO(this, contextTAG, "Room Creation Success in Google Spreadsheets")
                        animationView.setAnimation(R.raw.done)
                        animationView.playAnimation()
                        storeRoomIdAndAdminStatus(roomID, adminStatus)
                        Toast.makeText(this, "Room Created", Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            ActivityUtils.navigateToActivity(
                                this,
                                Intent(this, RoomActivity::class.java), "MainActivity received room creation success from user"
                            )
                        }, 2000)
                    }

                    !userIdStatus.toBoolean() -> {
                        LOGGING.DEBUG(this,
                            contextTAG,
                            "Room creation failed, Reason - ${getString(R.string.user_id_not_found)}"
                        )
                        animationView.setAnimation(R.raw.error)
                        animationView.playAnimation()
                        resultTV.visibility = View.VISIBLE
                        resultTV.text = getString(R.string.user_id_not_found)
                    }

                    else -> {
                        LOGGING.DEBUG(this,
                            contextTAG,
                            "Room creation failed, Reason - ${getString(R.string.something_went_wrong)}"
                        )
                        animationView.setAnimation(R.raw.error)
                        animationView.playAnimation()
                        resultTV.visibility = View.VISIBLE
                        resultTV.text = getString(R.string.something_went_wrong)
                    }
                }

            } else {
                LOGGING.DEBUG(this,
                    contextTAG,
                    "Room Creation failed, Reason - ${getString(R.string.no_data_found)}"
                )
                animationView.setAnimation(R.raw.error)
                animationView.playAnimation()
                resultTV.visibility = View.VISIBLE
                resultTV.text = getString(R.string.no_data_found)
            }
        } catch (e: JSONException) {
            LOGGING.DEBUG(this, contextTAG, "JSONException ${e.message}")
            e.printStackTrace()
        }
    }

    private fun storeRoomIdAndAdminStatus(roomID: String, adminStatus: String) {
        try {

            val content = ActivityUtils.getUserDataFile(this).readText()
            val userData = JSONObject(content)

            userData.put("roomId", roomID)
            userData.put("adminStatus", adminStatus)

            ActivityUtils.getUserDataFile(this).writeText(userData.toString())

        } catch (e: IOException) {
            LOGGING.DEBUG(this, contextTAG, "Storing roomId and AdminStatus failed, ${e.message}")
            e.printStackTrace()
        }
    }

    private fun storeRoomId(roomID: String) {
        try {

            val content = ActivityUtils.getUserDataFile(this).readText()
            val userData = JSONObject(content)

            userData.put("roomId", roomID)

            ActivityUtils.getUserDataFile(this).writeText(userData.toString())

        } catch (e: IOException) {
            LOGGING.DEBUG(this, contextTAG, "Storing room id failed, ${e.message}")
            e.printStackTrace()
        }
    }
}