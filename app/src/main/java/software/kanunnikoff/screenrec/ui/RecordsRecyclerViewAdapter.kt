package software.kanunnikoff.screenrec.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import software.kanunnikoff.screenrec.R
import software.kanunnikoff.screenrec.core.Core
import software.kanunnikoff.screenrec.model.Record
import java.util.ArrayList


/**
 * Created by dmitry on 17/10/2017.
 */
class RecordsRecyclerViewAdapter(val activity: MainActivity, recyclerView: RecyclerView, val isFavoredRecordsSubFragment: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var records: ArrayList<Record> = ArrayList()
    private val onClickListener = View.OnClickListener { view ->
        val record = this.records[recyclerView.getChildLayoutPosition(view)]

//        val fragment = RecordFragment.create(record)
//        val transaction = (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.fragment_container, fragment, RecordFragment.RECORD_FRAGMENT.toString())
//        transaction.addToBackStack(null)
//        transaction.commit()

        if (view.tag == "invisible") {
            view.findViewById<LinearLayout>(R.id.recordMeta).visibility = View.VISIBLE
            view.tag = "visible"
        } else {
            view.findViewById<LinearLayout>(R.id.recordMeta).visibility = View.GONE
            view.tag = "invisible"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return RECORD_VIEW_TYPE
    }

    /**
     * Создание новых View и RecordViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.records_recyclerview_item, parent, false)
        view.setOnClickListener(onClickListener)

        return RecordViewHolder(view)
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecordViewHolder) {
            val record = records[position]

            try {
                Core.loadBitmap(record.thumbnail, holder.thumbnail)
            } catch (cause: Throwable) {
                Log.e(Core.APP_TAG, "getting image error", cause)
            }

            holder.title.text = record.title
            holder.favorite.setImageDrawable(if (record.isFavored == 1) activity.resources.getDrawable(R.drawable.ic_favorite_black_24px) else activity.resources.getDrawable(R.drawable.ic_favorite_border_black_24px))
            holder.fileSize.text = record.fileSize
            holder.duration.text = record.duration
            holder.date.text = record.date
            holder.outputFile.text = record.outputFile.substring(record.outputFile.lastIndexOf("/") + 1)
            holder.outputFormat.text = record.outputFormat
            holder.audioEncoder.text = record.audioEncoder
            holder.audioEncodingBitRate.text = record.audioEncodingBitRate
            holder.audioSamplingRate.text = record.audioSamplingRate
            holder.audioChannels.text = record.audioChannels
            holder.videoEncoder.text = record.videoEncoder
            holder.videoEncodingBitRate.text = record.videoEncodingBitRate
            holder.videoFrameRate.text = record.videoFrameRate
            holder.videoSize.text = record.videoSize

            holder.favorite.setOnClickListener {
                if (record.isFavored == 1) {
                    Core.unFavoriteRecord(record)
                    record.isFavored = 0

                    if (!isFavoredRecordsSubFragment) {
                        activity.favoredRecordsSubFragment.adapter!!.records.remove(record)
                        activity.favoredRecordsSubFragment.adapter?.notifyDataSetChanged()
                        holder.favorite.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_favorite_border_black_24px))
                    } else {
                        records.remove(record)
                        notifyDataSetChanged()
                        activity.allRecordsSubFragment.adapter!!.records.find { it -> it.id == record.id }?.isFavored = 0
                        activity.allRecordsSubFragment.adapter!!.notifyDataSetChanged()
                    }
                } else {
                    Core.favoriteRecord(record)
                    record.isFavored = 1
                    activity.favoredRecordsSubFragment.adapter!!.records.add(0, record)
                    activity.favoredRecordsSubFragment.adapter?.notifyDataSetChanged()
                    holder.favorite.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_favorite_black_24px))
                }
            }

            holder.menu.setOnClickListener {
                RecordMenuDialogFragment(record, onDelete = {
                    ConfirmDeletionDialogFragment(onConfirm = {
                        Core.deleteRecord(record)
                        activity.allRecordsSubFragment.adapter!!.records.remove(record)
                        activity.allRecordsSubFragment.adapter?.notifyDataSetChanged()
                        activity.favoredRecordsSubFragment.adapter!!.records.remove(record)
                        activity.favoredRecordsSubFragment.adapter?.notifyDataSetChanged()
                    }).show(activity.supportFragmentManager, ConfirmDeletionDialogFragment.TAG)
                },
                onRename = {
                    RenameRecordDialogFragment(onRename = { title ->
                        Core.renameRecord(record, title)
                        record.title = title

                        activity.allRecordsSubFragment.adapter!!.records.find { it -> it.id == record.id }?.title = title
                        activity.allRecordsSubFragment.adapter!!.notifyDataSetChanged()
                        activity.favoredRecordsSubFragment.adapter!!.records.find { it -> it.id == record.id }?.title = title
                        activity.favoredRecordsSubFragment.adapter!!.notifyDataSetChanged()
                    }).show(activity.supportFragmentManager, RenameRecordDialogFragment.TAG)
                }).show(activity.supportFragmentManager, RecordMenuDialogFragment.TAG)
            }

            if (position % 2 != 0) {
                holder.view.setBackgroundColor(activity.resources.getColor(R.color.darkBackgroundColor))
            } else {
                holder.view.setBackgroundColor(activity.resources.getColor(R.color.defaultBackgroundColor))
            }
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }

    /**
     * Реализация класса RecordViewHolder, хранящего ссылки на виджеты.
     */
    private inner class RecordViewHolder internal constructor(val view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail = view.findViewById<ImageView>(R.id.thumbnail)
        val title = view.findViewById<TextView>(R.id.title)
        val favorite = view.findViewById<ImageView>(R.id.favorite)
        val fileSize = view.findViewById<TextView>(R.id.fileSize)
        val duration = view.findViewById<TextView>(R.id.duration)
        val date = view.findViewById<TextView>(R.id.date)
        val outputFile = view.findViewById<TextView>(R.id.outputFile)
        val outputFormat = view.findViewById<TextView>(R.id.outputFormat)
        val audioEncoder = view.findViewById<TextView>(R.id.audioEncoder)
        val audioEncodingBitRate = view.findViewById<TextView>(R.id.audioEncodingBitRate)
        val audioSamplingRate = view.findViewById<TextView>(R.id.audioSamplingRate)
        val audioChannels = view.findViewById<TextView>(R.id.audioChannels)
        val videoEncoder = view.findViewById<TextView>(R.id.videoEncoder)
        val videoEncodingBitRate = view.findViewById<TextView>(R.id.videoEncodingBitRate)
        val videoFrameRate = view.findViewById<TextView>(R.id.videoFrameRate)
        val videoSize = view.findViewById<TextView>(R.id.videoSize)
        val menu = view.findViewById<ImageView>(R.id.menu)
    }

    companion object {
        private val RECORD_VIEW_TYPE = 0
    }
}
