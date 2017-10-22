package software.kanunnikoff.screenrec.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import software.kanunnikoff.screenrec.core.MyMediaRecorder
import android.util.DisplayMetrics
import software.kanunnikoff.screenrec.core.MyMediaProjection
import android.content.Intent
import android.media.projection.MediaProjectionManager
import software.kanunnikoff.screenrec.core.PermissionManager
import software.kanunnikoff.screenrec.core.PermissionManager.CAST_PERMISSION_CODE
import software.kanunnikoff.screenrec.core.PermissionManager.PERMISSIONS_CODE
import java.util.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.ThumbnailUtils
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.annotation.UiThread
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.android.billingclient.api.BillingClient
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import software.kanunnikoff.screenrec.R
import software.kanunnikoff.screenrec.billing.BillingManager
import software.kanunnikoff.screenrec.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED
import software.kanunnikoff.screenrec.billing.BillingProvider
import software.kanunnikoff.screenrec.billing.MainViewController
import software.kanunnikoff.screenrec.core.Core
import software.kanunnikoff.screenrec.model.Record
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, BillingProvider {
    private val recorder = MyMediaRecorder()
    private val projection = MyMediaProjection()
    private val displayMetrics = DisplayMetrics()
    private var isRecording = false
    val allRecordsSubFragment = AllRecordsSubFragment()
    val favoredRecordsSubFragment = FavoredRecordsSubFragment()

    private var mBillingManager: BillingManager? = null
    private var mViewController: MainViewController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        windowManager.defaultDisplay.getMetrics(displayMetrics)

        fab.setOnClickListener { _ ->
            if (projection.isInited()) {
                recorder.stop()
                projection.stop()
                isRecording = false
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_fiber_manual_record_white_24px))
                Core.hideNotifications()

                val size: String
                var length = File(recorder.outputFileAbsolutePath).length() / 1024 / 1024   // megabytes

                if (length > 0) {
                    size = length.toString() + resources.getString(R.string.mb)
                } else {
                    length = File(recorder.outputFileAbsolutePath).length() / 1024   // kilobytes

                    if (length > 0) {
                        size = length.toString() + resources.getString(R.string.kb)
                    } else {
                        length = File(recorder.outputFileAbsolutePath).length()   // bytes
                        size = length.toString() + resources.getString(R.string.b)
                    }
                }

                val bitmap = ThumbnailUtils.createVideoThumbnail(recorder.outputFileAbsolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream)

                val record = Record(
                        -1,
                        Core.getRecordTitlePrefix() + " #${Core.recordNumber}",
                        Core.outputFormatString(),
                        recorder.outputFileAbsolutePath,
                        Core.audioEncoderString(),
                        Core.getAudioEncodingBitRate().toString(),
                        Core.getAudioSamplingRate().toString(),
                        Core.audioChannelsString(),
                        Core.videoEncoderString(),
                        Core.getVideoEncodingBitRate().toString(),
                        Core.getVideoFrameRate().toString(),
                        "${Core.getVideoSizeWidth()}x${Core.getVideoSizeHeight()}",
                        0,
                        size,
                        Core.formatDuration(recorder.duration / 1000),
                        Core.formatDate(recorder.date),
                        stream.toByteArray()
                        )

                record.id = Core.insertRecord(record)
                allRecordsSubFragment.adapter!!.records.add(0, record)
                allRecordsSubFragment.adapter?.notifyDataSetChanged()
                Core.recordNumber++
            } else {
                if (PermissionManager.hasPermissions(this)) {
                    startActivityForResult((getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).createScreenCaptureIntent(), CAST_PERMISSION_CODE)
                }
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = MyPagerAdapter(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all_records_tab))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.favored_records_tab))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position

                if (tab.position == RecordsFragment.ALL_RECORDS_TAB) {
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

        Core.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        if (Core.isFirstLaunch) {
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                val profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)

                Core.setVideoFrameRate(profile.videoFrameRate)
                Core.setVideoEncodingBitRate(profile.videoBitRate)
                Core.setVideoEncoder(profile.videoCodec)
                Core.setAudioChannels(profile.audioChannels)
                Core.setAudioSamplingRate(profile.audioSampleRate)
                Core.setAudioEncodingBitRate(profile.audioBitRate)
                Core.setAudioEncoder(profile.audioCodec)
                Core.setOutputFormat(profile.fileFormat)
            }

            Core.setVideoSizeWidth(displayMetrics.widthPixels)
            Core.setVideoSizeHeight(displayMetrics.heightPixels)

            Core.isFirstLaunch = false
        }

