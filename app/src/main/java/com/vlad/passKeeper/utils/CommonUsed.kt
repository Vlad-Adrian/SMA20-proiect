package com.vlad.passKeeper.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.util.Log
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vlad.passKeeper.R
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.CoroutineContext

class CommonUsed {
    companion object{
        fun checkConnectivity(context: Context):Boolean {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = manager.activeNetworkInfo

            if (null == activeNetwork) {
                val dialogBuilder = AlertDialog.Builder(context)
                // set message of alert dialog
                dialogBuilder.setMessage("Make sure that WI-FI or mobile data is turned on, then try again")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Retry") { _, _ ->
                        recreate(context as Activity)
                    }

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("No Internet Connection")
                alert.setIcon(R.drawable.logo)
                // show alert dialog
                alert.show()
                return false
            }
            return true
        }
    }
}