package com.example.goserverdemoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goserverdemoapp.file.File
import com.example.goserverdemoapp.net.Net
import com.example.goserverdemoapp.net.RequestMethod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = arrayOf(RequestMethod.POST, RequestMethod.GET)
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        type_select_spinner.adapter = adapter

        file_create_button.setOnClickListener(this)
        start_connection_button.setOnClickListener(this)
        start_download_button.setOnClickListener(this)
    }

    @SuppressLint("ShowToast")
    override fun onClick(p0: View?) {
        val fileName = file_name_input_form.text.toString()
        when (p0!!) {
            file_create_button -> {
                val str = contents_input_form.text.toString()
                Log.d(TAG, "#onClick fileName = $fileName contents = $str")
                File().makeTxtFile(this, fileName, str)
            }

            start_connection_button -> {
                val url = url_input_form.text.toString()
//                val method = type_select_spinner.selectedItem.toString()
//                Log.d(TAG, "#onClick url = $url requestMethod = $method")
                Log.d(TAG, "#onClick upload url = $url fileName = $fileName")
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        Net().requestPostFile(
                            applicationContext,
                            url,
                            fileName
                        )
                    }.let {
                        var result = "Upload Failed"
                        if (it.first == HttpURLConnection.HTTP_OK) {
                            result = "Upload Success"
                        }
                        Toast.makeText(
                            applicationContext, result + "\nresult =  ${it.second}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

            start_download_button -> {
                val url = url_input_form.text.toString()
                Log.d(TAG, "#onClick download url = $url fileName = $fileName")
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        Net().requestFileDownload(applicationContext, url, fileName)
                    }.let {
                        var result = "Download Failed"
                        if (it == HttpURLConnection.HTTP_OK) {
                            result = "Download Success"
                        }
                        Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}