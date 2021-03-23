package de.jonas_thelemann.uni.gosoan.ui

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import de.jonas_thelemann.uni.gosoan.repository.SensorRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    sensorRepository: SensorRepository
) : LifecycleObserver, ViewModel() {
    val data: LiveData<GosoanSensorEvent> = sensorRepository.getSensorData()
}