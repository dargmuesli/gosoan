package de.jonas_thelemann.uni.gosoan.ui.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor

class PreferenceViewModel(val gosoanSensor: GosoanSensor?) : ViewModel() {
    class Factory(
        private val gosoanSensor: GosoanSensor?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PreferenceViewModel(gosoanSensor) as T
    }
}
