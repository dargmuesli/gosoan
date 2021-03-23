package de.jonas_thelemann.uni.gosoan.navigation

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.math.MathUtils
import com.google.android.material.navigation.NavigationView
import de.jonas_thelemann.uni.gosoan.R

class GosoanNavigation constructor(private val activity: AppCompatActivity) {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NavigationView>

    fun onCreate() {
        activity.setContentView(R.layout.activity_main)

        val scrim = activity.findViewById<View>(R.id.scrim)
        val navigationController =
            (activity.supportFragmentManager.findFragmentById(R.id.fragment_nav_host) as NavHostFragment).navController
        val navigationView = activity.findViewById<NavigationView>(R.id.nav_view)
        val bottomAppBar = activity.findViewById<BottomAppBar>(R.id.bottomAppBar)

        appBarConfiguration = AppBarConfiguration(navigationController.graph)
        bottomSheetBehavior = BottomSheetBehavior.from(navigationView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        setupActionBarWithNavController(activity, navigationController, appBarConfiguration)
        navigationView.setupWithNavController(navigationController)
        bottomAppBar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        scrim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val baseColor = Color.BLACK
                // 60% opacity
                val baseAlpha = ResourcesCompat.getFloat(activity.resources, R.dimen.material_emphasis_medium)
                // Map slideOffset from [-1.0, 1.0] to [0.0, 1.0]
                val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
                val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
                val color = Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
                scrim.setBackgroundColor(color)
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }

    fun onSupportNavigateUp(): Boolean {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val navController = activity.findNavController(R.id.fragment_nav_host)
        return navController.navigateUp(appBarConfiguration)
    }
}
