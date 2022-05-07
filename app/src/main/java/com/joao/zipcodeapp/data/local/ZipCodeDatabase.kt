package com.joao.zipcodeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joao.zipcodeapp.data.local.entity.ZipCodeEntity
import com.joao.zipcodeapp.data.local.entity.ZipCodeFts

@Database(
    entities = [ZipCodeEntity::class, ZipCodeFts::class],
    version = 2
)
abstract class ZipCodeDatabase: RoomDatabase() {

    abstract val zipCodeDao: ZipCodeDao
}