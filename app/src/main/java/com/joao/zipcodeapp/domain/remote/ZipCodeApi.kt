package com.joao.zipcodeapp.domain.remote

import com.joao.zipcodeapp.data.local.ZipCodeEntity

interface ZipCodeApi {

    fun downloadZipCodes()

    fun readCSV(title: String): List<ZipCodeEntity>
}