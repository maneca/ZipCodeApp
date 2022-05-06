package com.joao.zipcodeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zipCodes")
data class ZipCodeEntity(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val codigoPostal: String = "",
    val designacaoPostal: String = ""
)