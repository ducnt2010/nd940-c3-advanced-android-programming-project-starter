package com.udacity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

private const val TAG = "DetailActivity"

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val isSuccess = intent.getBooleanExtra(Constant.IS_SUCCESS, false)
        var status = if (isSuccess) {
            getString(R.string.success)
        } else {
            getString(R.string.failed)
        }

        val fileName = intent.getStringExtra(Constant.FILE_NAME)

        Log.i(TAG, "isSuccess= $isSuccess , fileName= $fileName ")

        textFileName.text = fileName
        textStatus.text = status
        okButton.setOnClickListener { finish() }
    }

}
