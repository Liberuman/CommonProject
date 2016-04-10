package com.sxu.commonproject.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

	public static boolean hasStorage() {
		File dir = Environment.getExternalStorageDirectory();

		if (dir != null) {
			return true;
		} else {
			return false;
		}
	}
	
	// Sdcard
	public static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	public static String getPrivatePath(Context context) {
		return context.getCacheDir().getAbsolutePath();
	}

	public static void writeToFile(String filePath, byte[] bytes, boolean append) {
		try {
			File file = new File(filePath);
			if (!file.isFile()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, append);// openFileOutput(filePath,MODE_PRIVATE);
			fos.write(bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFileContent(String filePath) {
		StringBuilder sb = new StringBuilder();

		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return sb.toString();
	}

	public static boolean isExistFile(String strFile) {
		File f = new File(strFile);
		return f.exists();
	}

	public static void mkdir(String path) {
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
	}

	public static void mkdirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static boolean delete(String filePath) {
		LogUtil.i("FileUtil delete filePath:" + filePath);
		File file = new File(filePath);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[10240];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：
	 * @param newPath
	 *            String 复制后路径 如：
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[10240];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			LogUtil.i("文件夹复制失败");
		}

	}


	public static void copyFile(InputStream in, String outPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			FileOutputStream fs = new FileOutputStream(outPath);
			byte[] buffer = new byte[10240];
			while ((byteread = in.read(buffer)) != -1) {
				bytesum += byteread; // 字节数 文件大小
				fs.write(buffer, 0, byteread);
			}
			in.close();
		} catch (Exception e) {
			LogUtil.i("复制单个文件操作出错");
			e.printStackTrace();

		}

	}
}
