package com.example.goserverdemoapp.net

import android.content.Context
import android.os.FileUtils
import android.util.Log
import com.example.goserverdemoapp.file.FILE_EXPAND
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.min


class Net {

    companion object {
        private const val TAG = "Net"

        private const val TWO_HYPHEN = "--"
        private const val LINE_END = "\r\n"

        private const val BUFFER_SIZE = 128
    }

    fun requestPostFile(
        context: Context,
        requestUrl: String,
        fileName: String
    ): Pair<Int, String> {
        Log.d(TAG, "#startConnection url = $requestUrl fileName = $fileName")
        // URLオブジェクト生成
        val url = URL(requestUrl)
        // UrlConnection生成
        val urlConnection = url.openConnection() as HttpURLConnection
        var result = ""
        var responseCode = 0
        try {
            val boundary = "--------------------------"
            urlConnection.requestMethod = RequestMethod.POST.toString()
            urlConnection.doOutput = true
            urlConnection.doInput = true
            urlConnection.useCaches = false
            urlConnection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=$boundary"
            )
            val filePath = context.filesDir
            val file = File("$filePath/$fileName$FILE_EXPAND")
            FileInputStream(file).use { fileInputStream ->
                urlConnection.connect()
                DataOutputStream(urlConnection.outputStream).use {
                    it.writeBytes(
                        TWO_HYPHEN + boundary + LINE_END +
                                "Content-Disposition: form-data; name=\"upload_file\"; " +
                                "filename=\"$fileName$FILE_EXPAND\"$LINE_END" +
                                "Content-Type: application/octet-stream$LINE_END$LINE_END"
                    )

                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int
                    do {
                        bytesRead = fileInputStream.read(buffer)
                        if (bytesRead == -1) {
                            break
                        }
                        it.write(buffer, 0, bytesRead)
                    } while (true)
                    it.writeBytes(
                        LINE_END + TWO_HYPHEN + boundary + TWO_HYPHEN + LINE_END
                    )
                    it.flush()
                }
            }

            responseCode = urlConnection.responseCode
            Log.d(TAG, "#startConnection responseCode = $responseCode")

            BufferedReader(InputStreamReader(urlConnection.inputStream)).use {
                val sb = StringBuffer()
                for (line in it.readLines()) {
                    line.let { sb.append(line) }
                }
                result = sb.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "#startConnection$e")
        } finally {
            urlConnection.disconnect()
        }
        Log.d(TAG, result)
        return responseCode to result
    }

    fun requestFileDownload(
        context: Context,
        requestUrl: String,
        fileName: String
    ): Int {
        val url = URL("$requestUrl/$fileName$FILE_EXPAND")
        // UrlConnection生成
        val urlConnection = url.openConnection() as HttpURLConnection
        var responseCode = 0
        try {
            urlConnection.requestMethod = RequestMethod.GET.toString()
            urlConnection.doInput = true
            urlConnection.useCaches = false
            urlConnection.connect()

            responseCode = urlConnection.responseCode
            Log.d(TAG, "#startDownload responseCode = $responseCode")
            when (responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val path = context.filesDir.toString() + "/" + fileName + FILE_EXPAND
                    DataInputStream(urlConnection.inputStream).use { fileInputStream ->
                        DataOutputStream(BufferedOutputStream(FileOutputStream(path))).use {
                            val buffer = ByteArray(BUFFER_SIZE)
                            var byteRead: Int
                            do {
                                byteRead = fileInputStream.read(buffer)
                                if (byteRead == -1) {
                                    break
                                }
                                it.write(buffer, 0, byteRead)
                            } while (true)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "#startConnection$e")
        } finally {
            urlConnection.disconnect()
        }
        return responseCode
    }
}