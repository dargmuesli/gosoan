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

class Navigation constructor(private val activity: AppCompatActivity) {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NavigationView>
    private lateinit var scrim: View

    fun onCreate() {
        activity.setContentView(R.layout.activity_main)

        val navigationController =
            (activity.supportFragmentManager.findFragmentById(R.id.fragment_nav_host) as NavHostFragment).navController
        val navigationView = activity.findViewById<NavigationView>(R.id.nav_view)
        val bottomAppBar = activity.findViewById<BottomAppBar>(R.id.bottomAppBar)

        scrim = activity.findViewById(R.id.scrim)
        appBarConfiguration = AppBarConfiguration(navigationController.graph)
        bottomSheetBehavior = BottomSheetBehavior.from(navigationView)
        bottomSheetHide()

        setupActionBarWithNavController(activity, navigationController, appBarConfiguration)
        navigationView.setupWithNavController(navigationController)
        bottomAppBar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetShow()
            } else {
                bottomSheetHide()
            }
        }
        scrim.setOnClickListener {
            bottomSheetHide()
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val baseColor = Color.BLACK
                val baseAlpha = ResourcesCompat.getFloat(activity.resources, R.dimen.material_emphasis_medium)
                val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
                val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
                val color = Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
                scrim.setBackgroundColor(color)
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetHide()
                }
            }
        })
    }

    fun onSupportNavigateUp(): Boolean {
        bottomSheetHide()
        val navController = activity.findNavController(R.id.fragment_nav_host)
        return navController.navigateUp(appBarConfiguration)
    }

    private fun bottomSheetHide() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        scrim.visibility = View.INVISIBLE
    }

    private fun bottomSheetShow() {
        scrim.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }
}
