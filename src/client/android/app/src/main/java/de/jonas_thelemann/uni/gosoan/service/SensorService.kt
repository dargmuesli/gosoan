package de.jonas_thelemann.uni.gosoan.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import de.jonas_thelemann.uni.gosoan.*
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorService @Inject constructor() : SensorEventListener, Service() {
    companion object {
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

        fun getSensorMap(context: Context): MutableMap<Int, Sensor> {
            val sensorMap: MutableMap<Int, Sensor> = mutableMapOf()
            val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

            for (sensorApiPair in sensorApiMap) {
                if (sensorApiPair.value > BuildConfig.MIN_SDK_VERSION) continue

                val sensorNumber = sensorApiPair.key

                if (sensorManager.getSensorList(sensorNumber).size > 1) {
                    Timber.w("Warning: Multiple sensors found for sensor number $sensorNumber and only the default sensor is used now!")
                }

                val sensor = sensorManager.getDefaultSensor(sensorNumber)

                if (sensor == null) {
                    Timber.w("No sensor found for sensor number ${sensorNumber}!")
                    continue
                }

                sensorMap[sensorNumber] = sensor
            }

            return sensorMap
        }

        private fun isCreatable(context: Context): Boolean {
            val sensorMap = getSensorMap(context)

            for (sensorMapEntry in sensorMap) {
                val sensor = sensorMapEntry.value
                val gosoanSensor = GosoanSensor(context, sensor.name, sensor.type)

                if (gosoanSensor.isActive()) {
                    return true
                }
            }

            return false
        }

        fun start(context: Context) {
            if (isCreatable(context)) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, SensorService::class.java)
                )
            }
        }

        private fun stop(context: Context) {
            context.stopService(Intent(context, SensorService::class.java))
        }

        fun restart(context: Context) {
            stop(context)
            start(context)
        }
    }

//    override fun onCreate() {
//        super.onCreate()
//
//        Timber.i("Service Started.")
//    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.i("Received start id %s: %s", startId, intent)

        val pendingIntent: PendingIntent =
            Intent(this, SensorService::class.java).let {
                PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
            }

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    this,
                    getString(R.string.NOTIFICATION_CHANNEL_ID),
                    getString(R.string.NOTIFICATION_CHANNEL_NAME)
                )
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        @Suppress("DEPRECATION") // Handled by the if-statement.
        val notification: Notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.running))
                .setSmallIcon(R.drawable.ic_baseline_sensors_24)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_LOW)
                .build()

        startForeground(R.string.NOTIFICATION_ID, notification)

        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        for (sensorMapEntry in getSensorMap(this)) {
            val gosoanSensor =
                GosoanSensor(this, sensorMapEntry.value.name, sensorMapEntry.value.type)

            if (!gosoanSensor.isActive()) continue
            val measurementFrequency =
                PreferenceUtil.getPreferenceMeasurementFrequency(
                    sharedPreferences,
                    gosoanSensor.getId()
                )

            Timber.i(
                "Registering listener for sensor %s with delay %s.",
                sensorMapEntry.value.name,
                measurementFrequency
            )
            sensorManager.registerListener(
                this, sensorMapEntry.value,
                measurementFrequency
            )
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This method is willingly unused.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("Unregistering listeners.")
        (getSystemService(SENSOR_SERVICE) as SensorManager).unregisterListener(this)
        Timber.i("Service Stopped.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        println(
            GosoanSensorEvent(
                event.accuracy,
                event.sensor.name,
                event.timestamp,
                event.sensor.type,
                event.values
            )
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // This method is willingly unused.
    }

//    private fun destroy() {
//        stopForeground(true)
//        stopSelf()
//    }

    fun getSensors(context: Context, query: String = ""): List<Sensor> {
        val sensors: MutableList<Sensor> = mutableListOf()

        for (sensorMapEntry in getSensorMap(context)) {
            sensors.add(sensorMapEntry.value)
        }

        // sensorManager.getSensorList(Sensor.TYPE_ALL)
        return sensors.filter { it.name.contains(query) }
    }
}