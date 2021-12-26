package com.caijinfu.ziputils

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

    private val TAG = javaClass.simpleName

    companion object {

        /**
         * 解压缩
         *
         * @param zipFile 需要解压缩的文件
         * @param descDir 解压到的目录
         */
        @JvmStatic
        fun unZip(zipFile: String, descDir: String) {
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
                    val descFile: File = newFile
                    outputStream = FileOutputStream(descFile)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        outputStream.write(buffer, 0, len)
                    }
                    FileUtils.closeQuietly(inputStream)
                    FileUtils.closeQuietly(outputStream)
                }
            } finally {
                FileUtils.closeQuietly(inputStream)
                FileUtils.closeQuietly(outputStream)
            }
        }

        private fun createFile(filePath: String): File {
            val file = File(filePath)
            val parentFile = file.parentFile!!
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            return file
        }

        @JvmStatic
        fun zip(files: List<File>, zipFilePath: String) {
            if (files.isEmpty()) {
                return
            }
            val newZipFile = File(zipFilePath)
            if (!FileUtils.createFile(newZipFile)) {
                return
            }
            val zipFile = newZipFile
            val buffer = ByteArray(1024)
            var zipOutputStream: ZipOutputStream? = null
            var inputStream: FileInputStream? = null
            try {
                zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
                for (file in files) {
                    if (!file.exists()) {
                        continue
                    }
                    zipOutputStream.putNextEntry(ZipEntry(file.name))
                    inputStream = FileInputStream(file)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        zipOutputStream.write(buffer, 0, len)
                    }
                    zipOutputStream.closeEntry()
                }
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
            }
        }
    }

}