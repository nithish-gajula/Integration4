package com.example.integration4

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.Image
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import org.w3c.dom.Text

class DefaultFragment : Fragment() {

    private lateinit var welcomeText: TextView
    private lateinit var offlineImage: ImageView
    private lateinit var offlineLottieView: LottieAnimationView
    private lateinit var networkReceiver: BroadcastReceiver
    private val contextTAG: String = "DefaultFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_default, container, false)

        welcomeText = view.findViewById(R.id.welcomeTV)
        offlineImage = view.findViewById(R.id.offlineImageView)
        offlineLottieView = view.findViewById(R.id.offlineLottieView)

        checkInternetAndUpdateUI(requireContext())

        return view
    }

    override fun onResume() {
        super.onResume()
        registerNetworkReceiver()
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(networkReceiver)
    }

    private fun checkInternetAndUpdateUI(context: Context) {
        if (isInternetAvailable(context)) {
            welcomeText.visibility = View.VISIBLE
            offlineImage.visibility = View.GONE
            offlineLottieView.visibility = View.GONE
        } else {
            welcomeText.visibility = View.GONE
            offlineImage.visibility = View.GONE
            offlineLottieView.visibility = View.VISIBLE
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun registerNetworkReceiver() {
        networkReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                checkInternetAndUpdateUI(context)
            }
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkReceiver, filter)
    }

}