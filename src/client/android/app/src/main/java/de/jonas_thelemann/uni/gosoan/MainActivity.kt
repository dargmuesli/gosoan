package de.jonas_thelemann.uni.gosoan

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.navigation.GosoanNavigation
import de.jonas_thelemann.uni.gosoan.repository.SensorRepository
import de.jonas_thelemann.uni.gosoan.service.SensorService
import de.jonas_thelemann.uni.gosoan.ui.preference.PreferenceFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var sensorService: SensorService

    @Inject
    lateinit var sensorRepository: SensorRepository

    val gosoanNavigation: GosoanNavigation = GosoanNavigation(this)

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

        gosoanNavigation.onCreateActivity()
        sensorService.onCreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString(STATE_SEARCH_QUERY, searchQuery)
        }

        super.onSaveInstanceState(outState)
    }

    // vs onCreate: would have sensor always sending data
    // vs onResume: As of Android 7.0 (API 24), apps can run in multi-window mode (split-screen or picture-in-picture mode). Apps running in this mode are paused, but still visible on screen. Use onStart() and onStop() to ensure that sensors continue running even if the app is in multi-window mode.
    // SENSOR_DELAY_NORMAL indicates how often onSensorChanged is called (not actual data change, and only suggested, potentially faster)
    // normal: 200,000 microseconds
    // game: 20.000 microseconds
    // ui: 60k
    // fastest: 0
    // since Android 3.0 (API Level 11): number supplyable
    // If for some reason you do need to change the delay, you will have to unregister and reregister the sensor listener.
    override fun onStart() {
        super.onStart()

        sensorService.onStart()
    }

    override fun onStop() {
        super.onStop()

        sensorService.onStop()
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
                sensorRepository.fetchSensors(query)
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
}