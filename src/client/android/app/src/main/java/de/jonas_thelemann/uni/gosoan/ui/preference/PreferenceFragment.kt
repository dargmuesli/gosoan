package de.jonas_thelemann.uni.gosoan.ui.preference

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.preference.*
import de.jonas_thelemann.uni.gosoan.model.SensorId

private const val SENSOR_OVERRIDE_ID = "override_global_preference"
private const val SENSOR_TOGGLE_ID = "toggle_measurements"
private const val SENSOR_SERVER_IP_ID = "server_ip"
private const val SENSOR_MEASUREMENT_FREQUENCY_ID = "measurement_frequency"
private const val SENSOR_DATA_FORMAT_ID = "data_format"
private const val SENSOR_TRANSMISSION_METHOD_ID = "transmission_method"

class PreferenceFragment(private val sensorId: SensorId?) : PreferenceFragmentCompat() {
    private val prefix = sensorId?.toString() ?: "global"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val category = PreferenceCategory(context)
        screen.addPreference(category)
        category.title = sensorId?.name ?: getResourceString("global")

        if (sensorId != null) {
            val overridePreference = SwitchPreferenceCompat(context)
            overridePreference.icon = getResourceDrawable("ic_baseline_bolt_24")
            overridePreference.key = getKey(SENSOR_OVERRIDE_ID, prefix)
            overridePreference.title = getResourceString(SENSOR_OVERRIDE_ID)
            overridePreference.setDefaultValue(false)
            category.addPreference(overridePreference)
        }

        val togglePreference = SwitchPreferenceCompat(context)
        togglePreference.icon = getResourceDrawable("ic_baseline_sensors_24")
        togglePreference.key = getKey(SENSOR_TOGGLE_ID, prefix)
        togglePreference.title = getResourceString(SENSOR_TOGGLE_ID)
        togglePreference.setDefaultValue(false)
        category.addPreference(togglePreference)

        val serverIpPreference = EditTextPreference(context)
        serverIpPreference.icon = getResourceDrawable("ic_baseline_cloud_24")
        serverIpPreference.key = getKey(SENSOR_SERVER_IP_ID, prefix)
        serverIpPreference.title = getResourceString(SENSOR_SERVER_IP_ID)
        serverIpPreference.setDefaultValue("127.0.0.1")
        serverIpPreference.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        category.addPreference(serverIpPreference)

        val measurementFrequencyPreference = EditTextPreference(context)
        measurementFrequencyPreference.icon = getResourceDrawable("ic_baseline_timer_24")
        measurementFrequencyPreference.key = getKey(SENSOR_MEASUREMENT_FREQUENCY_ID, prefix)
        measurementFrequencyPreference.title = getResourceString(SENSOR_MEASUREMENT_FREQUENCY_ID)
        measurementFrequencyPreference.summaryProvider =
            EditTextPreference.SimpleSummaryProvider.getInstance()
        measurementFrequencyPreference.setDefaultValue("200000")

        val dataFormatPreference = ListPreference(context)
        dataFormatPreference.entries = getResourceArray(SENSOR_DATA_FORMAT_ID + "_entries")
        dataFormatPreference.entryValues = getResourceArray(SENSOR_DATA_FORMAT_ID + "_values")
        dataFormatPreference.icon = getResourceDrawable("ic_baseline_text_format_24")
        dataFormatPreference.key = getKey(SENSOR_DATA_FORMAT_ID, prefix)
        dataFormatPreference.title = getResourceString(SENSOR_DATA_FORMAT_ID)
        dataFormatPreference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        dataFormatPreference.setDefaultValue("flatbuffers")

        val transmissionMethodPreference = ListPreference(context)
        transmissionMethodPreference.entries =
            getResourceArray(SENSOR_TRANSMISSION_METHOD_ID + "_entries")
        transmissionMethodPreference.entryValues =
            getResourceArray(SENSOR_TRANSMISSION_METHOD_ID + "_values")
        transmissionMethodPreference.icon = getResourceDrawable("ic_baseline_import_export_24")
        transmissionMethodPreference.key = getKey(SENSOR_TRANSMISSION_METHOD_ID, prefix)
        transmissionMethodPreference.title = getResourceString(SENSOR_TRANSMISSION_METHOD_ID)
        transmissionMethodPreference.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()
        transmissionMethodPreference.setDefaultValue("websocket")

        category.addPreference(measurementFrequencyPreference)
        category.addPreference(dataFormatPreference)
        category.addPreference(transmissionMethodPreference)

        preferenceScreen = screen

        if (sensorId != null) {
            val overrideGlobalPreferenceKey = getKey(SENSOR_OVERRIDE_ID, prefix)

            togglePreference.dependency = overrideGlobalPreferenceKey
            serverIpPreference.dependency = overrideGlobalPreferenceKey
            measurementFrequencyPreference.dependency = overrideGlobalPreferenceKey
            dataFormatPreference.dependency = overrideGlobalPreferenceKey
            transmissionMethodPreference.dependency = overrideGlobalPreferenceKey
        }

        val toggleMeasurementsKey = getKey(SENSOR_TOGGLE_ID, prefix)
        serverIpPreference.dependency = toggleMeasurementsKey
        measurementFrequencyPreference.dependency = toggleMeasurementsKey
        dataFormatPreference.dependency = toggleMeasurementsKey
        transmissionMethodPreference.dependency = toggleMeasurementsKey

        val countingPreference: EditTextPreference? =
            findPreference(prefix + "_measurement_frequency")

        countingPreference?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text)) {
                    "Not set"
                } else {
                    "$text ms"
                }
            }
    }

    private fun getKey(id: String, namespace: String): String {
        return namespace + "_" + id
    }

    private fun getResourceArray(id: String): Array<out String> {
        return resources.getStringArray(resources.getIdentifier(id, "array", context?.packageName))
    }

    private fun getResourceDrawable(id: String): Drawable? {
        return context?.let {
            ContextCompat.getDrawable(
                it,
                resources.getIdentifier(id, "drawable", context?.packageName)
            )
        }
    }

    private fun getResourceString(id: String): String {
        return resources.getString(resources.getIdentifier(id, "string", context?.packageName))
    }
}