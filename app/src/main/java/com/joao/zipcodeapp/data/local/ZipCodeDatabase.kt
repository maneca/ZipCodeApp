package com.joao.zipcodeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ZipCodeEntity::class],
    version = 1
)
abstract class ZipCodeDatabase: RoomDatabase() {

    abstract val zipCodeDao: ZipCodeDao
}