package com.joao.zipcodeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ZipCodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZipCodes(zipCodes: List<ZipCodeEntity>)

    @Query("SELECT * FROM zipCodes")
    suspend fun getZipCodes(): List<ZipCodeEntity>
}