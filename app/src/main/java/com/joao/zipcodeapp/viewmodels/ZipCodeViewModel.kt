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
) : ViewModel() {

    private val _state = MutableStateFlow(ZipCodeState())
    val state = _state.asStateFlow()

    init {
        getZipCodes()
    }

    private fun getZipCodes() {
        viewModelScope.launch {
            repository
                .isDatabaseEmpty()
                .flowOn(dispatcher.io())
                .collect {
                    if (it) {
                        repository.getZipCodesFromNetwork()
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
                        }
                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                zipCodes = result.data ?: emptyList(),
                                exception = result.exception ?: CustomExceptions.UnknownException,
                            )
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
                .collect()
        }
    }

    fun searchZipCode(query: String) {
        viewModelScope.launch {
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
                        }
                    }
                }
        }
    }

    private fun sanitizeSearchQuery(query: String): String {
        val strings = query.split(" ")
        val stringsEscaped = strings.map {
            val queryWithEscapedQuotes = it.replace(Regex.fromLiteral("\""), "\"\"")
            "*$queryWithEscapedQuotes*"
        }
        //val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return stringsEscaped.joinToString(separator = " OR ")
    }
}