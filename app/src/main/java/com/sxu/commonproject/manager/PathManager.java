package com.sxu.commonproject.manager;

import android.text.TextUtils;

import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.util.FileUtil;

import java.io.File;

public class PathManager {
	private static final String ROOT_PATH = ".yiqihai/";
	private static final String IMG_CACHE_DIR = "imgCache/";
	private static final String DATA_CACHE_DIR = "dataCache/";
	private static final String INFO_DIR = "info/";
	private static final String CRASH_DIR = "crash/";
	private static final String APKS_DIR = "apks/";
	private static final String FILES_DIR = "files/";

	private String _rootDir;
	private String _dataCacheDir;
	private String _imgCacheDir;
	private String _infoDir;
	private String _crashDir;
	private String _apksDir;
	private String _filesDir;

	private PathManager() {
		// mkdirs
		FileUtil.mkdirs(getRootDir());
		FileUtil.mkdirs(getImgCacheDir());
		FileUtil.mkdirs(getDataCacheDir());
		FileUtil.mkdirs(getInfoDir());
		FileUtil.mkdirs(getCrashDir());
		FileUtil.mkdirs(getApksDir());
	}

	public static PathManager getInstance() {
		return new PathManager();
	}

	public String getRootDir() {
		if (TextUtils.isEmpty(_rootDir)) {
			if (FileUtil.hasStorage()) {
				_rootDir = FileUtil.getSdcardPath();
			} else {
				_rootDir = FileUtil.getPrivatePath(CommonApplication.getInstance().getApplicationContext());
			}
			_rootDir += File.separator + ROOT_PATH + File.separator;
		}
		return _rootDir;
	}

	public String getImgCacheDir() {
		if (TextUtils.isEmpty(_imgCacheDir)) {
			_imgCacheDir = getRootDir() + IMG_CACHE_DIR;
		}
		return _imgCacheDir;
	}

	public String getDataCacheDir() {
		if (TextUtils.isEmpty(_dataCacheDir)) {
			_dataCacheDir = getRootDir() + DATA_CACHE_DIR;
		}
		return _dataCacheDir;
	}

	public String getInfoDir() {
		if (TextUtils.isEmpty(_infoDir)) {
			_infoDir = getRootDir() + INFO_DIR;
		}
		return _infoDir;
	}
	
	public String getCrashDir() {
		if (TextUtils.isEmpty(_crashDir)) {
			_crashDir = getRootDir() + CRASH_DIR;
		}
		return _crashDir;
	}
	
	public String getApksDir() {
		if (TextUtils.isEmpty(_apksDir)) {
			_apksDir = getRootDir() + APKS_DIR;
		}
		return _apksDir;
	}

	public String getFilesDir() {
		if (TextUtils.isEmpty(_filesDir)) {
			_filesDir = getRootDir() + FILES_DIR;
		}
		return _filesDir;
	}
}
