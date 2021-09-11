@file:Suppress("DEPRECATION")

package com.example.book.util

import android.net.ConnectivityManager
import android.content.Context
import android.net.NetworkInfo

@Suppress("DEPRECATION")
class ConnectionManagement {
    //create a function which will return boolean value and tell us weter the app is connected or not

    fun checkConnectivity(context: Context): Boolean{
        var connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        if (activeNetwork?.isConnected != null) {
            return activeNetwork.isConnected
        } else {
            return false
        }
    }
}