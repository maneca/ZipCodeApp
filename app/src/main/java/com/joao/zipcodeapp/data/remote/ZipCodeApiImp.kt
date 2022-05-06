package com.joao.zipcodeapp.data.remote

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import com.joao.zipcodeapp.domain.remote.ZipCodeApi
import com.joao.zipcodeapp.util.CSVReader
import java.io.FileReader

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

    private fun importCSV(title: String){
        val csvReader =
            CSVReader(FileReader("${Environment.DIRECTORY_DOWNLOADS}/CSV/$title.csv"))
        /* path of local storage (it should be your csv file locatioin)*/
        var nextLine: Array<String> ? = null
        var count = 0
        val columns = StringBuilder()
            do {
                val value = StringBuilder()
                nextLine = csvReader.readNext()
                nextLine.let { line->
                    if (line != null) {
                        for (i in 0 until line.size - 1) {
                            if (count == 0) {                             // the count==0 part only read
                                if (i == line.size - 2) {             //your csv file column name
                                    columns.append(line[i])
                                    count =1
                                } else
                                    columns.append(line[i]).append(",")
                            } else {                         // this part is for reading value of each row
                                if (i == line.size - 2) {
                                    value.append("'").append(line[i]).append("'")
                                    count = 2
                                } else
                                    value.append("'").append(line[i]).append("',")
                            }
                        }
                    }
                    if (count==2) {
                        var a = 2
                    }
                }
            }while ((nextLine)!=null)
    }
}