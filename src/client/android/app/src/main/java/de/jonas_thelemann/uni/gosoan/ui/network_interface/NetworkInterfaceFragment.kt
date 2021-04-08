package de.jonas_thelemann.uni.gosoan.ui.network_interface

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.MainActivity
import de.jonas_thelemann.uni.gosoan.R
import de.jonas_thelemann.uni.gosoan.databinding.FragmentNetworkInterfaceBinding

private const val REFRESH_DELAY = 1000L

@AndroidEntryPoint
class NetworkInterfaceFragment : Fragment(R.layout.fragment_network_interface) {
    private val viewModel: NetworkInterfaceViewModel by viewModels()
    private val refreshTask = object : Runnable {
        override fun run() {
            viewModel.refresh(requireContext())
            handler.postDelayed(this, REFRESH_DELAY)
        }
    }

    lateinit var binding: FragmentNetworkInterfaceBinding

    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun onStart() {
        super.onStart()
        handler.postDelayed(refreshTask, REFRESH_DELAY)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(refreshTask)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNetworkInterfaceBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = NetworkInterfaceListAdapter()
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).gosoanNavigation.onCreateFragment(view)

        lifecycle.addObserver(viewModel)
    }
}
