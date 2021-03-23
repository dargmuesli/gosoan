package de.jonas_thelemann.uni.gosoan.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.jonas_thelemann.uni.gosoan.R

class GosoanNavigation constructor(private val activity: AppCompatActivity) {
    fun setupNavigation() {
        activity.setContentView(R.layout.activity_main)

        val navController =
            (activity.supportFragmentManager.findFragmentById(R.id.fragment_nav_host) as NavHostFragment).navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_main
            )
        )
        val navView: BottomNavigationView = activity.findViewById(R.id.nav_view)

        setupActionBarWithNavController(activity, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
