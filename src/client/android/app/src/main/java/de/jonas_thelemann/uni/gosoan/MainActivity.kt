package de.jonas_thelemann.uni.gosoan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.navigation.GosoanNavigation
import de.jonas_thelemann.uni.gosoan.sensor.GosoanSensorEventListener
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var gosoanSensorEventListener: GosoanSensorEventListener
    private val gosoanNavigation: GosoanNavigation = GosoanNavigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gosoanNavigation.setupNavigation()
        gosoanSensorEventListener.setupSensors()
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

        gosoanSensorEventListener.onStart()
    }

    override fun onStop() {
        super.onStop()

        gosoanSensorEventListener.onStop()
    }
}