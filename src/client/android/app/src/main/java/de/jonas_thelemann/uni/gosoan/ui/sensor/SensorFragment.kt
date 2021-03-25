package de.jonas_thelemann.uni.gosoan.ui.sensor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.jonas_thelemann.uni.gosoan.MainActivity
import de.jonas_thelemann.uni.gosoan.R
import de.jonas_thelemann.uni.gosoan.databinding.FragmentSensorBinding
import de.jonas_thelemann.uni.gosoan.ui.OnClickListener

@AndroidEntryPoint
class SensorFragment : Fragment(R.layout.fragment_sensor) {
    private val viewModel: SensorViewModel by viewModels()

    lateinit var binding: FragmentSensorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.errorAction.observe(viewLifecycleOwner, { exception: Exception? ->
            exception?.let {
                Snackbar.make(
                    requireView(),
                    R.string.unable_to_fetch_sensors,
                    Snackbar.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
                viewModel.onErrorActionCompleted()
            }
        })

        binding = FragmentSensorBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = SensorListAdapter(OnClickListener { sensorId ->
            val action = SensorFragmentDirections.actionNavigationSensorToNavigationPreference(
                sensorId
            )
            findNavController().navigate(action)
        })
        binding.lifecycleOwner = this
        viewModel.refresh()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).gosoanNavigation.onCreateFragment(view)

        lifecycle.addObserver(viewModel)

        viewModel.sensors.observe(viewLifecycleOwner, {
            println(it)
        })

        viewModel.sensorData.observe(viewLifecycleOwner, {
//            binding.
        })
    }
}
