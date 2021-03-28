package de.jonas_thelemann.uni.gosoan

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.BuildConfig.APPLICATION_ID
import de.jonas_thelemann.uni.gosoan.PreferenceUtil.Companion.getKey
import de.jonas_thelemann.uni.gosoan.navigation.GosoanNavigation
import de.jonas_thelemann.uni.gosoan.repository.SensorRepository
import de.jonas_thelemann.uni.gosoan.service.LocationService
import de.jonas_thelemann.uni.gosoan.service.REQUEST_PERMISSIONS_REQUEST_CODE
import de.jonas_thelemann.uni.gosoan.service.SensorService
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_SENSOR_TOGGLE_ID
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var sensorRepository: SensorRepository

    val gosoanNavigation: GosoanNavigation = GosoanNavigation(this)

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
            if (sharedPreferences == null) return@OnSharedPreferenceChangeListener

            if (key == getKey(
                    SensorService.getLocationServiceGosoanSensor(this).getId(),
                    PREFERENCE_SENSOR_TOGGLE_ID
                )
            ) {
                if (sharedPreferences.getBoolean(
                        key,
                        false
                    )
                ) {
                    if (!LocationService.checkPermissions(this)) {
                        LocationService.requestPermissions(this)
                    }
//                    else {
//                        getLastLocation()
//                    }
                }
            }

            SensorService.restart(this)
        }
    private var searchQuery: String = ""

    companion object {
        const val STATE_SEARCH_QUERY = "searchQuery"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            with(savedInstanceState) {
                searchQuery = getString(STATE_SEARCH_QUERY) ?: ""
            }
        }

        setContentView(R.layout.activity_main)

        gosoanNavigation.onCreateActivity()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        SensorService.start(this)
    }

//    override fun onStart() {
//        super.onStart()
//    }

    override fun onResume() {
        super.onResume()

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString(STATE_SEARCH_QUERY, searchQuery)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

//    override fun onStop() {
//        super.onStop()
////        sensorService.onStop()
//    }

    override fun onDestroy() {
        super.onDestroy()

        stopService(Intent(this, SensorService::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        return gosoanNavigation.onSupportNavigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        if (menu == null) return super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.bottom_app_bar, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val textChangeListener: OnQueryTextListener = object : OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                searchQuery = query
                sensorRepository.fetchSensors(applicationContext, query)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        }
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(textChangeListener)
        searchView.setQuery(searchQuery, true)

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.i("onRequestPermissionResult")
        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return
        when {
            // If user interaction was interrupted, the permission request is cancelled and you
            // receive empty arrays.
            grantResults.isEmpty() -> Timber.i("User interaction was cancelled.")

            // Permission granted.
            (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLocation()
            else -> {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(this, R.string.permission_denied_explanation, R.string.settings, Snackbar.LENGTH_INDEFINITE
                ) {
                    // Build intent that displays the App settings screen.
                    val intent = Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
            }
        }
    }
}