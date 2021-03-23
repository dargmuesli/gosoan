package de.jonas_thelemann.uni.gosoan.ui.sensor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.R
import de.jonas_thelemann.uni.gosoan.databinding.FragmentSensorBinding

@AndroidEntryPoint
class SensorFragment : Fragment(R.layout.fragment_sensor) {
    private var _binding: FragmentSensorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SensorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)

        viewModel.data.observe(viewLifecycleOwner, {
            binding.label.text = it.toString()
        })
    }
}
