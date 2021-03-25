package de.jonas_thelemann.uni.gosoan.ui.preference

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.preference.*
import de.jonas_thelemann.uni.gosoan.model.SensorId

class PreferenceFragment(private val sensorId: SensorId?) : PreferenceFragmentCompat() {
    private val prefix = sensorId?.toString() ?: "global"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val category = PreferenceCategory(context)
        screen.addPreference(category)
        category.title = sensorId?.name ?: getResourceString("global")

        val togglePreference = SwitchPreferenceCompat(context)
        togglePreference.icon = getResourceDrawable("ic_baseline_sensors_24")
        togglePreference.key = prefix + "_toggle"
        togglePreference.title = getResourceString("toggle_measurements")
        togglePreference.setDefaultValue(true)
        category.addPreference(togglePreference)

        val serverIpPreference = EditTextPreference(context)
        serverIpPreference.icon = getResourceDrawable("ic_baseline_cloud_24")
        serverIpPreference.key = prefix + "_server_ip"
        serverIpPreference.title = getResourceString("server_ip")
        serverIpPreference.setDefaultValue("127.0.0.1")
        serverIpPreference.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        category.addPreference(serverIpPreference)

        val measurementFrequencyPreference = EditTextPreference(context)
        measurementFrequencyPreference.icon = getResourceDrawable("ic_baseline_timer_24")
        measurementFrequencyPreference.key = prefix + "_measurement_frequency"
        measurementFrequencyPreference.title = getResourceString("measurement_frequency")
        measurementFrequencyPreference.summaryProvider =
            EditTextPreference.SimpleSummaryProvider.getInstance()
        measurementFrequencyPreference.setDefaultValue("200000")

        val dataFormatPreference = ListPreference(context)
        dataFormatPreference.entries = getResourceArray("format_entries")
        dataFormatPreference.entryValues = getResourceArray("format_values")
        dataFormatPreference.icon = getResourceDrawable("ic_baseline_text_format_24")
        dataFormatPreference.key = prefix + "_data_format"
        dataFormatPreference.title = getResourceString("data_format")
        dataFormatPreference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        dataFormatPreference.setDefaultValue("flatbuffers")

        val transmissionMethodPreference = ListPreference(context)
        transmissionMethodPreference.entries = getResourceArray("transmission_entries")
        transmissionMethodPreference.entryValues = getResourceArray("transmission_values")
        transmissionMethodPreference.icon = getResourceDrawable("ic_baseline_import_export_24")
        transmissionMethodPreference.key = prefix + "_transmission_method"
        transmissionMethodPreference.title = getResourceString("transmission_method")
        transmissionMethodPreference.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()
        transmissionMethodPreference.setDefaultValue("websocket")

        category.addPreference(measurementFrequencyPreference)
        category.addPreference(dataFormatPreference)
        category.addPreference(transmissionMethodPreference)

        preferenceScreen = screen

        serverIpPreference.dependency = prefix + "_toggle"
        measurementFrequencyPreference.dependency = prefix + "_toggle"
        dataFormatPreference.dependency = prefix + "_toggle"
        transmissionMethodPreference.dependency = prefix + "_toggle"


//        setPreferencesFromResource(R.xml.root_preferences, rootKey)

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