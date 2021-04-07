package de.jonas_thelemann.uni.gosoan.ui.network_interface

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.jonas_thelemann.uni.gosoan.BuildConfig
import de.jonas_thelemann.uni.gosoan.databinding.CardNetworkInterfaceBinding
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkInterface

class NetworkInterfaceListAdapter :
    ListAdapter<GosoanNetworkInterface, NetworkInterfaceListAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: CardNetworkInterfaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(networkInterface: GosoanNetworkInterface) {
            binding.gosoanNetworkInterface = networkInterface
            binding.queueSizeMax = BuildConfig.QUEUE_SIZE_MAX
            binding.interfaceException.visibility =
                if (networkInterface.exception == null) View.GONE else View.VISIBLE
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GosoanNetworkInterface>() {
        override fun areItemsTheSame(
            oldItem: GosoanNetworkInterface,
            newItem: GosoanNetworkInterface
        ) =
            oldItem.serverUri == newItem.serverUri

        override fun areContentsTheSame(
            oldItem: GosoanNetworkInterface,
            newItem: GosoanNetworkInterface
        ) =
            oldItem.dataSent == newItem.dataSent && oldItem.exception?.message == newItem.exception?.message && oldItem.queue.size == newItem.queue.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardNetworkInterfaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    fun x() {
        notifyDataSetChanged()
    }
}