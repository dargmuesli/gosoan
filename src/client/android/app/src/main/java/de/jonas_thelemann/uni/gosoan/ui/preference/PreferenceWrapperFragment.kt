package de.jonas_thelemann.uni.gosoan.ui.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import de.jonas_thelemann.uni.gosoan.R
import de.jonas_thelemann.uni.gosoan.databinding.FragmentPreferenceBinding

class PreferenceWrapperFragment : Fragment() {

    private val viewModel: PreferenceViewModel by viewModels {
        val args = PreferenceWrapperFragmentArgs.fromBundle(requireArguments())

        PreferenceViewModel.Factory(
            gosoanSensor = args.sensor
        )
    }

    private lateinit var binding: FragmentPreferenceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferenceBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (activity != null) {
                val preferenceFragment = PreferenceFragment()
            val args = Bundle()
            args.putSerializable("gosoanSensor", viewModel.gosoanSensor)
            preferenceFragment.arguments = args
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_preference_content,
                    preferenceFragment
                )
                .commit()
        }

        return binding.root
    }
}