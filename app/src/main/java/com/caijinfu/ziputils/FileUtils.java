package com.caijinfu.ziputils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件工具类
 *
 * @author 猿小蔡
 * @date 2021/12/26
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 复制一个文件到另一个文件
     *
     * @param srcFile 源文件
     * @param targetFile 目标文件
     */
    public static void copyFile(File srcFile, File targetFile) {
        try (InputStream in = new FileInputStream(srcFile)) {
            OutputStream out = new FileOutputStream(targetFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "copyFile, IOException: ", e);
        }
    }

    /**
     * 复制一个文件到另一个文件
     *
     * @param srcStream 源文件输入流
     * @param targetFile 目标文件
     */
    public static void copyFile(InputStream srcStream, File targetFile) {
        try (InputStream in = srcStream) {
            OutputStream out = new FileOutputStream(targetFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "copyFile, IOException: ", e);
        }
    }

    /**
     * 打开Asset下的文件
     *
     * @param context 上下文
     * @param fileName 文件名
     * @return 输入流
     */
    public static InputStream openAssetFile(Context context, String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            Log.e(TAG, "openAssetFile, IOException：", e);
        }
        return is;
    }

    /**
     * 创建文件
     *
     * @return true：成功，false：失败
     */
    public static boolean createFile(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (isExists(file.getParentFile())) {
                return file.createNewFile();
            } else {
                if (createDir(file.getParentFile().getAbsolutePath())) {
                    return file.createNewFile();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception：", e);
        }
        return false;
    }

    /**
     * 递归创建文件夹
     *
     * @return true：成功，false：失败
     */
    public static boolean createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (isExists(file.getParentFile())) {
                return file.mkdir();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                return file.mkdir();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception：", e);
        }
        return false;
    }

    /**
     * 文件是否存在
     *
     * @param file 目标文件
     * @return true：存在，false：不存在
     */
    public static boolean isExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 删除指定文件
     *
     * @throws IOException
     */
    public static boolean deleteFile(File file) {
        return deleteFileOrDirectory(file);
    }

    /**
     * 删除指定文件，如果是文件夹，则递归删除
     *
     * @throws IOException
     */
    public static boolean deleteFileOrDirectory(File file) {
        try {
            if (file != null && file.isFile()) {
                return isExists(file) && file.delete();
            }
            if (file != null && file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 删除空文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                }
                return file.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteFileOrDirectory, Exception：", e);
        }
        return false;
    }

    /**
     * 关闭流
     *
     * @param stream Closeable
     */
    public static void closeQuietly(Closeable... stream) {
        if (stream != null && stream.length > 0) {
            try {
                for (int i = 0; i < stream.length; i++) {
                    Closeable closeable = stream[i];
                    closeable.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "closeQuietly, IOException：", e);
            }
        }
    }

}
