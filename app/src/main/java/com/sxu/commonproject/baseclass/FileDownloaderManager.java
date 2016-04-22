package com.sxu.commonproject.baseclass;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.HashMap;

public class FileDownloaderManager {
	private HashMap<String, Boolean> downingMap = new HashMap<String, Boolean>();
	public static FileDownloaderManager fileDownloaderManager;
	private String mDownloadDir;// 下载目录
	private int threadNum = 1;// 下载线程数
	private HashMap<String, FileDownloader> hashMap = new HashMap<String, FileDownloader>();

	public static FileDownloaderManager getInstance() {
		if (fileDownloaderManager == null) {
			fileDownloaderManager = new FileDownloaderManager();
		}
		return fileDownloaderManager;
	}

	private FileDownloaderManager() {
	}

	public void cancel(String url) {
		if (hashMap.get(url) != null) {
			hashMap.get(url).cleanListener();
			hashMap.get(url).stopDownload();
			hashMap.remove(url);
			downingMap.remove(url);
		}
	}

	public FileDownloader getFileDownloader(String url) {
		FileDownloader fileDownloader = null;
		if (hashMap.get(url) != null) {
			fileDownloader = hashMap.get(url);
		}
		return fileDownloader;
	}

	public void removeListener(String url,
			DownloadProgressListener downloadProgressListener) {
		if (hashMap.get(url) != null) {
			hashMap.get(url).removeListener(downloadProgressListener);
		}
	}

	public void removeFinishOrFailUrl(String url) {
		downingMap.remove(url);
	}

	public void download(final Context context,
						 final String versionControlFileName, final String downloadDir,
						 final String downloadUrl, final String version,
						 final DownloadProgressListener downloadProgressListener) {

		if (downingMap.get(downloadUrl) != null && downingMap.get(downloadUrl)) {
			FileDownloader fileDownloader = getFileDownloader(downloadUrl);
			if (fileDownloader != null) {
				fileDownloader.addListener(downloadProgressListener);
			}
			return;
		} else {
			downingMap.put(downloadUrl, true);
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (hashMap.get(downloadUrl) != null) {
						hashMap.get(downloadUrl).download(
								downloadProgressListener);
					} else {
						FileDownloader downloader = new FileDownloader(context,
								downloadUrl, new File(downloadDir), threadNum,
								version, versionControlFileName);
						hashMap.put(downloadUrl, downloader);
						downloader.download(downloadProgressListener);
					}
				} catch (DownloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.out);
					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (downloadProgressListener != null) {
								downloadProgressListener.onDownLoadFail();
							}
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.out);
				} finally {
					downingMap.remove(downloadUrl);
					hashMap.remove(downloadUrl);
				}
			}
		}).start();
	}

	public void download(final Context context, final String downloadUrl,
						 final String version, final String downloadDir,
						 final DownloadProgressListener downloadProgressListener) {
		threadNum = 2;
		mDownloadDir = downloadDir;
		download(context, "task.xml", mDownloadDir, downloadUrl, version,
				downloadProgressListener);
		threadNum = 1;
	}

	public String getSavePath() {
		return mDownloadDir;
	}
}
