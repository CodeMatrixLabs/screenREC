package software.kanunnikoff.screenrec.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import software.kanunnikoff.screenrec.R
import software.kanunnikoff.screenrec.core.Core


/**
 * Created by dmitry on 20/10/2017.
 */
class RenameRecordDialogFragment() : DialogFragment() {
    private var onRename: ((String) -> Unit)? = null

    @SuppressLint("ValidFragment")
    constructor(onRename: (String) -> Unit) : this() {
        this.onRename = onRename
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(this@RenameRecordDialogFragment.context!!, R.style.MyDialogTheme)
        val inflater = this@RenameRecordDialogFragment.layoutInflater
        val view: View = inflater.inflate(R.layout.rename_record_dialog, null)

        builder.setView(view)
                .setPositiveButton(R.string.rename, { _, _ ->
                    val text = view.findViewById<EditText>(R.id.title).text.toString()

                    if (text.isNotEmpty()) {
                        onRename?.invoke(text)
                    } else {
                        Core.showToast(this@RenameRecordDialogFragment.resources.getString(R.string.field_must_not_be_empty))
                    }
                })
                .setNegativeButton(R.string.cancel, { _, _ -> this@RenameRecordDialogFragment.dialog.cancel() })

        val dialog: AlertDialog = builder.create()

        dialog.setOnShowListener { _ ->
            run {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this@RenameRecordDialogFragment.resources.getColor(android.R.color.white))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this@RenameRecordDialogFragment.resources.getColor(android.R.color.white))
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "RenameRecordDialogFragment"
    }
}