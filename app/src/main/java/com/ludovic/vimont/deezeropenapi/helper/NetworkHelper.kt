package com.ludovic.vimont.deezeropenapi.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Help us to check, if the user is connected to internet
 * @see: https://developer.android.com/training/monitoring-device-state/connectivity-status-type
 */
object NetworkHelper {
    fun isOnline(context: Context): Boolean {
        val connectivityManager: ConnectivityManager? =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.getNetworkCapabilities(it.activeNetwork)?.run {
                    return when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            true
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            true
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            } else {
                it.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        return true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        return true
                    }
                }
            }
        }
        return false
    }
}