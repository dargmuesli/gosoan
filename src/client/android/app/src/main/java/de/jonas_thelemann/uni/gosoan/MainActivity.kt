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
import android.view.View
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.navigation.GosoanNavigation
import de.jonas_thelemann.uni.gosoan.repository.SensorRepository
import de.jonas_thelemann.uni.gosoan.service.REQUEST_PERMISSIONS_REQUEST_CODE
import de.jonas_thelemann.uni.gosoan.service.SensorService
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var sensorRepository: SensorRepository

    val gosoanNavigation: GosoanNavigation = GosoanNavigation(this)

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, _: String? ->
            if (sharedPreferences == null) return@OnSharedPreferenceChangeListener

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
            grantResults.isEmpty() -> {
                Timber.i("User interaction was cancelled.")
            }
            (grantResults[0] == PackageManager.PERMISSION_GRANTED) ->
                Timber.i("Permission granted.")
            else -> {
                Timber.i("Permission denied.")
                showSnackbar(
                    this,
                    R.string.permission_denied_explanation,
                    R.string.settings,
                    Snackbar.LENGTH_INDEFINITE,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}