package software.kanunnikoff.screenrec.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import software.kanunnikoff.screenrec.R


/**
 * Created by dmitry on 20/10/2017.
 */
class ConfirmDeletionDialogFragment() : DialogFragment() {
    private var onConfirm: (() -> Unit)? = null

    @SuppressLint("ValidFragment")
    constructor(onConfirm: () -> Unit) : this() {
        this.onConfirm = onConfirm
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage(R.string.dialog_delete_record)
                .setPositiveButton(R.string.delete, { _, _ ->
                    onConfirm?.invoke()
                })
                .setNegativeButton(R.string.cancel, { _, _ ->
                    // User cancelled the dialog
                })

        val dialog: AlertDialog = builder.create()

        dialog.setOnShowListener { _ ->
            run {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.resources.getColor(android.R.color.white))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.resources.getColor(android.R.color.white))
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "ConfirmDeletionDialogFragment"
    }
}