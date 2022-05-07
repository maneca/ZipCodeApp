package com.joao.zipcodeapp.domain.repository

import com.joao.zipcodeapp.domain.data.ZipCode
import com.joao.zipcodeapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface ZipCodeRepository {
    fun getZipCodesFromLocalDatabase(): Flow<Resource<List<ZipCode>>>

    fun getZipCodesFromNetwork()

    fun populateDatabase(): Flow<Boolean>

    fun isDatabaseEmpty(): Flow<Boolean>

    fun searchZipCode(text: String): Flow<Resource<List<ZipCode>>>
}