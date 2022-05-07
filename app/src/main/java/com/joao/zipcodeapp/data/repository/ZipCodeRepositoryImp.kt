package com.joao.zipcodeapp.data.repository

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
    override fun getZipCodesFromLocalDatabase(): Flow<Resource<List<ZipCode>>> = flow {

        val zipCodes = zipCodeDao.getZipCodes().map { it.toZipCode() }
        emit(Resource.Success(zipCodes))
    }

    override fun getZipCodesFromNetwork() {
        zipCodeApi.downloadZipCodes()
    }

    override fun populateDatabase(): Flow<Boolean> = flow {
        val zipCodes = zipCodeApi.readCSV( "codigos_postais.csv")
        zipCodeDao.insertZipCodes(zipCodes)
    }

    override fun isDatabaseEmpty(): Flow<Boolean> = flow {
        emit(zipCodeDao.getZipCodes().isEmpty())
    }

    override fun searchZipCode(text: String): Flow<Resource<List<ZipCode>>> = flow {
        val zipCodes = zipCodeDao.searchZipCodes(text).map { it.toZipCode() }
        emit(Resource.Success(zipCodes))
    }
}