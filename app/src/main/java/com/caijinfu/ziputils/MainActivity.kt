package com.caijinfu.ziputils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    companion object {
        @JvmField
        val srcFileName1: String = "《Java实战（第2版）》源代码.rar"
        val srcFileName2: String = "沒有阻挠地Hi Kick_104.rmvb"
    }

    val mTvKTZip by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tvKTZip) }

    val mTvUnKTZip by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tvUnKTZip) }

    lateinit var zipFilePath: String

    lateinit var unZipFilePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        zipFilePath = "$filesDir${File.separator}good.zip"
        unZipFilePath = "$filesDir${File.separator}unzip"
        thread() {
            FileUtils.deleteFile(File(zipFilePath))
            val file1 = File(filesDir, srcFileName1)
            val file2 = File(filesDir, srcFileName2)
            FileUtils.deleteFile(file1)
            FileUtils.deleteFile(file2)
            val openAssetFile1 = FileUtils.openAssetFile(this, srcFileName1)
            FileUtils.copyFile(openAssetFile1, file1)
            val openAssetFile2 = FileUtils.openAssetFile(this, srcFileName2)
            FileUtils.copyFile(openAssetFile2, file2)
        }

        mTvKTZip.setOnClickListener {
            thread {
                ZipUtilKT.zipByFolder(filesDir.absolutePath, zipFilePath)
            }
        }
        mTvUnKTZip.setOnClickListener {
            thread {
                val file = File(unZipFilePath)
                FileUtils.deleteFile(file)
                ZipUtilKT.unZip(zipFilePath, unZipFilePath)
            }
        }
    }
}