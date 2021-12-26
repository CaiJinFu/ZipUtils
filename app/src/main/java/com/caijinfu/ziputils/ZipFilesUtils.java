package com.caijinfu.ziputils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩和解压工具类(java版本)
 *
 * @author 猿小蔡
 * @date 2021/12/26
 */
public class ZipFilesUtils {

    private static final String TAG = "ZipFilesUtils";

    /**
     * 解压缩一个文件
     *
     * @param zipFilePath 压缩文件路径
     * @param folderPath 解压缩的目标目录
     */
    public static void unZip(String zipFilePath, String folderPath) {
        File zipFile = new File(zipFilePath);
        if (!FileUtils.isExists(zipFile)) {
            Log.i(TAG, "upZipFile, zipFile is no exists");
            return;
        }
        File desDir = new File(folderPath);
        if (!FileUtils.isExists(desDir)) {
            boolean isSuccess = FileUtils.createDir(folderPath);
            if (!isSuccess) {
                Log.i(TAG, "upZipFile, createDir is fail");
                return;
            }
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            ZipFile zf = new ZipFile(zipFile);
            Enumeration< ? > entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                inputStream = zf.getInputStream(entry);
                String zipEntryName = entry.getName();
                String descFilePath = folderPath + File.separator + zipEntryName;
                File desFile = new File(descFilePath);
                if (!FileUtils.createFile(desFile)) {
                    continue;
                }
                Log.i(TAG, "zip, start unzip: " + zipEntryName);
                outputStream = new FileOutputStream(desFile);
                byte buffer[] = new byte[1024 * 1024];
                int realLength;
                while ((realLength = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, realLength);
                }
                Log.i(TAG, "zip, end unzip: " + zipEntryName);
                FileUtils.closeQuietly(inputStream, outputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "unZip, Exception：", e);
        } finally {
            FileUtils.closeQuietly(inputStream, outputStream);
        }
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
    public static void zipFolder(String srcFileString, String zipFileString, String fileName) {
        if (TextUtils.isEmpty(srcFileString) || TextUtils.isEmpty(zipFileString)) {
            Log.e(TAG, "zipFolder, srcFileString or zipFileString isEmpty");
            return;
        }
        String zipName;
        if (TextUtils.isEmpty(fileName)) {
            zipName = System.currentTimeMillis() + ".zip";
        } else {
            zipName = fileName;
        }
        // 创建ZIP
        try {
            FileOutputStream fos = new FileOutputStream(zipFileString + File.separator + zipName);
            //用于校验,不加这个校验的话，解压会报错
            CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
            ZipOutputStream outZip = new ZipOutputStream(cos);
            // 创建文件
            File file = new File(srcFileString);
            // 压缩
            zipFiles(file.getParent() + File.separator, file.getName(), outZip);
            // 完成和关闭
            outZip.finish();
            FileUtils.closeQuietly(outZip);
            Log.i(TAG, "zipFolder, all zip finish");
        } catch (Exception e) {
            Log.e(TAG, "zipFolder, Exception：", e);
        }
    }

    /**
     * 压缩目录
     *
     * @param folderString 需要压缩的目录
     * @param fileString 压缩到哪个目录
     * @param zipOutputSteam ZipOutputStream
     */
    private static void zipFiles(String folderString, String fileString,
        ZipOutputStream zipOutputSteam) {
        try {
            if (zipOutputSteam == null) {
                Log.e(TAG, "zipFiles, zipOutputSteam is null");
                return;
            }
            File file = new File(folderString + fileString);
            if (file.isFile()) {
                Log.i(TAG, "zipFiles, start zip: " + file.getName());
                ZipEntry zipEntry = new ZipEntry(fileString);
                FileInputStream inputStream = new FileInputStream(file);
                zipOutputSteam.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[4096];
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputSteam.write(buffer, 0, len);
                }
                zipOutputSteam.closeEntry();
                Log.i(TAG, "zipFiles, end zip: " + file.getName());
            } else {
                // 文件夹
                String fileList[] = file.list();
                // 没有子文件和压缩
                if (fileList == null || fileList.length <= 0) {
                    ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                    zipOutputSteam.putNextEntry(zipEntry);
                    zipOutputSteam.closeEntry();
                    return;
                }
                // 子文件和递归
                for (int i = 0; i < fileList.length; i++) {
                    zipFiles(folderString + fileString + File.separator, fileList[i],
                        zipOutputSteam);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "zipFiles, Exception：", e);
        }
    }
}
