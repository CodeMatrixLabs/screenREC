package software.kanunnikoff.screenrec.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import software.kanunnikoff.screenrec.R

/**
 * Created by dmitry on 16/10/2017.
 */
object PermissionManager {
    private val FRAGMENT_DIALOG = "dialog"

    const val PERMISSIONS_CODE = 1
    const val CAST_PERMISSION_CODE = 2

    fun hasPermissions(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) + ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as AppCompatActivity, Manifest.permission.RECORD_AUDIO) || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ConfirmationDialog(context).show(context.fragmentManager, FRAGMENT_DIALOG)
            } else {
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_CODE)
            }

            return false
        }

        return true
    }

    class ConfirmationDialog() : DialogFragment() {
        private var cntxt: Context? = null

        @SuppressLint("ValidFragment")
        constructor(cntxt: Context) : this() {
            this.cntxt = cntxt
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(cntxt)
                    .setMessage(R.string.permissions_message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> ActivityCompat.requestPermissions(cntxt as AppCompatActivity, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_CODE) }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        // working without permission
                    }
                    .create()
        }
    }

}
