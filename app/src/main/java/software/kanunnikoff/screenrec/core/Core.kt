package software.kanunnikoff.screenrec.core

import android.content.Context
import android.view.Gravity
import android.widget.Toast

/**
 * Created by dmitry on 15/10/2017.
 */
object Core {
    const val APP_TAG = "screenREC"

    fun showToast(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}