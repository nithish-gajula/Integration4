package com.example.integration4

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.get
import androidx.core.view.size

class RoomActivity : AppCompatActivity() {

    private var pressedTime: Long = 0
    lateinit var animationView: LottieAnimationView
    lateinit var alertDialog: AlertDialog
    private lateinit var customOverflowIcon: ImageView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private val contextTAG: String = "RoomActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        GlobalAccess.loadUserData(this)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.app_icon_48)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        customOverflowIcon = toolbar.findViewById(R.id.custom_overflow_icon)
        customOverflowIcon.setOnClickListener {
            openCustomMenu()
        }

        bottomNavigation = findViewById(R.id.bottom_navigation)

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.loading_box, null)
        animationView = dialogView.findViewById(R.id.lottie_animation)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)

        // Initial Fragment selection & Bottom Navigation bar style
        bottomNavigation.selectedItemId = R.id.nav_home
        replaceFragment(DefaultFragment())
        toolbar.title = "Home"
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleStyle) // Revert to normal style
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
                R.id.menu_profile -> {
                    ActivityUtils.navigateToActivity(this, Intent(this, EditDetailsActivity::class.java), "MainActivity received menu-profile action from user")
                    true
                }

                R.id.menu_relaunch -> {
                    ActivityUtils.relaunch(this)
                    true
                }

                R.id.menu_contact_us -> {
                    ActivityUtils.navigateToActivity(this, Intent(this, ContactUsActivity::class.java), "MainActivity received menu-contactUs action from user")
                    true
                }

                R.id.view_logs -> {
                    ActivityUtils.navigateToActivity(this, Intent(this, TestingActivity::class.java), "MainActivity received button-TestingActivity action from user")
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
