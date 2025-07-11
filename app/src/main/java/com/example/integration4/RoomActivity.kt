package com.example.integration4

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView

class RoomActivity : AppCompatActivity() {

    private var pressedTime: Long = 0
    lateinit var animationView: LottieAnimationView
    lateinit var alertDialog: AlertDialog
    private lateinit var customOverflowIcon: ImageView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var userprofileImageView: ShapeableImageView
    private lateinit var userFullNameTV: TextView
    private lateinit var userEmailTV: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var latestProfileImage: Int = 1
    private lateinit var userFullName: String
    private lateinit var userEmail: String
    private val contextTAG: String = "RoomActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        GlobalAccess.loadUserData(this)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        drawerLayout = findViewById(R.id.room_drawer_layout)
        navigationView = findViewById(R.id.room_nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.app_icon_48)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.itemIconTintList = null
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black)

        customOverflowIcon = toolbar.findViewById(R.id.custom_overflow_icon)
        customOverflowIcon.setOnClickListener {
            openCustomMenu()
        }

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.loading_box, null)
        animationView = dialogView.findViewById(R.id.lottie_animation)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)


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

        // Initial Fragment selection & Bottom Navigation bar style
        bottomNavigation.selectedItemId = R.id.nav_home
        replaceFragment(DefaultFragment())
        updateIconTint("#EE437D")

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    replaceFragment(DefaultFragment())
                    toolbar.title = "Home"
                    toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleStyle) // Revert to normal style
                    updateIconTint("#EE437D")
                }

                R.id.nav_add -> {
                    replaceFragment(AddDataFragment())
                    toolbar.title = "Add Expenses"
                    toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleStyle) // Revert to normal style
                    updateIconTint("#2196F3")
                }

                R.id.nav_expenses -> {
                    replaceFragment(GetDataFragment())
                    toolbar.title = "My Expenses"
                    toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleStyle) // Revert to normal style
                    updateIconTint("#FB8C00")
                }

                R.id.nav_room -> {
                    replaceFragment(GetAllDataFragment())
                    toolbar.title = "Room Expenses"
                    toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleStyle) // Revert to normal style
                    updateIconTint("#4CAF50")
                }

                R.id.nav_statistics -> {
                    replaceFragment(StatisticsFragment())
                    toolbar.title = "Statistics"
                    toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleStyle) // Revert to normal style
                    updateIconTint("#673AB7")
                }
            }
            true
        }

        navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_profile) {
                ActivityUtils.navigateToActivity(this, Intent(this, EditDetailsActivity::class.java), "RoomActivity received nav-profile action from user")
            } else if (item.itemId == R.id.nav_view_logs) {
                ActivityUtils.navigateToActivity(this, Intent(this, TestingActivity::class.java), "RoomActivity received nav-view_logs action from user")
            } else if (item.itemId == R.id.nav_report) {
                ActivityUtils.navigateToActivity(this, Intent(this, ContactUsActivity::class.java), "RoomActivity received nav-report action from user")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    finishAffinity()
                } else {
                    Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT)
                        .show()
                }
                pressedTime = System.currentTimeMillis()
            }
        })

    }

    // Optionally handle back button to close drawer if opened
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun updateIconTint(selectedColorHex: String) {
        val selectedColor = selectedColorHex.toColorInt()
        val gray = ContextCompat.getColor(this, android.R.color.darker_gray)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val colors = intArrayOf(
            selectedColor,
            gray
        )

        val colorStateList = ColorStateList(states, colors)
        bottomNavigation.itemIconTintList = colorStateList
        bottomNavigation.itemTextColor = colorStateList
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
                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout") { _, _ ->
                            // If the user confirms, proceed with logout
                            LOGGING.ERROR(this, contextTAG, "User Logged out from Menu")
                            ActivityUtils.navigateToActivity(this,  Intent(this, LoginActivity::class.java), "RoomActivity Received menu-logout action from user")
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

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }
}