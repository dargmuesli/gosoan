package de.jonas_thelemann.uni.gosoan.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor
import de.jonas_thelemann.uni.gosoan.service.SensorService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    private val _gosoanSensors = MutableLiveData<List<GosoanSensor>>()
    val gosoanSensors: LiveData<List<GosoanSensor>> = _gosoanSensors

    fun fetchSensors(context: Context, query: String = "") {
        try {
            _state.postValue(State.Refreshing)
            val sensors = sensorService.getSensors(context, query)
            _gosoanSensors.postValue(sensors)

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