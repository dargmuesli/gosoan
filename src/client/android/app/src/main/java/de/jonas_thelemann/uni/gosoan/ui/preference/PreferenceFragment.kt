package de.jonas_thelemann.uni.gosoan.ui.preference

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.jonas_thelemann.uni.gosoan.R

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}