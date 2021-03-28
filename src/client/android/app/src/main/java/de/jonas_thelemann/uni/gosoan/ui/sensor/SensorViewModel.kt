package de.jonas_thelemann.uni.gosoan.ui.sensor

import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jonas_thelemann.uni.gosoan.repository.SensorRepository
import de.jonas_thelemann.uni.gosoan.sourcedLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val sensorRepository: SensorRepository
) : LifecycleObserver, ViewModel() {
    val gosoanSensors = sensorRepository.gosoanSensors

    private val _state = sensorRepository.state
    val refreshing = _state.map { it === SensorRepository.State.Refreshing }

    private val _errorAction: MutableLiveData<Exception> = sourcedLiveData(_state) {
        when (val newState = _state.value) {
            is SensorRepository.State.Error -> newState.exception
            else -> null
        }
    } as MutableLiveData
    val errorAction: LiveData<Exception> = _errorAction

    fun onErrorActionCompleted() {
        _errorAction.postValue(null)
    }

    fun refresh(context: Context) {
        viewModelScope.launch {
            sensorRepository.fetchSensors(context)
        }
    }
}