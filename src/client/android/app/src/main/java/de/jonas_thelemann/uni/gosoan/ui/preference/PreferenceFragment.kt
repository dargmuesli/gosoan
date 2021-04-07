package de.jonas_thelemann.uni.gosoan.ui.preference

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.preference.*
import de.jonas_thelemann.uni.gosoan.BuildConfig
import de.jonas_thelemann.uni.gosoan.MainActivity
import de.jonas_thelemann.uni.gosoan.PreferenceUtil.Companion.getKey
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor
import de.jonas_thelemann.uni.gosoan.service.LocationService
import de.jonas_thelemann.uni.gosoan.service.SensorService

const val PREFERENCE_GLOBAL_ID = "global"
const val PREFERENCE_SENSOR_OVERRIDE_ID = "override_global_preference"
const val PREFERENCE_SENSOR_TOGGLE_ID = "toggle_measurements"
const val PREFERENCE_SENSOR_SERVER_IP_ID = "server_ip"
const val PREFERENCE_SENSOR_MEASUREMENT_FREQUENCY_ID = "measurement_frequency"
const val PREFERENCE_SENSOR_DATA_FORMAT_ID = "data_format"
const val PREFERENCE_SENSOR_TRANSMISSION_METHOD_ID = "transmission_method"

class PreferenceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var gosoanSensor: GosoanSensor? = null
    private lateinit var prefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        val gosoanSensorArg = arguments?.getSerializable("gosoanSensor")

        if (gosoanSensorArg != null) {
            gosoanSensor = gosoanSensorArg as GosoanSensor
        }

        prefix = gosoanSensor?.getId() ?: PREFERENCE_GLOBAL_ID

        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val category = PreferenceCategory(context)
        screen.addPreference(category)
        category.title = gosoanSensor?.name ?: getResourceString(PREFERENCE_GLOBAL_ID)

        if (gosoanSensor != null) {
            val overridePreference = SwitchPreferenceCompat(context)
            overridePreference.icon = getResourceDrawable("ic_baseline_bolt_24")
            overridePreference.key = getKey(prefix, PREFERENCE_SENSOR_OVERRIDE_ID)
            overridePreference.title = getResourceString(PREFERENCE_SENSOR_OVERRIDE_ID)
            overridePreference.setDefaultValue(true)
            category.addPreference(overridePreference)
        }

        val togglePreference = SwitchPreferenceCompat(context)
        togglePreference.icon = getResourceDrawable("ic_baseline_sensors_24")
        togglePreference.key = getKey(prefix, PREFERENCE_SENSOR_TOGGLE_ID)
        togglePreference.title = getResourceString(PREFERENCE_SENSOR_TOGGLE_ID)
        togglePreference.setDefaultValue(gosoanSensor == null)
        category.addPreference(togglePreference)

        val measurementFrequencyPreference = EditTextPreference(context)
        measurementFrequencyPreference.icon = getResourceDrawable("ic_baseline_timer_24")
        measurementFrequencyPreference.key =
            getKey(prefix, PREFERENCE_SENSOR_MEASUREMENT_FREQUENCY_ID)
        measurementFrequencyPreference.title =
            getResourceString(PREFERENCE_SENSOR_MEASUREMENT_FREQUENCY_ID)
        measurementFrequencyPreference.summaryProvider =
            EditTextPreference.SimpleSummaryProvider.getInstance()
        measurementFrequencyPreference.setDefaultValue(BuildConfig.DEFAULT_MEASUREMENT_FREQUENCY.toString())

        val serverIpPreference = EditTextPreference(context)
        serverIpPreference.icon = getResourceDrawable("ic_baseline_cloud_24")
        serverIpPreference.key = getKey(prefix, PREFERENCE_SENSOR_SERVER_IP_ID)
        serverIpPreference.title = getResourceString(PREFERENCE_SENSOR_SERVER_IP_ID)
        serverIpPreference.setDefaultValue(BuildConfig.DEFAULT_SERVER_IP)
        serverIpPreference.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()

        val dataFormatPreference = ListPreference(context)
        dataFormatPreference.entries =
            getResourceArray(PREFERENCE_SENSOR_DATA_FORMAT_ID + "_entries")
        dataFormatPreference.entryValues =
            getResourceArray(PREFERENCE_SENSOR_DATA_FORMAT_ID + "_values")
        dataFormatPreference.icon = getResourceDrawable("ic_baseline_text_format_24")
        dataFormatPreference.key = getKey(prefix, PREFERENCE_SENSOR_DATA_FORMAT_ID)
        dataFormatPreference.title = getResourceString(PREFERENCE_SENSOR_DATA_FORMAT_ID)
        dataFormatPreference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        dataFormatPreference.setDefaultValue(BuildConfig.DEFAULT_DATA_FORMAT)

        val transmissionMethodPreference = ListPreference(context)
        transmissionMethodPreference.entries =
            getResourceArray(PREFERENCE_SENSOR_TRANSMISSION_METHOD_ID + "_entries")
        transmissionMethodPreference.entryValues =
            getResourceArray(PREFERENCE_SENSOR_TRANSMISSION_METHOD_ID + "_values")
        transmissionMethodPreference.icon = getResourceDrawable("ic_baseline_import_export_24")
        transmissionMethodPreference.key = getKey(prefix, PREFERENCE_SENSOR_TRANSMISSION_METHOD_ID)
        transmissionMethodPreference.title =
            getResourceString(PREFERENCE_SENSOR_TRANSMISSION_METHOD_ID)
        transmissionMethodPreference.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()
        transmissionMethodPreference.setDefaultValue(BuildConfig.DEFAULT_TRANSMISSION_METHOD)

        category.addPreference(measurementFrequencyPreference)
        category.addPreference(serverIpPreference)
        category.addPreference(dataFormatPreference)
        category.addPreference(transmissionMethodPreference)

        preferenceScreen = screen

        if (gosoanSensor != null) {
            val overrideGlobalPreferenceKey = getKey(prefix, PREFERENCE_SENSOR_OVERRIDE_ID)

            togglePreference.dependency = overrideGlobalPreferenceKey
            measurementFrequencyPreference.dependency = overrideGlobalPreferenceKey
            serverIpPreference.dependency = overrideGlobalPreferenceKey
            dataFormatPreference.dependency = overrideGlobalPreferenceKey
            transmissionMethodPreference.dependency = overrideGlobalPreferenceKey
        }

        val toggleMeasurementsKey = getKey(prefix, PREFERENCE_SENSOR_TOGGLE_ID)
        measurementFrequencyPreference.dependency = toggleMeasurementsKey
        serverIpPreference.dependency = toggleMeasurementsKey
        dataFormatPreference.dependency = toggleMeasurementsKey
        transmissionMethodPreference.dependency = toggleMeasurementsKey

        val countingPreference: EditTextPreference? =
            findPreference(getKey(prefix, PREFERENCE_SENSOR_MEASUREMENT_FREQUENCY_ID))

        countingPreference?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text

                // TODO: Remove hardcoded string.
                if (TextUtils.isEmpty(text)) {
                    "Not set"
                } else {
                    "$text µs (guide value)"
                }
            }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null) return

        val locationServiceKey = getKey(
            SensorService.getLocationServiceGosoanSensor(requireContext()).getId(),
            PREFERENCE_SENSOR_TOGGLE_ID
        )

        if (key == locationServiceKey && sharedPreferences.getBoolean(
                key,
                false
            ) && !LocationService.checkPermissions(requireContext())
        ) {
            preferenceManager.findPreference<SwitchPreferenceCompat>(locationServiceKey)?.isChecked =
                false
            LocationService.requestPermissions(requireActivity() as MainActivity)
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