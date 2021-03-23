package de.jonas_thelemann.uni.gosoan.repository

import android.hardware.Sensor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import de.jonas_thelemann.uni.gosoan.service.SensorService
import javax.inject.Inject

class SensorRepository @Inject constructor(private val sensorService: SensorService) {
    sealed class State {
        object Empty : State()
        object Refreshing : State()
        object Done : State()
        class Error(val exception: Exception) : State()
    }

    private val _state = MutableLiveData<State>().apply {
        postValue(State.Empty)
    }
    val state: LiveData<State> = _state

    private val _sensors = MutableLiveData<List<Sensor>>()
    val sensors: LiveData<List<Sensor>> = _sensors

    val sensorData: LiveData<GosoanSensorEvent> = sensorService

    fun fetchSensors() {
        try {
            _state.postValue(State.Refreshing)
            val sensors = sensorService.getSensors()
            _sensors.postValue(sensors)

            if (sensors.isEmpty()) {
                _state.postValue(State.Empty)
            } else {
                _state.postValue(State.Done)
            }
        } catch (exception: Exception) {
            _state.postValue(State.Error(exception))
        }
    }
}