package de.jonas_thelemann.uni.gosoan.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import de.jonas_thelemann.uni.gosoan.BuildConfig
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import javax.inject.Inject
import javax.inject.Singleton

private const val SENSOR_DELAY_DEFAULT = SensorManager.SENSOR_DELAY_NORMAL

@Singleton
class GosoanSensorEventListener @Inject constructor(@ApplicationContext private val context: Context) :
    SensorEventListener, LiveData<GosoanSensorEvent>() {
    private lateinit var sensorManager: SensorManager // System services not available to Activities before onCreate().
    private val sensorMap: MutableMap<Int, Sensor> = mutableMapOf()
    @SuppressLint("InlinedApi")
    private val sensorApiMap: Map<Int, Int> = mapOf(
        Sensor.TYPE_ACCELEROMETER to 3,
        Sensor.TYPE_ACCELEROMETER_UNCALIBRATED to 26,
//        Sensor.TYPE_ALL, // Not an actual sensor!
        Sensor.TYPE_AMBIENT_TEMPERATURE to 14,
//        Sensor.TYPE_DEVICE_PRIVATE_BASE to 24, // Not an actual sensor!
        Sensor.TYPE_GAME_ROTATION_VECTOR to 18,
        Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR to 19,
        Sensor.TYPE_GRAVITY to 9,
        Sensor.TYPE_GYROSCOPE to 3,
        Sensor.TYPE_GYROSCOPE_UNCALIBRATED to 18,
        Sensor.TYPE_HEART_BEAT to 24,
        Sensor.TYPE_HEART_RATE to 20,
        Sensor.TYPE_HINGE_ANGLE to 30,
        Sensor.TYPE_LIGHT to 3,
        Sensor.TYPE_LINEAR_ACCELERATION to 9,
        Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT to 26,
        Sensor.TYPE_MAGNETIC_FIELD to 3,
        Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED to 18,
        Sensor.TYPE_MOTION_DETECT to 24,
        Sensor.TYPE_POSE_6DOF to 24,
        Sensor.TYPE_PRESSURE to 3,
        Sensor.TYPE_PROXIMITY to 3,
        Sensor.TYPE_RELATIVE_HUMIDITY to 14,
        Sensor.TYPE_ROTATION_VECTOR to 9,
//        Sensor.TYPE_SIGNIFICANT_MOTION to 18, // trigger sensor: different implementation logic needed!
        Sensor.TYPE_STATIONARY_DETECT to 24,
        Sensor.TYPE_STEP_COUNTER to 19,
        Sensor.TYPE_STEP_DETECTOR to 19,
    )

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        value = GosoanSensorEvent(
            event.accuracy,
            event.sensor.name,
            event.timestamp,
            event.sensor.type,
            event.values
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // This method is willingly unused.
    }

    fun onStart() {
        for (sensorMapEntry in sensorMap) {
            sensorManager.registerListener(
                this, sensorMapEntry.value,
                SENSOR_DELAY_DEFAULT
            )
        }
    }

    fun onStop() {
        sensorManager.unregisterListener(this)
    }


    fun onCreate() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        for (sensorApiPair in sensorApiMap) {
            if (sensorApiPair.value > BuildConfig.MIN_SDK_VERSION) continue

            val sensorNumber = sensorApiPair.key

            if (sensorManager.getSensorList(sensorNumber).size > 1) {
                println("Warning: Multiple sensors found for sensor number $sensorNumber and only the default sensor is used now!")
            }

            val sensor = sensorManager.getDefaultSensor(sensorNumber)

            if (sensor == null) {
                println("No sensor found for sensor number ${sensorNumber}!")
                continue
            }

            sensorMap[sensorNumber] = sensor
        }
    }
}