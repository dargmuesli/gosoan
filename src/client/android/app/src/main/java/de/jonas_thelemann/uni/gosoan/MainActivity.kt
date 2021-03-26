package de.jonas_thelemann.uni.gosoan

import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.navigation.GosoanNavigation
import de.jonas_thelemann.uni.gosoan.repository.SensorRepository
import de.jonas_thelemann.uni.gosoan.service.SensorService
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var sensorService: SensorService

    @Inject
    lateinit var sensorRepository: SensorRepository

    val gosoanNavigation: GosoanNavigation = GosoanNavigation(this)

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener {
            sharedPreferences: SharedPreferences?, key: String? -> sensorService.restart()
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        gosoanNavigation.onCreateActivity()
        sensorService.onCreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString(STATE_SEARCH_QUERY, searchQuery)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    override fun onPause() {
        super.onPause()

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }


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