// ------------------------------------------- Ads

        MobileAds.initialize(applicationContext, resources.getString(R.string.admob_app_id))
        findViewById<AdView>(R.id.adView).loadAd(AdRequest.Builder().build())

// ------------------------------------------- In-App Billing

        mViewController = MainViewController(this)
        mBillingManager = BillingManager(this, mViewController!!.updateListener)
    }

    override fun isPremiumPurchased(): Boolean {
        return mViewController!!.isPremiumPurchased
    }

    fun onBillingManagerSetupFinished() {
        // клиент In-App Billing настроен
    }

    /**
     * Update UI to reflect model
     */
    @UiThread
    fun updateUi() {  // здесь я должен поменять надписи - покупка подтверждена
        if (isPremiumPurchased) {
            Core.isPremiumPurchased = true
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_rate -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)))
            }
            R.id.nav_share -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                intent.putExtra(Intent.EXTRA_TEXT, "Google Play: https://play.google.com/store/apps/details?id=" + packageName)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, resources.getString(R.string.share) + "..."))
            }
            R.id.nav_buy -> {
                if (!isPremiumPurchased) {
                    if (mBillingManager != null && mBillingManager!!.billingClientResponseCode > BILLING_MANAGER_NOT_INITIALIZED) {
                        mBillingManager?.initiatePurchaseFlow(Core.PREMIUM_SKU_ID, BillingClient.SkuType.INAPP)
                    }
                } else {
                    Core.showToast(resources.getString(R.string.premium_purchased))
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAST_PERMISSION_CODE -> {
                    val rotation = windowManager.defaultDisplay.rotation

                    recorder.init(
                            outputFormat = Core.getOutputFormat(),
                            outputFile = Core.getFileNamePrefix() + Core.formatDateForFileName(Date()) + "." + (if (Core.getOutputFormat() == MediaRecorder.OutputFormat.THREE_GPP) "3gp" else "mp4"),
                            audioEncoder = Core.getAudioEncoder(),
                            audioEncodingBitRate = Core.getAudioEncodingBitRate(),
                            audioSamplingRate = Core.getAudioSamplingRate(),
                            audioChannels = Core.getAudioChannels(),
                            videoEncoder = Core.getVideoEncoder(),
                            videoEncodingBitRate = Core.getVideoEncodingBitRate(),
                            videoFrameRate = Core.getVideoFrameRate(),
                            videoSizeWidth = Core.getVideoSizeWidth(),
                            videoSizeHeight = Core.getVideoSizeHeight(),
                            rotation = rotation)

                    projection.init(this, displayMetrics, recorder, resultCode, data)
                    recorder.start()
                    isRecording = true
                    fab.setImageDrawable(resources.getDrawable(R.drawable.ic_stop_white_24px))
                    Core.showNotification(resources.getString(R.string.app_name), resources.getString(R.string.notification_body))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if (grantResults.size >= 2 && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult((getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).createScreenCaptureIntent(), CAST_PERMISSION_CODE)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        val stopRecording = intent?.extras?.getBoolean(Core.STOP_RECORDING_ACTION)

        if (stopRecording != null && stopRecording) {
            fab.performClick()
        } else {
            super.onNewIntent(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (mBillingManager != null && mBillingManager!!.billingClientResponseCode == BillingClient.BillingResponse.OK) {
            mBillingManager!!.queryPurchases()
        }
    }

    public override fun onDestroy() {
        mBillingManager?.destroy()
        super.onDestroy()
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
