package software.kanunnikoff.screenrec.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import software.kanunnikoff.screenrec.R


/**
 * Created by dmitry on 17/10/2017.
 */
class RecordsFragment : Fragment() {
    private val allRecordsSubFragment = AllRecordsSubFragment()
    private val favoredRecordsSubFragment = FavoredRecordsSubFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_records, container, false)

        val viewPager = view.findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = MyPagerAdapter(childFragmentManager)

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all_records_tab))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.favored_records_tab))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position

                if (tab.position == ALL_RECORDS_TAB) {
                    if (allRecordsSubFragment.adapter!!.records.isEmpty()) {
                        allRecordsSubFragment.loadRecords()
                    }
                } else {
                    if (favoredRecordsSubFragment.adapter!!.records.isEmpty()) {
                        favoredRecordsSubFragment.loadRecords()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        return view
    }

    private inner class MyPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                ALL_RECORDS_TAB -> allRecordsSubFragment
                else -> favoredRecordsSubFragment
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

    companion object {
        const val ALL_RECORDS_TAB = 0
    }
}
