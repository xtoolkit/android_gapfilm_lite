package com.gapfilm.app.presentation.util

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.registerNetwork(
    connectivityManager: ConnectivityManager,
    onInitFire: Boolean = true,
    changeState: (isConnected: Boolean) -> Unit
) {
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    if (onInitFire) changeState(isOnline(connectivityManager))

    connectivityManager.registerNetworkCallback(networkRequest,
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                changeState(true)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                changeState(false)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                changeState(false)
            }
        }
    )
}

private fun isOnline(connectivityManager: ConnectivityManager) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val n = connectivityManager.activeNetwork
        if (n != null) {
            val nc = connectivityManager.getNetworkCapabilities(n)
            nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        }
        false
    } else {
        val netInfo = connectivityManager.activeNetworkInfo
        netInfo != null && netInfo.isConnectedOrConnecting
    }