package software.kanunnikoff.screenrec.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import software.kanunnikoff.screenrec.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import software.kanunnikoff.screenrec.core.Core
import software.kanunnikoff.screenrec.model.Record
import java.io.File


/**
 * Created by dmitry on 20/10/2017.
 */
class RecordMenuDialogFragment() : DialogFragment() {
    val images = arrayOf(R.drawable.ic_edit_white_24px, R.drawable.ic_play_arrow_white_24px, R.drawable.ic_share_white_24px, R.drawable.ic_delete_white_24px)
    var record: Record? = null
    var onDelete: (() -> Unit)? = null
    var onRename: (() -> Unit)? = null

    @SuppressLint("ValidFragment")
    constructor(record: Record, onDelete: () -> Unit, onRename: () -> Unit) : this() {
        this.record = record
        this.onDelete = onDelete
        this.onRename = onRename
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = Array<String>(4, init = { index ->
            return@Array when (index) {
                0 -> activity.resources.getString(R.string.rename)
                1 -> activity.resources.getString(R.string.play)
                2 -> activity.resources.getString(R.string.share)
                else -> activity.resources.getString(R.string.delete)
            }
        })

        val adapter = object : ArrayAdapter<String>(activity, R.layout.record_menu_item, items) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
                val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.record_menu_item, null)
                view.findViewById<ImageView>(R.id.itemImage).setImageDrawable(activity.resources.getDrawable(images[position]))
                view.findViewById<TextView>(R.id.itemName).text = items[position]

                if (position == RENAME_ITEM && !Core.isPremiumPurchased) {
                    view.findViewById<TextView>(R.id.itemName).text = view.findViewById<TextView>(R.id.itemName).text.toString() + " - Premium"
                }

                return view
            }
        }

        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setTitle(null).setAdapter(adapter, { _, which ->
            when (which) {
                RENAME_ITEM -> {
                    if (Core.isPremiumPurchased) {
                        onRename?.invoke()
                    }
                }

                PLAY_ITEM -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(record!!.outputFile))
                    intent.setDataAndType(Uri.parse(record!!.outputFile), if (record!!.outputFile.endsWith("mp4")) "video/mp4" else "video/3gp")
                    startActivity(intent)
                }

                SHARE_ITEM -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_SUBJECT, record!!.title)
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(record!!.outputFile)))
                    intent.type = "text/plain"
                    startActivity(Intent.createChooser(intent, activity.resources.getString(R.string.share) + "..."))
                }

                DELETE_ITEM -> {
                    onDelete?.invoke()
                }
            }
        })

        return builder.create()
    }

    companion object {
        const val TAG = "RecordMenuDialogFragment"
        const val RENAME_ITEM = 0
        const val PLAY_ITEM = 1
        const val SHARE_ITEM = 2
        const val DELETE_ITEM = 3
    }
}