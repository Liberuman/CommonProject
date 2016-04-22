package com.sxu.commonproject.baseclass;

import android.content.Context;

import com.sxu.commonproject.util.LogUtil;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/*******************************************************************************
 * FileName: FileDownload
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
public class FileDownload {

    private Context context;
    private String filePath;
    private String downloadUrl;
    private int threadCount = 5;
    private int fileSize;
    private int blockSize;
    private DownloadThread2[] downloadThread;

    private static final int DEFAULT_THREAD_COUNT = 4;

    public FileDownload(Context context, String filePath, String downloadUrl) {
        this(context, filePath, downloadUrl, DEFAULT_THREAD_COUNT);
    }

    public FileDownload(Context context, String filePath, String downloadUrl, int threadCount) {
        this.context = context;
        this.filePath = filePath;
        this.downloadUrl = downloadUrl;
        this.threadCount = threadCount != 0 ? threadCount : DEFAULT_THREAD_COUNT;
        downloadThread = new DownloadThread2[this.threadCount];
    }

    public void download() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                File saveFile = null;
                try {
                    URL url = new URL(downloadUrl);
                    URLConnection connection = url.openConnection();
                    inputStream = connection.getInputStream();
                    saveFile = new File(filePath);
                    fileSize = connection.getContentLength();
                    LogUtil.i("fileSize==" + fileSize);
                    blockSize = fileSize % threadCount == 0 ? fileSize / threadCount : fileSize / threadCount + 1;
                    for (int i = 0; i < threadCount; i++) {
                        downloadThread[i] = new DownloadThread2(context, saveFile, downloadUrl, i, blockSize);
                        downloadThread[i].start();
                    }
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
                }
            }
        });
    }


}
