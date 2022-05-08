package com.joao.zipcodeapp.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.joao.zipcodeapp.MainCoroutineRule
import com.joao.zipcodeapp.data.local.entity.ZipCodeEntity
import com.joao.zipcodeapp.domain.data.ZipCode
import com.joao.zipcodeapp.domain.repository.ZipCodeRepository
import com.joao.zipcodeapp.util.CustomExceptions
import com.joao.zipcodeapp.util.DispatcherProvider
import com.joao.zipcodeapp.util.Resource
import com.joao.zipcodeapp.util.UiEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ZipCodeViewModelTests {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var mockRepo: ZipCodeRepository

    @MockK
    private lateinit var mockApp: Application

    private lateinit var viewModel: ZipCodeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = testDispatcher
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
        override fun unconfined(): CoroutineDispatcher = testDispatcher

    }

    companion object {
        val zipCode = ZipCode(
            codigoPostal = "4705-328",
            designacaoPostal = "Braga")
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search zip codes, successful`() = runTest (testDispatcher){

        Truth.assertThat(mockRepo).isNotNull()

        coEvery { mockRepo.isDatabaseEmpty() } returns flowOf(false)
        coEvery { mockRepo.getZipCodesFromLocalDatabase() } returns flowOf(Resource.Success(listOf(zipCode)))
        coEvery { mockRepo.searchZipCode(any()) } returns flowOf(Resource.Success(listOf(zipCode)))

        viewModel = ZipCodeViewModel(mockApp, testDispatcherProvider, mockRepo)
        viewModel.state.test {
            viewModel.searchZipCode("")
            val emission = awaitItem()
            Truth.assertThat(emission.zipCodes.size).isEqualTo(1)
        }
    }

    @Test
    fun `search zip codes, fail`() = runTest (testDispatcher){

        Truth.assertThat(mockRepo).isNotNull()

        coEvery { mockRepo.isDatabaseEmpty() } returns flowOf(false)
        coEvery { mockRepo.getZipCodesFromLocalDatabase() } returns flowOf(Resource.Error(exception = CustomExceptions.UnknownException))
        coEvery { mockRepo.searchZipCode(any()) } returns flowOf(Resource.Error(exception = CustomExceptions.UnknownException))

        viewModel = ZipCodeViewModel(mockApp, testDispatcherProvider, mockRepo)
        viewModel.eventFlow.test {
            viewModel.searchZipCode("")
            val emission = awaitItem()
            Truth.assertThat(emission).isEqualTo(UiEvent.Failed)
        }
    }

    @Test
    fun `populate database, successful`() = runTest (testDispatcher){

        Truth.assertThat(mockRepo).isNotNull()

        coEvery { mockRepo.isDatabaseEmpty() } returns flowOf(false)
        coEvery { mockRepo.getZipCodesFromLocalDatabase() } returns flowOf(Resource.Success(listOf(zipCode)))
        coEvery { mockRepo.populateDatabase() } returns flowOf(true)

        viewModel = ZipCodeViewModel(mockApp, testDispatcherProvider, mockRepo)
        viewModel.state.test {
            viewModel.searchZipCode("")
            val emission = awaitItem()
            Truth.assertThat(emission.zipCodes.size).isEqualTo(1)
        }
    }
}