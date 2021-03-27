package de.jonas_thelemann.uni.gosoan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_GLOBAL_ID
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_SENSOR_OVERRIDE_ID
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_SENSOR_TOGGLE_ID


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

        fun getPreferenceOverride(
            sharedPreferences: SharedPreferences,
            namespace: String
        ): Boolean {
            return sharedPreferences.getBoolean(
                getKey(namespace, PREFERENCE_SENSOR_OVERRIDE_ID),
                true
            )
        }

        fun getPreferenceToggle(sharedPreferences: SharedPreferences, namespace: String): Boolean {
            return sharedPreferences.getBoolean(
                getKey(namespace, PREFERENCE_SENSOR_TOGGLE_ID),
                namespace == PREFERENCE_GLOBAL_ID
            )
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