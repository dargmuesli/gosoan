package de.jonas_thelemann.uni.gosoan.ui.sensor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.jonas_thelemann.uni.gosoan.databinding.CardSensorBinding
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor
import de.jonas_thelemann.uni.gosoan.ui.OnClickListener

class SensorListAdapter(private val onClickListener: OnClickListener<GosoanSensor>) :
    ListAdapter<GosoanSensor, SensorListAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: CardSensorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(gosoanSensor: GosoanSensor) {
            binding.infoText.text = gosoanSensor.name
            binding.gosoanSensor = gosoanSensor
            binding.onClickListener = onClickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GosoanSensor>() {
        override fun areItemsTheSame(oldItem: GosoanSensor, newItem: GosoanSensor) =
            (oldItem.type.toString() + oldItem.name) == newItem.type.toString() + newItem.name

        override fun areContentsTheSame(oldItem: GosoanSensor, newItem: GosoanSensor) =
            oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardSensorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}