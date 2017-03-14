package com.fafa.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fafa.ftp.ftp.Ftp;
import com.fafa.ftp.tools.Tools;
import com.fafa.ftp.util.FTPUtil;

/**
 * @Author Stark
 * @Date 2017年3月13日 下午5:18:53
 * @File ftpTest.java
 */
public class ftpTest {
	private static Logger logger = Logger.getLogger(ftpTest.class);

	public void startMonitor() {
		int ftpPort = 21;
		String ftpUserName = "";
		String ftpPassword = "";
		String ftpHost = "";
		//String ftpPath = "";
		List<String> readEqpList = new ArrayList<String>();
		try {
			InputStream in = FTPUtil.class.getClassLoader().getResourceAsStream("ftp.properties");
			InputStream path = FTPUtil.class.getClassLoader().getResourceAsStream("path.properties");
			if (path == null) {
				logger.info("配置文件path.properties读取失败");
			}
			if (in == null) {
				logger.info("配置文件ftp.properties读取失败");
			} else {
				Properties properties = new Properties();
				properties.load(in);
				ftpUserName = properties.getProperty("ftpUserName");
				ftpPassword = properties.getProperty("ftpPassword");
				ftpHost = properties.getProperty("ftpHost");
				ftpPort = 21;
				//ftpPath = properties.getProperty("ftpPath");
		        readEqpList = Tools.FileTools.readProperity("./src/main/resources/path.properties");
			}
		} catch (IOException e) {
			logger.error("配置文件读取错误!" + e.getMessage());
		}
		Ftp ftp = new Ftp(ftpHost, ftpPort, ftpUserName, ftpPassword);
		ftp.ftpLogin();
		for (String path : readEqpList) {
			ftp.listPendingPath(path);
		}
		ftp.ftpLogOut();
		logger.info("-------------------------------------------");
	}
}
