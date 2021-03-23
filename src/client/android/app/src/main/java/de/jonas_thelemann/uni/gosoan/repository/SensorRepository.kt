package de.jonas_thelemann.uni.gosoan.repository

import androidx.lifecycle.LiveData
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import de.jonas_thelemann.uni.gosoan.sensor.GosoanSensorEventListener
import javax.inject.Inject

class SensorRepository @Inject constructor(private val gosoanSensorEventListener: GosoanSensorEventListener) {
    fun getSensorData(): LiveData<GosoanSensorEvent> {
        return gosoanSensorEventListener
    }
}