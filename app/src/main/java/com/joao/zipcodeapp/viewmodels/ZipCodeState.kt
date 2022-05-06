package com.joao.zipcodeapp.viewmodels

import com.joao.zipcodeapp.domain.data.ZipCode
import com.joao.zipcodeapp.util.CustomExceptions

data class ZipCodeState(
    val newsItems: List<ZipCode> = emptyList(),
    val exception: CustomExceptions? = null,
    val loading: Boolean = false
)