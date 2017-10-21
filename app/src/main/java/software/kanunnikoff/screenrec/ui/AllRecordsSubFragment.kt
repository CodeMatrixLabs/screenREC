package software.kanunnikoff.screenrec.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import software.kanunnikoff.screenrec.R
import software.kanunnikoff.screenrec.core.Callback
import software.kanunnikoff.screenrec.core.Core
import software.kanunnikoff.screenrec.model.Record

/**
 * Created by dmitry on 17/10/2017.
 */
class AllRecordsSubFragment : Fragment() {
    private var start: Long = 0
    var adapter: RecordsRecyclerViewAdapter? = null
    @Volatile private var loading: Boolean = false
    @Volatile private var loadedWhenTouched: Boolean = false
    private var myView: View? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (myView != null) {
            return myView
        }

        myView = inflater.inflate(R.layout.sub_fragment_records, container, false)

        val recyclerView = myView!!.findViewById<RecyclerView>(R.id.records_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = RecordsRecyclerViewAdapter(activity as MainActivity, false)
        recyclerView.adapter = adapter

        recyclerView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    loadedWhenTouched = false
                }
            }

            false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (adapter!!.records.isEmpty()) {
                    return
                }

                if (layoutManager.findFirstCompletelyVisibleItemPosition() != 0 && layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.itemCount - 1 && !loading && !loadedWhenTouched) {
                    loadedWhenTouched = true
                    Handler().post { loadRecords() }
                }
            }
        })

        return myView
    }

    fun loadRecords() {
        if (loading) {
            return
        }

        loading = true

        Core.receiveAllRecords(start, object : Callback<Record> {
            override fun onResult(records: ArrayList<Record>) {
                if (records.isNotEmpty()) {
                    records.filterNot { adapter!!.records.contains(it) }.forEach { adapter!!.records.add(it) }
                    adapter!!.notifyDataSetChanged()
                    start = records.last().id
                } else {
                    if (adapter!!.records.last().id != -1L) {
                        adapter!!.records.add(Record())
                        adapter!!.notifyDataSetChanged()
                    }
                }

                loading = false
            }
        })
    }

    override fun onResume() {
        super.onResume()

        if (adapter!!.records.isEmpty()) {
            loadRecords()
        }
    }
}
