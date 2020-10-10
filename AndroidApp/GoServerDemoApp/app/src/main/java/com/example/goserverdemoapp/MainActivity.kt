package com.example.goserverdemoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.example.goserverdemoapp.file.File
import com.example.goserverdemoapp.net.Net
import com.example.goserverdemoapp.net.RequestMethod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.net.ssl.HttpsURLConnection

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

        start_connection_button.setOnClickListener(this)
        file_create_button.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!) {
            start_connection_button -> {
                GlobalScope.launch {
                    val url = url_input_form.text.toString()
                    val method = type_select_spinner.selectedItem.toString()
                    Log.d(TAG, "#onClick url = $url requestMethod = $method")
                    Net().startConnection(
                        applicationContext,
                        url,
                        method,
                        transfer_file_name.text.toString()
                    )
                }
            }

            file_create_button -> {
                val fileName = file_name_input_form.text.toString()
                val str = contents_input_form.text.toString()
                Log.d(TAG, "#onClick fileName = $fileName contents = $str")
                File().makeTxtFile(this, fileName, str)
                transfer_file_name.text = file_name_input_form.text
            }
        }
    }
}