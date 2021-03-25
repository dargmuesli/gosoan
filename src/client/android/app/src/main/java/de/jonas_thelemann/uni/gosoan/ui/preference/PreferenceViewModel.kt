package de.jonas_thelemann.uni.gosoan.ui.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.jonas_thelemann.uni.gosoan.model.SensorId

class PreferenceViewModel(val sensorId: SensorId?) : ViewModel() {
    class Factory(
        private val sensorId: SensorId?
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PreferenceViewModel(sensorId) as T
    }
}
