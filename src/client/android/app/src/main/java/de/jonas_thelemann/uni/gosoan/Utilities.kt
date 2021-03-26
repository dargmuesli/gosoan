package de.jonas_thelemann.uni.gosoan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData


fun <T> sourcedLiveData(vararg sources: LiveData<*>, block: () -> T?): LiveData<T> =
    MediatorLiveData<T>().apply {
        sources.forEach { source ->
            addSource(source) {
                val oldValue = value
                val newValue = block()
                if (oldValue != newValue) value = block()
            }
        }
    }

class PreferenceUtil {
    companion object {
        fun getKey(namespace: String, id: String): String {
            return namespace + "_" + id
        }
    }
}