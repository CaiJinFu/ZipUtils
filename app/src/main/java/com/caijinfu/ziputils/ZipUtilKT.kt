package com.caijinfu.ziputils

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 *
 * @author 猿小蔡
 * @date 2021/12/26
 */

class ZipUtilKT {

    companion object {

        private val TAG = "ZipUtilKT"

        /**
         * 解压缩
         *
         * @param zipFile 需要解压缩的文件
         * @param descDir 解压到的目录
         */
        @JvmStatic
        fun unZip(zipFile: String, descDir: String) {
            if (!File(zipFile).exists()) {
                Log.i(TAG, "zip, zipFile is no exists")
                return
            }
            val buffer = ByteArray(1024)
            var outputStream: OutputStream? = null
            var inputStream: InputStream? = null
            try {
                val zf = ZipFile(zipFile)
                val entries = zf.entries()
                while (entries.hasMoreElements()) {
                    val zipEntry: ZipEntry = entries.nextElement() as ZipEntry
                    val zipEntryName: String = zipEntry.name
                    inputStream = zf.getInputStream(zipEntry)
                    val descFilePath: String = descDir + File.separator + zipEntryName
                    val newFile = File(descFilePath)
                    if (!FileUtils.createFile(newFile)) {
                        continue
                    }
                    Log.i(TAG, "zip, $zipEntryName start unzip")
                    val descFile: File = newFile
                    outputStream = FileOutputStream(descFile)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        outputStream.write(buffer, 0, len)
                    }
                    Log.i(TAG, "zip, $zipEntryName end unzip")
                    FileUtils.closeQuietly(inputStream)
                    FileUtils.closeQuietly(outputStream)
                }
            } catch (e: Exception) {
                Log.e(TAG, "unZip, Exception：", e)
            } finally {
                FileUtils.closeQuietly(inputStream)
                FileUtils.closeQuietly(outputStream)
            }
            Log.i(TAG, "zip, all unzip finish")
        }

        @JvmStatic
        fun zip(files: List<File>, zipFilePath: String) {
            if (files.isEmpty()) {
                Log.e(TAG, "zip, files isEmpty")
                return
            }
            val zipFile = File(zipFilePath)
            if (!FileUtils.createFile(zipFile)) {
                Log.e(TAG, "zip, createFile is fail")
                return
            }
            val buffer = ByteArray(1024)
            var zipOutputStream: ZipOutputStream? = null
            var inputStream: FileInputStream? = null
            try {
                zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
                for (file in files) {
                    // 如果文件不存在或者是目录则不压缩
                    if (!file.exists() || file.isDirectory) {
                        Log.e(TAG, "zip, ${file.name} is no exists or isDirectory")
                        continue
                    }
                    Log.i(TAG, "zip, ${file.name} start zip")
                    zipOutputStream.putNextEntry(ZipEntry(file.name))
                    inputStream = FileInputStream(file)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        zipOutputStream.write(buffer, 0, len)
                    }
                    zipOutputStream.closeEntry()
                    Log.i(TAG, "zip, ${file.name} end zip")
                }
            } catch (e: Exception) {
                Log.e(TAG, "zip, Exception：", e)
            } finally {
                FileUtils.closeQuietly(inputStream)
                FileUtils.closeQuietly(zipOutputStream)
            }
        }

        /**
         * 压缩目录
         *
         * @param fileDir 压缩的目录
         * @param zipFilePath 压缩到哪个文件
         */
        @JvmStatic
        fun zipByFolder(fileDir: String, zipFilePath: String) {
            val folder = File(fileDir)
            if (folder.exists() && folder.isDirectory) {
                val files = folder.listFiles()
                val filesList: List<File> = files.toList()
                zip(filesList, zipFilePath)
                Log.i(TAG, "zipByFolder, all zip finish")
            }
        }
    }

}