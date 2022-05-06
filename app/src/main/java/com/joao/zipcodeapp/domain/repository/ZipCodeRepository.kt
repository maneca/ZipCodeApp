package com.joao.zipcodeapp.domain.repository

import com.joao.zipcodeapp.domain.data.ZipCode
import com.joao.zipcodeapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface ZipCodeRepository {
    fun getZipCodes(): Flow<Resource<List<ZipCode>>>
}