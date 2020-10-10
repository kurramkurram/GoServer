package com.example.goserverdemoapp.file

import android.content.Context
import android.util.Log
import java.io.IOException

const val FILE_EXPAND = ".txt"

class File {
    companion object {
        private const val TAG = "File"
    }

    fun makeTxtFile(context: Context, fileName: String, str: String) {
        try {
            context.openFileOutput(fileName + FILE_EXPAND, Context.MODE_PRIVATE).use {
                it.write(str.toByteArray())
            }

        } catch (e: IOException) {
            Log.e(TAG, "#makeTxtFile $e")
        }
    }
}