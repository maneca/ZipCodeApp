package com.joao.zipcodeapp.data.repository

import android.os.Environment
import android.webkit.URLUtil
import com.joao.zipcodeapp.data.local.ZipCodeDao
import com.joao.zipcodeapp.domain.data.ZipCode
import com.joao.zipcodeapp.domain.remote.ZipCodeApi
import com.joao.zipcodeapp.domain.repository.ZipCodeRepository
import com.joao.zipcodeapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ZipCodeRepositoryImp(
    private val zipCodeDao: ZipCodeDao,
    private val zipCodeApi: ZipCodeApi
): ZipCodeRepository {
    override fun getZipCodes(): Flow<Resource<List<ZipCode>>> = flow {
        emit(Resource.Loading())

        zipCodeApi.downloadZipCodes()

        emit(Resource.Success(listOf(ZipCode())))
    }

    override fun populateDatabase(): Flow<Boolean> = flow {
        val zipCodes = zipCodeApi.readCSV( "codigos_postais.csv")
        zipCodeDao.insertZipCodes(zipCodes)
    }
}