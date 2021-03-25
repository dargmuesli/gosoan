package de.jonas_thelemann.uni.gosoan.ui

import android.hardware.Sensor
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jonas_thelemann.uni.gosoan.ui.sensor.SensorListAdapter

@BindingAdapter("visible")
fun View.bindVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.bindRefreshListener(listener: Runnable) {
    setOnRefreshListener {
        listener.run()
    }
}

@BindingAdapter("sensors")
fun RecyclerView.bindSensors(sensors: List<Sensor>?) {
    val adapter = adapter as SensorListAdapter
    adapter.submitList(sensors)
}