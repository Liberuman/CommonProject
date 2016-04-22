package com.sxu.commonproject.baseclass;

public interface DownloadProgressListener {
	public void onDownloadSize(int size, int totalSize);

	public void onDownloadFinish(String fileName);

	public void onDownLoadFail();
}
