package software.kanunnikoff.screenrec.ui

import android.os.Bundle
import software.kanunnikoff.screenrec.R

class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addPreferencesFromResource(R.xml.preferences)
    }
}
