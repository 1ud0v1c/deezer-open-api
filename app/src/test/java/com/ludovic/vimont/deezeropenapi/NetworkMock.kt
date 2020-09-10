package com.ludovic.vimont.deezeropenapi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowNetwork
import org.robolectric.shadows.ShadowNetworkCapabilities
import org.robolectric.shadows.ShadowNetworkInfo

/**
 * Class to mock network access for unit tests
 */
object NetworkMock {
    private fun getConnectivityManager(context: Context): ConnectivityManager? {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun mockNetworkAccess(context: Context) {
        val connectivityManager = getConnectivityManager(context)
        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
        val networkInfo: NetworkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED,
            ConnectivityManager.TYPE_WIFI,
            0,
            true,
            NetworkInfo.State.CONNECTED
        )
        val network: Network = ShadowNetwork.newInstance(1)
        val networkCapabilities: NetworkCapabilities = ShadowNetworkCapabilities.newInstance()
        Shadows.shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)
        shadowConnectivityManager.setNetworkCapabilities(network, networkCapabilities)
    }
}