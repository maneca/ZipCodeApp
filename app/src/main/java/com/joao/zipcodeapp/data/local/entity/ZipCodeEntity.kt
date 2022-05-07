package com.joao.zipcodeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joao.zipcodeapp.domain.data.ZipCode

@Entity(tableName = "zipCodes")
data class ZipCodeEntity(
    @PrimaryKey val codigoPostal: String,
    val designacaoPostal: String
){
    fun toZipCode(): ZipCode{
        return ZipCode(
            codigoPostal = codigoPostal,
            designacaoPostal = designacaoPostal
        )
    }
}