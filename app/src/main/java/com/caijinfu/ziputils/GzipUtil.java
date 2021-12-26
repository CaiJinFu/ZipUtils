package com.caijinfu.ziputils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip压缩和解压工具类(java版本)
 *
 * @author 猿小蔡
 * @date 2021/12/26
 */
public class GzipUtil {

    private static final String TAG = "GzipUtil";

    /**
     * gzip压缩
     *
     * @param srcFile 源文件
     * @param targetFile 目标文件
     */
    public static void zip(File srcFile, File targetFile) {
        if (!FileUtils.isExists(srcFile)) {
            Log.e(TAG, "zip, srcFile is no exists");
            return;
        }
        try (FileInputStream fis = new FileInputStream(srcFile);
             // 往外写的时候, 用GZIPOutputStream, 直接写成压缩文件, 包装流
             GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(targetFile))) {
            Log.i(TAG, "unzip, start zip");
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                gos.write(buffer, 0, len);
            }
            Log.i(TAG, "unzip, end zip");
        } catch (Exception e) {
            Log.e(TAG, "zip, Exception：", e);
        }
    }

    /**
     * 解压缩
     *
     * @param srcFile 源文件 压缩包
     * @param targetFile 目标文件 普通文件
     */
    public static void unzip(File srcFile, File targetFile) {
        if (!FileUtils.isExists(srcFile)) {
            Log.e(TAG, "unzip, srcFile is no exists");
            return;
        }
        try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(srcFile));
             FileOutputStream fos = new FileOutputStream(targetFile)) {
            Log.i(TAG, "unzip, start unzip");
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            Log.i(TAG, "unzip, end unzip");
        } catch (Exception e) {
            Log.e(TAG, "unzip, Exception：", e);
        }
    }

    /**
     * 解压缩
     *
     * @param is 输入流 压缩包
     * @param targetFile 目标文件 普通文件
     */
    public static void unzip(InputStream is, File targetFile) {
        try (GZIPInputStream gis = new GZIPInputStream(is);
             FileOutputStream fos = new FileOutputStream(targetFile)) {
            Log.i(TAG, "unzip, start unzip");
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            Log.i(TAG, "unzip, end unzip");
        } catch (IOException e) {
            Log.e(TAG, "unzip, Exception：", e);
        }
    }
}