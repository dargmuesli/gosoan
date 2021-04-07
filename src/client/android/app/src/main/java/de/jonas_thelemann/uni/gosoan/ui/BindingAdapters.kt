package de.jonas_thelemann.uni.gosoan.ui

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkInterface
import de.jonas_thelemann.uni.gosoan.ui.network_interface.NetworkInterfaceListAdapter
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

@BindingAdapter("gosoanNetworkInterfaces")
fun RecyclerView.bindNetworkInterfaces(gosoanNetworkInterface: List<GosoanNetworkInterface>?) {
    val adapter = adapter as NetworkInterfaceListAdapter
    adapter.submitList(gosoanNetworkInterface)
}

@BindingAdapter("gosoanSensors")
fun RecyclerView.bindSensors(gosoanSensors: List<GosoanSensor>?) {
    val adapter = adapter as SensorListAdapter
    adapter.submitList(gosoanSensors)
}