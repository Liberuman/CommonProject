package com.sxu.commonproject.baseclass;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Function: TODO ADD FUNCTION
 *
 * @author yrguo
 * @Date 2012-12-14 下午1:14:37
 *
 * @see
 */
public class RecordDownloadInfo {

	private final String FILERECORD = "filerecord";// 文件记录
	private final String FILEVERSION = "fileversion";// 文件下载地址
	private final String FILEURL = "fileurl";// 文件下载地址
	private final String FILENAME = "filename";// 文件名
	private final String FILEPATH = "filepath";// 文件路径
	private final String FILESIZE = "filesize";// 文件大小
	private final String PIECES = "pieces";
	private final String PIECE = "piece";// 单个线程
	private final String PIECEID = "pieceid";// 线程id
	private final String START = "start";
	private final String END = "end";
	private final String CURRENT = "current";
	private Context context;
	private String versionControlFileName;

	// private static RecordDownloadInfo recordDownloadInfo;

	// public static RecordDownloadInfo getInstance(Context context)
	// {
	// if (recordDownloadInfo == null)
	// {
	// recordDownloadInfo = new RecordDownloadInfo(context);
	// }
	// return recordDownloadInfo;
	// }

	public RecordDownloadInfo(Context context, String versionControlFileName) {
		this.context = context;
		this.versionControlFileName = versionControlFileName;
	}

	public void deleteIfNeed() {
		File file = context.getFileStreamPath(versionControlFileName);
		if (file.exists()) {
			file.delete();
		}
	}

	public boolean isExists(String url, String version, int filesize) {

		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		InputStream inputStream = null;
		// 首先找到xml文件
		factory = DocumentBuilderFactory.newInstance();
		try {
			// 找到xml，并加载文档
			builder = factory.newDocumentBuilder();
			File taskFile = context.getFileStreamPath(versionControlFileName);
			if (taskFile.exists()) {
				inputStream = context.openFileInput(versionControlFileName);
				document = builder.parse(inputStream);
				// 找到根Element
				Element root = document.getDocumentElement();
				if (root.getAttribute(FILEURL).equals(url)
						&& root.getAttribute(FILEVERSION).equals(version)
						&& root.getAttribute(FILESIZE).equals("" + filesize)) {
					return true;
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 功能描述:创建下载记录
	 * 
	 * <pre>
	 *     yrguo:   2012-12-14      新建
	 * </pre>
	 *
	 * @param url
	 *            下载地址
	 * @param totalSize
	 *            文件长
	 * @param mData
	 *            线程下载记录
	 * @param length
	 *            每个线程下载长度
	 * @param fileName
	 *            文件名
	 * @param path
	 *            保存路径
	 */
	public void creatRecord(String url, int totalSize,
							HashMap<Integer, Integer> mData, int length, String fileName,
							String path, String version) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			// 创建xml根元素
			Element rootEle = doc.createElement(FILERECORD);
			rootEle.setAttribute(FILEVERSION, version);
			rootEle.setAttribute(FILEURL, url);
			rootEle.setAttribute(FILENAME, fileName);
			rootEle.setAttribute(FILEPATH, path);
			rootEle.setAttribute(FILESIZE, "" + totalSize);
			doc.appendChild(rootEle);
			// 创建xml二级元素
			Element groupEle = doc.createElement(PIECES);
			Set<Integer> threadIds = mData.keySet();
			for (int i : threadIds) {
				Element pieceEle = doc.createElement(PIECE);
				// personEle 的属性和属性值
				pieceEle.setAttribute(PIECEID, "" + i);
				pieceEle.setAttribute(START, "" + mData.get(i));
				pieceEle.setAttribute(END, "" + (mData.get(i) + length - 1));
				pieceEle.setAttribute(CURRENT, "0");
				groupEle.appendChild(pieceEle);
			}
			rootEle.appendChild(groupEle);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();

			DOMSource source = new DOMSource(doc);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// 创建文件存放在 /data/data/当前包/files
			FileOutputStream fos = context.openFileOutput(
					versionControlFileName, Context.MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
			fos.close();
			// System.out.println("生成XML文件成功!");
		} catch (ParserConfigurationException e) {
			// System.out.println(e.getMessage());
		} catch (TransformerConfigurationException e) {
			// System.out.println(e.getMessage());
		} catch (TransformerException e) {
			// System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			// System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 功能描述:更新文件
	 * 
	 * <pre>
	 *     yrguo:   2012-12-14      新建
	 * </pre>
	 *
	 * @param threadid
	 *            下载线程id
	 * @param postion
	 *            下载内容
	 */
	public void updatePieces(int threadid, int postion) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		InputStream inputStream = null;
		factory = DocumentBuilderFactory.newInstance();
		try {
			// 找到xml，并加载文档
			builder = factory.newDocumentBuilder();
			inputStream = context.openFileInput(versionControlFileName);
			document = builder.parse(inputStream);
			// 找到根Element
			Element root = document.getDocumentElement();
			NodeList list = root.getElementsByTagName(PIECE);
			for (int l = 0; l < list.getLength(); l++) {
				Node node = list.item(l);
				NamedNodeMap namedNodeMap = node.getAttributes();
				Node idNode = namedNodeMap.getNamedItem(PIECEID);
				if (idNode != null
						&& idNode.getNodeValue().equals("" + threadid)) {
					Node currentNode = namedNodeMap.getNamedItem(CURRENT);
					currentNode.setNodeValue("" + postion);
				}
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// 创建文件存放在 /data/data/当前包/files
			FileOutputStream fos = context.openFileOutput(
					versionControlFileName, Context.MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
			// System.out.println("更新XML文件成功!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 功能描述:解析现有记录
	 * 
	 * <pre>
	 *     yrguo:   2012-12-14      新建
	 * </pre>
	 *
	 * @param fileName
	 * @return
	 */
	public Map<Integer, Integer> readPieces() {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		InputStream inputStream = null;
		Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
		// 首先找到xml文件
		factory = DocumentBuilderFactory.newInstance();
		try {
			// 找到xml，并加载文档
			builder = factory.newDocumentBuilder();
			inputStream = context.openFileInput(versionControlFileName);
			document = builder.parse(inputStream);
			// 找到根Element
			Element root = document.getDocumentElement();
			NodeList list = root.getElementsByTagName(PIECE);
			for (int l = 0; l < list.getLength(); l++) {
				Node node = list.item(l);
				NamedNodeMap namedNodeMap = node.getAttributes();
				Node idNode = namedNodeMap.getNamedItem(PIECEID);
				Node currentNode = namedNodeMap.getNamedItem(CURRENT);
				// Node startNode = namedNodeMap.getNamedItem(START);
				// Node endNode = namedNodeMap.getNamedItem(END);
				data.put(Integer.valueOf(idNode.getNodeValue()),
						Integer.valueOf(currentNode.getNodeValue()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

}
