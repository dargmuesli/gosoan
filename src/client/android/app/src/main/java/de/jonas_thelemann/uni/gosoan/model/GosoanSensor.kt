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
    companion object {
        fun getId(name: String, type: Int): String {
            return type.toString() + "|" + name.replace("""\s""".toRegex(), "_")
        }
    }

    fun getId(): String {
        return getId(name, type)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GosoanSensor

        if (name != other.name) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type
        return result
    }
}
