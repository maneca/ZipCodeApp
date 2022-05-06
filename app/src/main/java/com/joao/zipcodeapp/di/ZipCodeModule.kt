package com.joao.zipcodeapp.di

import android.app.Application
import androidx.room.Room
import com.joao.zipcodeapp.data.local.ZipCodeDatabase
import com.joao.zipcodeapp.data.remote.ZipCodeApiImp
import com.joao.zipcodeapp.data.repository.ZipCodeRepositoryImp
import com.joao.zipcodeapp.domain.remote.ZipCodeApi
import com.joao.zipcodeapp.domain.repository.ZipCodeRepository
import com.joao.zipcodeapp.util.DefaultDispatcherProvider
import com.joao.zipcodeapp.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ZipCodeModule {

    @Provides
    @Singleton
    fun provideZipCodeRepository(
        zipCodeDatabase: ZipCodeDatabase,
        zipCodeApi: ZipCodeApi): ZipCodeRepository{
        return ZipCodeRepositoryImp(
            zipCodeDatabase.zipCodeDao,
            zipCodeApi
        )
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(app: Application): ZipCodeApi {
        return ZipCodeApiImp(
            app.applicationContext
        )
    }

    @Provides
    @Singleton
    fun provideZipCodesDatabase(app: Application): ZipCodeDatabase{
        return Room.databaseBuilder(
            app, ZipCodeDatabase::class.java, "zipCodes_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesDispatcher(): DispatcherProvider = DefaultDispatcherProvider()
}