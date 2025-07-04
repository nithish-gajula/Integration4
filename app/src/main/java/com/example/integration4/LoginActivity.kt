package com.example.integration4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date

class LoginActivity : AppCompatActivity() {
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var forgotPasswordTV: TextView
    private lateinit var signupTV: TextView
    private lateinit var loginBTN: Button
    private lateinit var resultTV: TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var animationView: LottieAnimationView
    private lateinit var alertDialog: AlertDialog
    private val contextTAG: String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailET = findViewById(R.id.email_et_id)
        passwordET = findViewById(R.id.password_et_id)
        forgotPasswordTV = findViewById(R.id.forgotpswd_tv_id)
        loginBTN = findViewById(R.id.login_btn_id)
        signupTV = findViewById(R.id.signup_tv_id)
        resultTV = findViewById(R.id.result_tv_id)
        requestQueue = Volley.newRequestQueue(applicationContext)

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.loading_box, null)
        animationView = dialogView.findViewById(R.id.lottie_animation)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)

        val emailTIL = findViewById<TextInputLayout>(R.id.email_til_id)
        val passwordTIL = findViewById<TextInputLayout>(R.id.password_til_id)
        emailTIL.setStartIconTintList(null)
        passwordTIL.setStartIconTintList(null)

        loginBTN.setOnClickListener {
            resultTV.visibility = View.INVISIBLE
            loginFunction()
        }

        signupTV.setOnClickListener {
            ActivityUtils.navigateToActivity(this, Intent(this, SignupActivity::class.java), "LoginActivity received button-signup action from user")
        }

        forgotPasswordTV.setOnClickListener {
            ActivityUtils.navigateToActivity(this, Intent(this, ForgotPasswordActivity::class.java), "LoginActivity received button-forgotpassword action from user")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }


    private fun loginFunction() {
        val email = emailET.text.trim().toString()
        val password = passwordET.text.trim().toString()

        if (!email.endsWith("@gmail.com") || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.error = getString(R.string.enter_valid_email)
            return
        }

        if (password.isEmpty()) {
            passwordET.error = getString(R.string.password_should_not_be_empty)
            return
        }

        animationView.setAnimation(R.raw.verifying)
        animationView.playAnimation()
        alertDialog.show()

        val url = getString(R.string.spreadsheet_url)
        val loginParameter = "?action=login&email=$email&password=$password"
        val stringRequest = StringRequest(
            Request.Method.GET, "$url$loginParameter",
            { response ->
                LOGGING.INFO(this, contextTAG, "Got response = $response")
                extractJsonData(response)
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
            },
            { error ->
                LOGGING.DEBUG(this, contextTAG, "Got Error $error")
                animationView.setAnimation(R.raw.error)
                animationView.playAnimation()
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
                resultTV.visibility = View.VISIBLE
                resultTV.text = error.toString()
            }
        )
        val socketTimeOut = 50000
        val policy = DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = policy
        requestQueue.add(stringRequest)
    }


    private fun extractJsonData(jsonResponse: String) {
        val emailStatus: String
        val passwordStatus: String
        val loginStatus: String
        val roomId: String

        try {
            val jsonObj = JSONObject(jsonResponse)
            val jsonArray = jsonObj.getJSONArray("items")

            if (jsonArray.length() > 0) {
                val jsonItem = jsonArray.getJSONObject(0)
                emailStatus = jsonItem.getString("email_status")
                passwordStatus = jsonItem.getString("password_status")
                loginStatus = jsonItem.getString("result")
                roomId = jsonItem.getString("roomId")

                when {
                    loginStatus.toBoolean() -> {
                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                        animationView.setAnimation(R.raw.done)
                        animationView.playAnimation()
                        createUserDataFile(jsonItem)
                        Handler(Looper.getMainLooper()).postDelayed({
                            LOGGING.INFO(this, contextTAG, "Login Success with roomId = $roomId")

                            if (roomId == "0" || roomId.isEmpty()) {
                                LOGGING.DEBUG(this, contextTAG, "Invalid Room ID = 0 or empty")
                                ActivityUtils.navigateToActivity(
                                    this,
                                    Intent(this, MainActivity::class.java), "LoginActivity received login success and room id not available from database"
                                )
                            } else {
                                GlobalAccess.loadUserData(this)
                                ActivityUtils.navigateToActivity(
                                    this,
                                    Intent(this, RoomActivity::class.java), "LoginActivity received login success and room id is valid from database"
                                )
                            }
                            finish()
                        }, 2000)

                    }

                    emailStatus.toBoolean() && !passwordStatus.toBoolean() -> {
                        LOGGING.DEBUG(this, contextTAG, "Login failed, Reason - ${getString(R.string.incorrect_password)}")
                        animationView.setAnimation(R.raw.error)
                        animationView.playAnimation()
                        resultTV.visibility = View.VISIBLE
                        resultTV.text = getString(R.string.incorrect_password)
                    }

                    else -> {
                        LOGGING.DEBUG(this, contextTAG, "Login failed, Reason - ${getString(R.string.no_user_data_found)}")
                        animationView.setAnimation(R.raw.error)
                        animationView.playAnimation()
                        resultTV.visibility = View.VISIBLE
                        resultTV.text = getString(R.string.no_user_data_found)
                    }
                }
            } else {
                LOGGING.DEBUG(this, contextTAG, "Login failed, Reason - ${getString(R.string.no_data_found)}")
                animationView.setAnimation(R.raw.error)
                animationView.playAnimation()
                resultTV.visibility = View.VISIBLE
                resultTV.text = getString(R.string.no_data_found)
            }
        } catch (e: JSONException) {
            LOGGING.DEBUG(this, contextTAG, "Login failed, Reason - ${e.printStackTrace()}")
            e.printStackTrace()
        }
    }

    private fun createUserDataFile(userData: JSONObject) {

        try {
            userData.put("loginTime", DateFormat.getDateTimeInstance().format(Date()).toString())
            ActivityUtils.getUserDataFile(this).writeText(userData.toString())
            LOGGING.INFO(this, contextTAG, "Login Information : $userData")
        } catch (e: IOException) {
            e.printStackTrace()
            LOGGING.DEBUG(this, contextTAG, "Writing to Files failed : ${e.printStackTrace()}")
            Toast.makeText(this, "Failed to write file", Toast.LENGTH_SHORT).show()
        }

    }
}