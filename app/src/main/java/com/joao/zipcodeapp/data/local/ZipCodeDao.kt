package com.joao.zipcodeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joao.zipcodeapp.data.local.entity.ZipCodeEntity

@Dao
interface ZipCodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZipCodes(zipCodes: List<ZipCodeEntity>)

    @Query("SELECT * FROM zipCodes ORDER BY codigoPostal ASC")
    suspend fun getZipCodes(): List<ZipCodeEntity>

    @Query("""
  SELECT *
  FROM zipCodes
  JOIN zipcodes_fts ON zipCodes.codigoPostal = zipcodes_fts.codigoPostal
  WHERE zipcodes_fts MATCH :query
""")
    suspend fun searchZipCodes(query: String): List<ZipCodeEntity>
}