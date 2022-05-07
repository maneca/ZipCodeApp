package com.joao.zipcodeapp.data.remote

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.joao.zipcodeapp.data.local.ZipCodeEntity
import com.joao.zipcodeapp.domain.remote.ZipCodeApi
import java.io.File

class ZipCodeApiImp(
    private val context: Context
): ZipCodeApi {
    override fun downloadZipCodes() {
        val url = "https://raw.githubusercontent.com/centraldedados/codigos_postais/master/data/codigos_postais.csv"
        val title = URLUtil.guessFileName(url, null, null)
        val cookie = CookieManager.getInstance().getCookie(url)
        val request = DownloadManager
            .Request(Uri.parse(url))
            .setTitle(title)
            .setDescription("Download file please wait....")
            .addRequestHeader("cookie", cookie)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    override fun readCSV(title: String): List<ZipCodeEntity>{
        val zipCodes = mutableListOf<ZipCodeEntity>()
        val filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}/$title"
        csvReader().open(filePath) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                val codigoPostal = row["num_cod_postal"] + "-" + row["ext_cod_postal"]
                val desigPostal = row["desig_postal"] ?: ""
                zipCodes.add(ZipCodeEntity(codigoPostal = codigoPostal, designacaoPostal = desigPostal))
            }
        }

        val file = File(filePath)
        file.delete()

        return zipCodes
    }
}