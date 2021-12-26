package com.caijinfu.ziputils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val srcFileName1: String = "《Java实战（第2版）》源代码.rar"

        @JvmField
        val srcFileName2: String = "沒有阻挠地Hi Kick_104.rmvb"

        @JvmField
        val srcFileName3: String = "test.zip"

        @JvmField
        val TAG = "MainActivity"

        @JvmField
        val zipName = "cool.zip"
    }

    val mTvKTZip by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tvKTZip) }

    val mTvUnKTZip by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tvUnKTZip) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val zipFilePath = "$filesDir"
        val unZipFilePath = "$filesDir${File.separator}unzip"
        val downPath = "$filesDir${File.separator}down"
        FileUtils.createDir(downPath)
        FileUtils.createDir(unZipFilePath)
        thread() {
            val file1 = File(downPath, srcFileName1)
            val file2 = File(downPath, srcFileName2)
            val file3 = File(downPath, srcFileName3)
            FileUtils.deleteFile(file1)
            FileUtils.deleteFile(file2)
            FileUtils.deleteFile(file3)
            val openAssetFile1 = FileUtils.openAssetFile(this, srcFileName1)
            FileUtils.copyFile(openAssetFile1, file1)
            val openAssetFile2 = FileUtils.openAssetFile(this, srcFileName2)
            FileUtils.copyFile(openAssetFile2, file2)
            Log.i(TAG, "onCreate, copyFile is finish")
        }

        mTvKTZip.setOnClickListener {
            thread {
                val file = File(zipFilePath, zipName)
                FileUtils.deleteFile(file)
                ZipFilesUtils.zipFolder(downPath, zipFilePath, zipName)
            }
        }

        mTvUnKTZip.setOnClickListener {
            thread {
                val file = File(unZipFilePath)
                FileUtils.deleteFile(file)
                ZipFilesUtils.unZip(zipFilePath + File.separator + zipName, unZipFilePath)
            }
        }
    }
}