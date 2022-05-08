package com.joao.zipcodeapp.repository

import com.google.common.truth.Truth
import com.joao.zipcodeapp.data.local.ZipCodeDao
import com.joao.zipcodeapp.data.local.entity.ZipCodeEntity
import com.joao.zipcodeapp.data.repository.ZipCodeRepositoryImp
import com.joao.zipcodeapp.domain.data.ZipCode
import com.joao.zipcodeapp.domain.remote.ZipCodeApi
import com.joao.zipcodeapp.domain.repository.ZipCodeRepository
import com.joao.zipcodeapp.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ZipCodeRepositoryTests {

    @MockK
    private lateinit var mockApi: ZipCodeApi

    @MockK(relaxUnitFun = true)
    private lateinit var mockDao: ZipCodeDao

    private lateinit var repository: ZipCodeRepository

    companion object {
        val zipCodeA = ZipCodeEntity(
            codigoPostal = "4705-328",
            designacaoPostal = "Braga")

        val zipCodeB = ZipCodeEntity(
            codigoPostal = "1243-345",
            designacaoPostal = "Setubal")
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = ZipCodeRepositoryImp(mockDao, mockApi)
    }

    @Test
    fun getZipCodesFromLocalDb() = runBlocking {
        Assert.assertNotNull(mockDao)

        coEvery { mockDao.getZipCodes() } coAnswers { listOf(zipCodeA, zipCodeB) }

        val list: ArrayList<Resource<List<ZipCode>>> = ArrayList()
        repository.getZipCodesFromLocalDatabase().collect {
            list.add(it)
        }

        Truth.assertThat(list.size).isEqualTo(1)
        Truth.assertThat((list[0] as Resource.Success).data?.size).isEqualTo(2)
    }

    @Test
    fun getZipCodesFromLocalDbThrowsException() = runBlocking {
        Assert.assertNotNull(mockDao)

        coEvery { mockDao.getZipCodes() } throws Exception()

        val list: ArrayList<Resource<List<ZipCode>>> = ArrayList()
        repository.getZipCodesFromLocalDatabase().collect {
            list.add(it)
        }

        Truth.assertThat(list.size).isEqualTo(1)
        Truth.assertThat((list[0] as Resource.Error).data).isNull()
        Truth.assertThat((list[0] as Resource.Error).exception).isNotNull()
    }

    @Test
    fun populateDatabase() = runBlocking {
        Assert.assertNotNull(mockApi)

        coEvery { mockApi.readCSV("codigos_postais.csv") } coAnswers { listOf(zipCodeA, zipCodeB) }

        val list: ArrayList<Boolean> = ArrayList()
        repository.populateDatabase().collect {
            list.add(it)
        }

        Truth.assertThat(list.size).isEqualTo(1)
        Truth.assertThat(list[0]).isTrue()
    }

    @Test
    fun populateDatabaseThrowsException() = runBlocking {
        Assert.assertNotNull(mockApi)

        coEvery { mockApi.readCSV("codigos_postais.csv") } throws Exception()

        val list: ArrayList<Boolean> = ArrayList()
        repository.populateDatabase().collect {
            list.add(it)
        }

        Truth.assertThat(list.size).isEqualTo(1)
        Truth.assertThat(list[0]).isFalse()
    }

    @Test
    fun isDatabaseEmpty() = runBlocking {
        Assert.assertNotNull(mockDao)

        coEvery { mockDao.getZipCodes() } coAnswers { listOf(zipCodeA, zipCodeB) }

        val list: ArrayList<Boolean> = ArrayList()
        repository.populateDatabase().collect {
            list.add(it)
        }

        Truth.assertThat(list.size).isEqualTo(1)
        Truth.assertThat(list[0]).isFalse()
    }

    @Test
    fun searchZipCode() = runBlocking {
        Assert.assertNotNull(mockDao)

        coEvery { mockDao.searchZipCodes("*32*") } coAnswers { listOf(zipCodeA, zipCodeB) }

        val list: ArrayList<Resource<List<ZipCode>>> = ArrayList()
        repository.searchZipCode("*32*").collect {
            list.add(it)
        }

        Truth.assertThat(list.size).isEqualTo(1)
        Truth.assertThat((list[0] as Resource.Success).data?.size).isEqualTo(2)
    }
}