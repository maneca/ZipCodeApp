package com.joao.zipcodeapp.viewmodels

import androidx.lifecycle.ViewModel
import com.joao.zipcodeapp.domain.repository.ZipCodeRepository
import com.joao.zipcodeapp.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.joao.zipcodeapp.util.CustomExceptions
import com.joao.zipcodeapp.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZipCodeViewModel @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val repository: ZipCodeRepository
): ViewModel() {

    private val _state = MutableStateFlow(ZipCodeState())
    val state = _state.asStateFlow()

    init {
        getZipCodes()
    }

    private fun getZipCodes(){
        viewModelScope.launch {
            repository
                .getZipCodes()
                .flowOn(dispatcher.io())
                .collect{result ->
                    when(result){
                        is Resource.Success ->{
                            _state.value = state.value.copy(
                                newsItems = result.data ?: emptyList(),
                                exception = null,
                                loading = false
                            )
                        }
                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                newsItems = result.data ?: emptyList(),
                                exception = result.exception ?: CustomExceptions.UnknownException,
                                loading = false
                            )
                        }
                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                newsItems = result.data ?: emptyList(),
                                exception = null,
                                loading = true
                            )
                        }
                    }

                }
        }
    }
}