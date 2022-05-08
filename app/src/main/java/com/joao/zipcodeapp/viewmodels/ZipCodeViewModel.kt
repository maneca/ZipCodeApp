package com.joao.zipcodeapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.joao.zipcodeapp.domain.repository.ZipCodeRepository
import com.joao.zipcodeapp.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.joao.zipcodeapp.util.CustomExceptions
import com.joao.zipcodeapp.util.Resource
import com.joao.zipcodeapp.util.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZipCodeViewModel @Inject constructor(
    application: Application,
    private val dispatcher: DispatcherProvider,
    private val repository: ZipCodeRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(ZipCodeState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private var searchJob: Job? = null

    init {
        getZipCodes()
    }

    private fun getZipCodes() {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.Loading)
            repository
                .isDatabaseEmpty()
                .flowOn(dispatcher.io())
                .collect { isEmpty ->
                    if (isEmpty) {
                        if(isNetworkAvailable(getApplication<Application?>().applicationContext)){
                            repository.getZipCodesFromNetwork()
                        }else{
                            _eventFlow.emit(UiEvent.NoInternetConnection)
                        }

                    } else {
                        getZipCodesFromLocalDatabase()
                    }
                }
        }
    }

    private fun getZipCodesFromLocalDatabase() {
        viewModelScope.launch {
            repository
                .getZipCodesFromLocalDatabase()
                .flowOn(dispatcher.io())
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                zipCodes = result.data ?: emptyList(),
                                exception = null
                            )
                            _eventFlow.emit(UiEvent.ZipCodesLoaded)
                        }
                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                zipCodes = result.data ?: emptyList(),
                                exception = result.exception ?: CustomExceptions.UnknownException,
                            )
                            _eventFlow.emit(UiEvent.Failed)
                        }
                    }

                }
        }
    }

    fun populateDatabase() {
        viewModelScope.launch {
            repository
                .populateDatabase()
                .flowOn(dispatcher.io())
                .collect{
                    if(it){
                        getZipCodesFromLocalDatabase()
                    }
                }
        }
    }

    fun searchZipCode(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if(query.isEmpty() || query.isBlank()){
            getZipCodesFromLocalDatabase()
        }else{
            searchJob = viewModelScope.launch {
                delay(500L)
                repository
                    .searchZipCode(sanitizeSearchQuery(query))
                    .flowOn(dispatcher.io())
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _state.value = state.value.copy(
                                    zipCodes = result.data ?: emptyList(),
                                    exception = null
                                )
                            }
                            is Resource.Error -> {
                                _state.value = state.value.copy(
                                    zipCodes = result.data ?: emptyList(),
                                    exception = result.exception ?: CustomExceptions.UnknownException,
                                )
                                _eventFlow.emit(UiEvent.Failed)
                            }
                        }
                    }
            }
        }
    }

    private fun sanitizeSearchQuery(query: String): String {
        val strings = query.trim().split(" ")
        val stringsEscaped = strings.map {
            val queryWithEscapedQuotes = it
                .replace("-", "")
                .replace(Regex.fromLiteral("\""), "\"\"")
            "*$queryWithEscapedQuotes*"
        }

        return stringsEscaped.joinToString(separator = " OR ")
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}