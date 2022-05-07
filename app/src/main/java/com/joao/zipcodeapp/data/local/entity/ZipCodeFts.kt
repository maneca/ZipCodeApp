package com.joao.zipcodeapp.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "zipcodes_fts")
@Fts4(contentEntity = ZipCodeEntity::class)
data class ZipCodeFts (
    val codigoPostal: String,
    val designacaoPostal: String
    )