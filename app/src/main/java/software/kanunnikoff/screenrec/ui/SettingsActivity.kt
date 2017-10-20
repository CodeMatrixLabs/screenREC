package software.kanunnikoff.screenrec.ui

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.view.MenuItem
import software.kanunnikoff.screenrec.R

class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }

            return true
        }

        return super.onMenuItemSelected(featureId, item)
    }
}
