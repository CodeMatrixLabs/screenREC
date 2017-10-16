package software.kanunnikoff.screenrec

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
import java.text.SimpleDateFormat
import android.content.Intent
import android.media.projection.MediaProjectionManager
import software.kanunnikoff.screenrec.core.PermissionManager
import software.kanunnikoff.screenrec.core.PermissionManager.CAST_PERMISSION_CODE
import software.kanunnikoff.screenrec.core.PermissionManager.PERMISSIONS_CODE
import java.util.*
import android.content.pm.PackageManager
import software.kanunnikoff.screenrec.core.Core


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val recorder = MyMediaRecorder()
    private val projection = MyMediaProjection()
    private val dateFormat = SimpleDateFormat("_yyyyMMdd_HHmmss", Locale.getDefault())
    private val DEFAULT_FILE_NAME = "screenrec"
    private val displayMetrics = DisplayMetrics()
    private var isRecording = false

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
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

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
                            outputFile = DEFAULT_FILE_NAME + dateFormat.format(Date()) + ".mp4",
                            videoSizeWidth = displayMetrics.widthPixels,
                            videoSizeHeight = displayMetrics.heightPixels,
                            rotation = rotation)

                    projection.init(this, displayMetrics, recorder, resultCode, data)
                    recorder.start()
                    isRecording = true
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

    override fun onPause() {
        if (isRecording) {
            Core.showNotification(this, resources.getString(R.string.app_name), resources.getString(R.string.notification_body))
        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Core.hideNotifications(this)
    }
}
