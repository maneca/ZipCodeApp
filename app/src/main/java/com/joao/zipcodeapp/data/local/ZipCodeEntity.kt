package com.joao.zipcodeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zipCodes")
data class ZipCodeEntity(
    @PrimaryKey val codigoPostal: String,
    val designacaoPostal: String
)