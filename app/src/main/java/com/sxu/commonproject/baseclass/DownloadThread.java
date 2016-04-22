package com.sxu.commonproject.baseclass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread {
	private File saveFile;
	private URL downUrl;// 下载地址
	private int block;// 长度
	private int threadId = -1; // 下载线程
	private int downLength;// 已下载长度
	private int bufferSize = 1024 * 50;
	private boolean finish = false;
	private FileDownloader downloader;

	public DownloadThread(FileDownloader downloader, URL downUrl,
						  File saveFile, int block, int downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
	}

	@Override
	public void run() {
		if (block == 0 && downLength == 0) {
			/** 无法断点续传的文件 */
			try {
				HttpURLConnection connection = (HttpURLConnection) downUrl
						.openConnection();
				InputStream in = connection.getInputStream();
				FileOutputStream fout = new FileOutputStream(saveFile);
				byte[] buffer = new byte[bufferSize];
				int readed = 0;
				while ((readed = in.read(buffer)) != -1) {
					downLength += readed;
					fout.write(buffer, 0, readed);
					downloader.update(this.threadId, downLength);
				}
				in.close();
				fout.close();
				downloader.setNotFinish(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.downLength = -1;
			}
		} else {
			if (downLength < block) {// 未下载完成
				try {
					HttpURLConnection http = (HttpURLConnection) downUrl
							.openConnection();
					http.setConnectTimeout(20 * 1000);
					int startPos = block * (threadId - 1) + downLength;// 开始位置
					int endPos = block * threadId - 1;// 结束位置
					http.setRequestProperty("Range", "bytes=" + startPos + "-"
							+ endPos);// 设置获取实体数据的范围
					http.setRequestProperty("Connection", "close");

					InputStream inStream = http.getInputStream();
					BufferedInputStream bufferedInputStream = new BufferedInputStream(
							inStream);
					byte[] buffer = new byte[bufferSize];
					int offset = 0;
					RandomAccessFile threadfile = new RandomAccessFile(
							this.saveFile, "rwd");
					threadfile.seek(startPos);

					while ((offset = bufferedInputStream.read(buffer, 0,
							bufferSize)) != -1) {
						threadfile.write(buffer, 0, offset);
						downLength += offset;
						downloader.update(this.threadId, downLength);
						downloader.append(offset);
						if (!downloader.getState().equals(
								FileDownloader.DOWNLOADING)) {
							break;
						}
					}
					threadfile.close();
					bufferedInputStream.close();
					inStream.close();
					if (downloader.getState()
							.equals(FileDownloader.DOWNLOADING)) {
						this.finish = true;
					}
				} catch (Exception e) {
					this.downLength = -1;
				}
			}
		}

	}

	/**
	 * 下载是否完成
	 * 
	 * @return
	 */
	public boolean isFinish() {
		return finish;
	}

	/**
	 * 已经下载的内容大小
	 * 
	 * @return 如果返回值为-1,代表下载失败
	 */
	public long getDownLength() {
		return downLength;
	}
}
