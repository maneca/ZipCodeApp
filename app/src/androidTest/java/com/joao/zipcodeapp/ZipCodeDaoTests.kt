package com.joao.zipcodeapp

import androidx.room.Room
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.joao.zipcodeapp.data.local.ZipCodeDao
import com.joao.zipcodeapp.data.local.ZipCodeDatabase
import com.joao.zipcodeapp.data.local.entity.ZipCodeEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@SmallTest
class ZipCodeDaoTests {

    private lateinit var database: ZipCodeDatabase
    private lateinit var dao: ZipCodeDao

    companion object {
        val zipCodeA = ZipCodeEntity(
            codigoPostal = "4705-328",
            designacaoPostal = "Braga")

        val zipCodeB = ZipCodeEntity(
            codigoPostal = "1243-345",
            designacaoPostal = "Setubal")
    }

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ZipCodeDatabase::class.java)
            .build()

        dao = database.zipCodeDao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun getZipCodes() = runBlocking{
        dao.insertZipCodes(listOf(zipCodeA, zipCodeB))

        val notes = dao.getZipCodes()
        Assert.assertEquals(2, notes.size)
        Assert.assertEquals(zipCodeB.codigoPostal, notes[0].codigoPostal)
        Assert.assertEquals(zipCodeB.designacaoPostal, notes[0].designacaoPostal)
        Assert.assertEquals(zipCodeA.codigoPostal, notes[1].codigoPostal)
        Assert.assertEquals(zipCodeA.designacaoPostal, notes[1].designacaoPostal)
    }

    @Test
    fun searchZipCodes() = runBlocking{
        dao.insertZipCodes(listOf(zipCodeA, zipCodeB))

        val notes = dao.searchZipCodes("*32*")
        Assert.assertEquals(1, notes.size)
        Assert.assertEquals(zipCodeA.codigoPostal, notes[0].codigoPostal)
        Assert.assertEquals(zipCodeA.designacaoPostal, notes[0].designacaoPostal)
    }
}