package com.sxu.commonproject.baseclass;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;


import com.sxu.commonproject.util.MD5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDownloader {
	public static final String DOWNLOADING = "DOWNLOADING";
	public static final String STOP = "STOP";
	public static final String CANCEL = "CANCEL";
	private String state;// 下载状态
	/* 已下载文件长度 */
	private int downloadSize = 0;
	/* 原始文件长度 */
	private int fileSize = 0;
	/* 线程数 */
	private DownloadThread[] threads;
	/* 本地保存文件 */
	private File saveFile;
	/* 本地保存文件 */
	private File saveTempFile;
	/* 缓存各线程下载的长度 */
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
	/* 每条线程下载的长度 */
	private int block;
	/* 下载路径 */
	private String downloadUrl;
	/* 断点续传 */
	private boolean canRestart = false;
	/* 是否finish */
	private boolean notFinish;
	private String fileName;
	private String tempFileName;
	public String version;
	private RecordDownloadInfo recordDownloadInfo;
	private ArrayList<DownloadProgressListener> downloadProgressListeners = new ArrayList<DownloadProgressListener>();

	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * 累计已下载大小
	 * 
	 * @param size
	 */
	protected synchronized void append(int size) {
		downloadSize += size;
	}

	/**
	 * 更新指定线程最后下载的位置
	 * 
	 * @param threadId
	 *            线程id
	 * @param pos
	 *            最后下载的位置
	 */
	protected synchronized void update(int threadId, int pos) {
		this.data.put(threadId, pos);
		this.recordDownloadInfo.updatePieces(threadId, pos);
	}

	/**
	 * 构建文件下载器
	 * 
	 * @param downloadUrl
	 *            下载路径
	 * @param fileSaveDir
	 *            文件保存目录
	 * @param threadNum
	 *            下载线程数
	 * @throws DownloadException
	 */
	public FileDownloader(Context context, String downloadUrl,
						  File fileSaveDir, int threadNum, String version,
						  String versionControlFileName) throws DownloadException {
		try {
			this.downloadUrl = downloadUrl;
			recordDownloadInfo = new RecordDownloadInfo(context,
					versionControlFileName);
			this.version = version;
			URL url = new URL(this.downloadUrl);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
			}
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(20 * 1000);
			// conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("HEAD");
			conn.connect();
			printResponseHeader(conn);
			if (conn.getResponseCode() == 200) {
				this.fileSize = conn.getContentLength();// 根据响应获取文件大小
				if (this.fileSize <= 0) {
					// 仅能单线程，且不可断点续传
					canRestart = false;
					fileSize = 0;
					throw new DownloadException();
				} else {
					canRestart = true;
				}
				if (version.equals("0.0")) {
					fileName = getMD5FileName(downloadUrl);// 获取文件名称
				} else {
					fileName = getFileName(conn);// 获取文件名称
				}
				tempFileName = fileName + ".tmp";
				this.saveFile = new File(fileSaveDir, fileName);// 构建保存文件
				this.saveTempFile = new File(fileSaveDir, tempFileName);
				if (canRestart) {
					if (checkFileDownloaded(version)) {
						// 如果为下载结束
						threads = new DownloadThread[0];
					} else {
						if (recordDownloadInfo.isExists(downloadUrl, version,
								fileSize) && saveTempFile.exists()) {
							// 如果为继续下载
							data = recordDownloadInfo.readPieces();
							threads = new DownloadThread[data.size()];
							this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize
									/ this.threads.length
									: this.fileSize / this.threads.length + 1;
						} else {
							threads = new DownloadThread[threadNum];
							data.clear();
							this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize
									/ this.threads.length
									: this.fileSize / this.threads.length + 1;
							HashMap<Integer, Integer> mData = new HashMap<Integer, Integer>();
							for (int i = 0; i < threadNum; i++) {
								data.put(i + 1, 0);
								mData.put(i + 1, block * i);
							}
							recordDownloadInfo.creatRecord(downloadUrl,
									fileSize, mData, block, tempFileName,
									fileSaveDir.getPath(), version);
						}
					}
				} else {
					if (checkFileDownloaded(version)) {
						// 如果为下载结束
						threads = new DownloadThread[0];
					} else {
						threads = new DownloadThread[1];
						data.clear();
						HashMap<Integer, Integer> mData = new HashMap<Integer, Integer>();
						for (int i = 0; i < 1; i++) {
							data.put(i + 1, 0);
							mData.put(i + 1, block * i);
						}
						recordDownloadInfo
								.creatRecord(downloadUrl, 0, mData, 0,
										tempFileName, fileSaveDir.getPath(),
										version);
					}

				}
				if (this.data.size() == this.threads.length) {// 下面计算所有线程已经下载的数据长度
					for (int i = 0; i < this.threads.length; i++) {
						this.downloadSize += this.data.get(i + 1);
					}
				}
			} else {
				throw new DownloadException();
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new DownloadException();
		}

	}

	public boolean checkFileDownloaded(String version) {
		boolean taskExist = recordDownloadInfo.isExists(downloadUrl, version,
				fileSize);
		if (taskExist) {
			if (saveFile.exists()) {
				long saveFileSize = saveFile.length();
				boolean fileLength = false;
				if (fileSize > 0) {
					fileLength = (fileSize == saveFileSize);
				} else {
					data = recordDownloadInfo.readPieces();
					long downTempSize = 0;
					for (int i = 0; i < this.data.size(); i++) {
						downTempSize += this.data.get(i + 1);
					}
					fileLength = (downTempSize == saveFileSize);
				}
				boolean tempDeleted = !saveTempFile.exists();
				return fileLength && tempDeleted;
			}
		}
		return false;

	}

	private String getMD5FileName(String url) {
		return MD5Util.toMD5(url);
	}

	/**
	 * 获取文件名
	 * 
	 * @param conn
	 * @return
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = this.downloadUrl.substring(this.downloadUrl
				.lastIndexOf('/') + 1);
		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) {
					break;
				}
				if ("content-disposition".equals(conn.getHeaderFieldKey(i)
						.toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(
							mine.toLowerCase());
					if (m.find()) {
						return m.group(1);
					}
				}
			}
			filename = UUID.randomUUID() + "";// 默认取一个文件名
		}
		return filename;

	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 开始下载文件
	 * 
	 * @param downloadProgressListener
	 *            监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */
	public int download(DownloadProgressListener downloadProgressListener)
			throws Exception {
		if (!downloadProgressListeners.contains(downloadProgressListener)) {
			downloadProgressListeners.add(downloadProgressListener);
		}
		if (checkFileDownloaded(version)) {
			// 如果为下载结束
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// FileDownloaderManager.getInstance().removeFinishOrFailUrl(downloadUrl);
					for (DownloadProgressListener listener : downloadProgressListeners) {
						if (listener != null) {
							listener.onDownloadFinish(saveFile
									.getAbsolutePath());// 通知目前已经下载完成的数据长度
						}
					}
				}
			});
		} else {
			try {
				state = DOWNLOADING;
				if (this.fileSize > 0) {
					RandomAccessFile randOut = new RandomAccessFile(
							this.saveTempFile, "rw");
					randOut.setLength(this.fileSize);
					randOut.close();
					URL url = new URL(this.downloadUrl);
					if (this.data.size() != this.threads.length) {
						this.data.clear();
						for (int i = 0; i < this.threads.length; i++) {
							this.data.put(i + 1, 0);// 初始化每条线程已经下载的数据长度为0
						}
					}
					for (int i = 0; i < this.threads.length; i++) {// 开启线程进行下载
						int downLength = this.data.get(i + 1);
						if (downLength < this.block
								&& this.downloadSize < this.fileSize) {// 判断线程是否已经完成下载,否则继续下载
							this.threads[i] = new DownloadThread(this, url,
									this.saveTempFile, this.block,
									this.data.get(i + 1), i + 1);
							this.threads[i].start();
						} else {
							this.threads[i] = null;
						}
					}
					if (downloadSize == fileSize) {
						moveTempFileToWholeFile(saveTempFile, saveFile);
						if ("0.0".equals(version)) {
							recordDownloadInfo.deleteIfNeed();
						}
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								// FileDownloaderManager.getInstance().removeFinishOrFailUrl(downloadUrl);
								for (DownloadProgressListener listener : downloadProgressListeners) {
									if (listener != null) {
										listener.onDownloadFinish(saveFile
												.getAbsolutePath());// 通知目前已经下载完成的数据长度
									}
								}
							}
						});

					} else {
						boolean notFinish = true;// 下载未完成
						while (notFinish) {// 循环判断所有线程是否完成下载
							Thread.sleep(900);
							notFinish = false;// 假定全部线程下载完成
							if (!state.equals(DOWNLOADING)) {
								break;
							}
							for (int i = 0; i < this.threads.length; i++) {
								if (this.threads[i] != null
										&& !this.threads[i].isFinish()) {// 如果发现线程未完成下载
									notFinish = true;// 设置标志为下载没有完成
									if (this.threads[i].getDownLength() == -1) {// 如果下载失败,再重新下载
										this.threads[i] = new DownloadThread(
												this, url, this.saveTempFile,
												this.block,
												this.data.get(i + 1), i + 1);
										this.threads[i].start();
									}
								}
							}
							Handler handler = new Handler(
									Looper.getMainLooper());
							handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									for (DownloadProgressListener listener : downloadProgressListeners) {
										if (listener != null) {
											listener.onDownloadSize(
													downloadSize, fileSize);// 通知目前已经下载完成的数据长度
										}
									}
								}
							});
						}
						if (downloadSize == fileSize) {
							moveTempFileToWholeFile(saveTempFile, saveFile);
							if ("0.0".equals(version)) {
								recordDownloadInfo.deleteIfNeed();
							}
							Handler handler = new Handler(
									Looper.getMainLooper());
							handler.post(new Runnable() {

								@Override
								public void run() {
									// FileDownloaderManager.getInstance().removeFinishOrFailUrl(downloadUrl);
									for (DownloadProgressListener listener : downloadProgressListeners) {
										if (listener != null) {
											listener.onDownloadFinish(saveFile
													.getAbsolutePath());// 通知目前已经下载完成
										}
									}
								}
							});
						}
					}
				} else {
					URL url = new URL(this.downloadUrl);
					if (this.data.size() != this.threads.length) {
						this.data.clear();
						for (int i = 0; i < this.threads.length; i++) {
							this.data.put(i + 1, 0);// 初始化每条线程已经下载的数据长度为0
						}
					}
					for (int i = 0; i < this.threads.length; i++) {// 开启线程进行下载
						this.threads[i] = new DownloadThread(this, url,
								this.saveTempFile, 0, 0, i + 1);
						this.threads[i].start();
					}
					notFinish = true;// 下载未完成
					while (notFinish) {// 循环判断所有线程是否完成下载
						Thread.sleep(900);
						notFinish = false;// 假定全部线程下载完成
						for (int i = 0; i < this.threads.length; i++) {
							if (this.threads[i] != null
									&& !this.threads[i].isFinish()) {// 如果发现线程未完成下载
								notFinish = true;// 设置标志为下载没有完成
								if (this.threads[i].getDownLength() == -1) {// 如果下载失败,再重新下载
									this.data.put(i, 0);
									this.threads[i] = new DownloadThread(this,
											url, this.saveTempFile, 0, 0, i + 1);
									this.threads[i].start();
								}
							}
						}
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {

							@Override
							public void run() {
								for (DownloadProgressListener listener : downloadProgressListeners) {
									if (listener != null) {
										listener.onDownloadSize(downloadSize,
												fileSize);// 通知目前已经下载完成的数据长度
									}
								}
							}
						});
					}
					if (state == DOWNLOADING) {
						moveTempFileToWholeFile(saveTempFile, saveFile);
						if ("0.0".equals(version)) {
							recordDownloadInfo.deleteIfNeed();
						}
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {

							@Override
							public void run() {
								// FileDownloaderManager.getInstance().removeFinishOrFailUrl(downloadUrl);
								for (DownloadProgressListener listener : downloadProgressListeners) {
									if (listener != null) {
										listener.onDownloadFinish(saveFile
												.getAbsolutePath());// 通知目前已经下载完成
									}
								}
							}
						});
					}
				}
			} catch (Exception e) {
				try {
					if (saveTempFile != null && saveTempFile.exists()) {
						saveTempFile.delete();
					}
				} catch (Exception e2) {
					// TODO: handle exception
					e.printStackTrace(System.out);
				}
				e.printStackTrace(System.out);
				throw new Exception("file download fail");
			}
		}
		return this.downloadSize;
	}

	private boolean moveTempFileToWholeFile(File sourceFile, File destFile) {
		// TODO Auto-generated method stub
		boolean copySuccess = false;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(sourceFile);
			out = new FileOutputStream(destFile);
			byte[] buf = new byte[1024 * 10];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			copySuccess = true;
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
			}
		}

		try {
			if (!copySuccess) {
				if (destFile != null && destFile.exists()) {
					destFile.delete();
				}
			} else {
				if (sourceFile != null && sourceFile.exists()) {
					sourceFile.delete();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(System.out);
		}
		return copySuccess;
	}

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(
			HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null) {
				break;
			}
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;

	}

	public void stopDownload() {
		state = STOP;
	}

	public void cancleDownload() {
		state = CANCEL;
	}

	public boolean isNotFinish() {
		return notFinish;
	}

	public void setNotFinish(boolean notFinish) {
		this.notFinish = notFinish;
	}

	public void removeListener(DownloadProgressListener listener) {
		downloadProgressListeners.remove(listener);
	}

	public void addListener(DownloadProgressListener listener) {
		if (!downloadProgressListeners.contains(listener)) {
			downloadProgressListeners.add(listener);
		}
	}

	/**
	 * 打印Http头字段
	 * 
	 * @param http
	 */

	public static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
		}
	}

	public void cleanListener() {
		// TODO Auto-generated method stub
		downloadProgressListeners.clear();
	}

	public String getSaveFilePath() {
		return saveFile == null ? "" : saveFile.getAbsolutePath();
	}
}