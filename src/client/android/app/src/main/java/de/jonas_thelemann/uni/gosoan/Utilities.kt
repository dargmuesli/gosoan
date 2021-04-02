package de.jonas_thelemann.uni.gosoan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.flatbuffers.FlatBufferBuilder
import de.jonas_thelemann.uni.gosoan.generated.GosoanSensorEvent
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_SENSOR_MEASUREMENT_FREQUENCY_ID


fun <T> sourcedLiveData(vararg sources: LiveData<*>, block: () -> T?): LiveData<T> =
    MediatorLiveData<T>().apply {
        sources.forEach { source ->
            addSource(source) {
                val oldValue = value
                val newValue = block()
                if (oldValue != newValue) value = block()
            }
        }
    }

class PreferenceUtil {
    companion object {
        fun getKey(namespace: String, id: String): String {
            return "$namespace|$id"
        }

        fun getPreferenceMeasurementFrequency(
            sharedPreferences: SharedPreferences,
            namespace: String
        ): Int {
            return sharedPreferences.getString(
                getKey(namespace, PREFERENCE_SENSOR_MEASUREMENT_FREQUENCY_ID),
                SensorManager.SENSOR_DELAY_NORMAL.toString()
            )?.toInt() ?: SensorManager.SENSOR_DELAY_NORMAL
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(context: Context, channelId: String, channelName: String): String {
    val chan = NotificationChannel(
        channelId,
        channelName, NotificationManager.IMPORTANCE_NONE
    )
    chan.lightColor = Color.BLUE
    chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(chan)
    return channelId
}

fun showSnackbar(
    context: FragmentActivity,
    snackStrId: Int,
    actionStrId: Int = 0,
    length: Int,
    listener: View.OnClickListener? = null
) {
    val snackbar = Snackbar.make(
        context.findViewById(android.R.id.content), context.getString(snackStrId),
        length
    )
    if (actionStrId != 0 && listener != null) {
        snackbar.setAction(context.getString(actionStrId), listener)
    }
    snackbar.show()
}

@ExperimentalUnsignedTypes
fun getGosoanSensorEventAsByteArray(
    sensorType: Int,
    sensorName: String,
    values: FloatArray,
    accuracy: Int,
    timestamp: Long
): ByteArray {
    val fb = FlatBufferBuilder(128)
    val sensorNameOffset = fb.createString(sensorName)
    val valuesOffset = GosoanSensorEvent.createValuesVector(fb, values)
    val gosoanSensorEventOffset = GosoanSensorEvent.createGosoanSensorEvent(
        fb,
        sensorType,
        sensorNameOffset,
        valuesOffset,
        accuracy,
        timestamp
    )
    fb.finish(gosoanSensorEventOffset)
    return fb.sizedByteArray()
}
