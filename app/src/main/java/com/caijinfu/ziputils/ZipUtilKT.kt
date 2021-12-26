package com.caijinfu.ziputils

import android.text.TextUtils
import android.util.Log
import java.io.*
import java.util.zip.*

/**
 * zip压缩和解压工具类(kt版本)
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
            if (!FileUtils.isExists(File(zipFile))) {
                Log.i(TAG, "unZip, zipFile is no exists")
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
                    Log.i(TAG, "unZip, $zipEntryName start unzip")
                    val descFile: File = newFile
                    outputStream = FileOutputStream(descFile)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        outputStream.write(buffer, 0, len)
                    }
                    Log.i(TAG, "unZip, $zipEntryName end unzip")
                    FileUtils.closeQuietly(inputStream, outputStream)
                }
            } catch (e: Exception) {
                Log.e(TAG, "unZip, Exception：", e)
            } finally {
                FileUtils.closeQuietly(inputStream, outputStream)
            }
            Log.i(TAG, "unZip, all unzip finish")
        }

        /**
         * 压缩目录
         * 注意事项：
         * 1.压缩的目录与被压缩的目录不能是同一个。因为会将压缩文件压缩，循环直到奔溃。
         * 2.压缩的目录也不能是被压缩的目录的子目录，否则也会出现1的情况。
         *
         * @param srcFileString 需要压缩的目录
         * @param zipFileString 压缩到哪个目录
         * @param fileName 压缩文件名，xxx.zip
         */
        @JvmStatic
        fun zipFolder(srcFileString: String, zipFileString: String, fileName: String) {
            if (TextUtils.isEmpty(srcFileString) || TextUtils.isEmpty(zipFileString)) {
                Log.e(TAG, "zipFolder, srcFileString or zipFileString isEmpty")
                return
            }
            val zipName: String = if (TextUtils.isEmpty(fileName)) {
                System.currentTimeMillis().toString() + ".zip"
            } else {
                fileName
            }
            val fos = FileOutputStream(zipFileString + File.separator + zipName)
            //用于校验,不加这个校验的话，解压会报错
            val cos = CheckedOutputStream(fos, CRC32())
            // 创建ZIP
            try {
                val outZip = ZipOutputStream(cos)
                // 创建文件
                val file = File(srcFileString)
                // 压缩
                zipFiles(file.parent + File.separator, file.name, outZip)
                // 完成和关闭
                outZip.finish()
                FileUtils.closeQuietly(outZip)
                Log.i(TAG, "zipFolder, all zip finish")
            } catch (e: Exception) {
                Log.e(TAG, "zipFolder, Exception：", e)
            }
        }

        /**
         * 压缩目录
         *
         * @param folderString 需要压缩的目录
         * @param fileString 压缩到哪个目录
         * @param zipOutputSteam ZipOutputStream
         */
        private fun zipFiles(
            folderString: String, fileString: String,
            zipOutputSteam: ZipOutputStream?,
        ) {
            try {
                if (zipOutputSteam == null) {
                    Log.e(TAG, "zipFiles, zipOutputSteam is null")
                    return
                }
                val file = File(folderString + fileString)
                if (file.isFile) {
                    Log.i(TAG, "zipFiles, ${file.name} start zip")
                    val zipEntry = ZipEntry(fileString)
                    val inputStream = FileInputStream(file)
                    zipOutputSteam.putNextEntry(zipEntry)
                    var len: Int
                    val buffer = ByteArray(4096)
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        zipOutputSteam.write(buffer, 0, len)
                    }
                    zipOutputSteam.closeEntry()
                    Log.i(TAG, "zipFiles, ${file.name} end zip")
                } else {
                    Log.e(TAG, "zip, ${file.name} isDirectory")
                    // 文件夹
                    val fileList = file.list()
                    // 没有子文件和压缩
                    if (fileList.isEmpty()) {
                        val zipEntry = ZipEntry(fileString + File.separator)
                        zipOutputSteam.putNextEntry(zipEntry)
                        zipOutputSteam.closeEntry()
                    }
                    // 子文件和递归
                    for (i in fileList.indices) {
                        zipFiles(folderString + fileString + File.separator, fileList[i],
                            zipOutputSteam)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "zipFiles, Exception：", e)
            }
        }
    }
}