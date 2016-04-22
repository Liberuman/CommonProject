package com.sxu.commonproject.baseclass;

import android.content.Context;
import android.net.Uri;

import com.sxu.commonproject.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/*******************************************************************************
 * FileName: DownloadThread2
 * <p/>
 * Description:
 * <p/>
 * Author: juhg
 * <p/>
 * Version: v1.0
 * <p/>
 * Date: 16/4/12
 * <p/>
 * Copyright: all rights reserved by zhinanmao.
 *******************************************************************************/
public class DownloadThread2 extends Thread {

    private Context context;
    private File saveFile;
    private String downloadUrl;
    private int index;
    private int blockSize;

    private static final int BUFFER_SIZE = 1024;

    public DownloadThread2(Context context, File saveFile, String downloadUrl, int index, int blockSize) {
        this.context = context;
        this.saveFile = saveFile;
        this.downloadUrl = downloadUrl;
        this.index = index;
        this.blockSize = blockSize;
    }

    public void download() {
        InputStream inputStream = null;
        RandomAccessFile itemFile = null;
        try {
            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            int startPos = (index-1) * blockSize;
            int endPos = index * blockSize - 1;
            connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            LogUtil.i("Range==" + startPos + "-" + endPos);
            inputStream = connection.getInputStream();
            itemFile = new RandomAccessFile(saveFile, "rwd");
            itemFile.seek(startPos);
            int readSize;
            byte []buffer = new byte[BUFFER_SIZE];
            while ((readSize = inputStream.read(buffer, 0, 1024)) != -1) {
                itemFile.write(buffer, 0, readSize);
            }
            stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (saveFile != null) {
                    itemFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        download();
    }
}
