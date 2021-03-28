package de.jonas_thelemann.uni.gosoan.model

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import de.jonas_thelemann.uni.gosoan.PreferenceUtil.Companion.getKey
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_GLOBAL_ID
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_SENSOR_OVERRIDE_ID
import de.jonas_thelemann.uni.gosoan.ui.preference.PREFERENCE_SENSOR_TOGGLE_ID
import java.io.Serializable

data class GosoanSensor constructor(
    val name: String,
    val type: Int
) : Serializable {
    fun getId(): String {
        return type.toString() + "|" + name.replace("""\s""".toRegex(), "_")
    }

    fun isActive(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        val preferenceGlobalToggle = sharedPreferences.getBoolean(
            getKey(PREFERENCE_GLOBAL_ID, PREFERENCE_SENSOR_TOGGLE_ID),
            true
        )

        return if (sharedPreferences.getBoolean(
                getKey(getId(), PREFERENCE_SENSOR_OVERRIDE_ID),
                true
            )
        ) sharedPreferences.getBoolean(
            getKey(getId(), PREFERENCE_SENSOR_TOGGLE_ID), false
        ) else preferenceGlobalToggle
    }
}
