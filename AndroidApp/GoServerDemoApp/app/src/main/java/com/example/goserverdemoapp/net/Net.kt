package com.example.goserverdemoapp.net

import android.content.Context
import android.os.FileUtils
import android.util.Log
import com.example.goserverdemoapp.file.FILE_EXPAND
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
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

    fun startConnection(
        context: Context,
        requestUrl: String,
        requestMethod: String,
        fileName: String
    ): String {
        Log.d(
            TAG,
            "#startConnection url = $requestUrl requestMethod = $requestMethod fileName = $fileName"
        )
        // URLオブジェクト生成
        val url = URL(requestUrl)
        // UrlConnection生成
        val urlConnection = url.openConnection() as HttpURLConnection
        var result = ""
        try {
            // パラメータを設定
            when (requestMethod) {
                RequestMethod.GET.toString() -> {
                    urlConnection.requestMethod = requestMethod
                    urlConnection.connect()
                }
                RequestMethod.POST.toString() -> {
                    val boundary = "--------------------------"
                    urlConnection.requestMethod = requestMethod
                    urlConnection.doOutput = true
                    urlConnection.doInput = true
                    urlConnection.useCaches = false
                    urlConnection.setRequestProperty("ENCTYPE", "multipart/form-data")
                    urlConnection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=$boundary"
                    )
                    val filePath = context.filesDir
                    val file = File("$filePath/$fileName$FILE_EXPAND")
                    var bytesRead: Int
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
                }
                else ->
                    return ""
            }

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
        return result
    }
}