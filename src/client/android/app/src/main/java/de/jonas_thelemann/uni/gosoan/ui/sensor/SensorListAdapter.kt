package de.jonas_thelemann.uni.gosoan.ui.sensor

import android.content.Context
import android.hardware.Sensor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.jonas_thelemann.uni.gosoan.databinding.CardSensorBinding
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor
import de.jonas_thelemann.uni.gosoan.ui.OnClickListener

class SensorListAdapter(private val onClickListener: OnClickListener<GosoanSensor>) :
    ListAdapter<Sensor, SensorListAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: CardSensorBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sensor: Sensor) {
            binding.infoText.text = sensor.name
            binding.sensor = GosoanSensor(context, sensor.name, sensor.type)
            binding.onClickListener = onClickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Sensor>() {
        override fun areItemsTheSame(oldItem: Sensor, newItem: Sensor) =
            (oldItem.type.toString() + oldItem.name) == newItem.type.toString() + newItem.name

        override fun areContentsTheSame(oldItem: Sensor, newItem: Sensor) =
            oldItem.name.equals(newItem.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardSensorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            parent.context
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}