package de.jonas_thelemann.uni.gosoan.ui.network_interface

import android.content.Context
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.internal.managers.ViewComponentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jonas_thelemann.uni.gosoan.R
import de.jonas_thelemann.uni.gosoan.repository.NetworkInterfaceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkInterfaceViewModel @Inject constructor(
    networkInterfaceRepository: NetworkInterfaceRepository
) : LifecycleObserver, ViewModel() {
    val gosoanNetworkInterfaces = networkInterfaceRepository.gosoanNetworkInterfaces

    private val _refreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean> = _refreshing

    fun refresh(context: Context) {
        viewModelScope.launch {
            _refreshing.postValue(true)
            ((context as ViewComponentManager.FragmentContextWrapper).fragment.view?.findViewById(R.id.recycler_view) as RecyclerView).adapter?.notifyDataSetChanged()
            _refreshing.postValue(false)
        }
    }
}