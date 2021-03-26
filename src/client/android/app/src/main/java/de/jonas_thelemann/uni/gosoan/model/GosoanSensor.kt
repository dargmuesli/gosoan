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
    private val context: Context,
    val name: String,
    val type: Int
) : Serializable {
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun getId(): String {
        return type.toString() + "_" + name.replace("""\s""".toRegex(), "-")
    }

    fun isActive(): Boolean {
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
