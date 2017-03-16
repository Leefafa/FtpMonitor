package com.fafa.test;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fafa.ftp.Ftp;
import com.fafa.ftp.tools.Tools;

/**
 * @Author Stark
 * @Date 2017年3月13日 下午5:18:53
 * @File ftpTest.java
 */
public class ftpTest {
	private static Logger logger = LoggerFactory.getLogger(ftpTest.class);
	
	public void startMonitor() {
		int ftpPort = 21;
		int ftpPendingCnt = 10;
		String ftpUserName = "";
		String ftpPassWord = "";
		String ftpHost = "";
		List<String> readEqpList = new ArrayList<String>();
		List<String> porps = new ArrayList<String>();
		try {
			readEqpList = Tools.FileTools.readProperity(System.getProperty("user.dir")+"\\path.properties");
			porps = Tools.FileTools.readProperity(System.getProperty("user.dir")+"\\ftp.properties");
			if (readEqpList == null) {
				logger.error("配置文件path.properties读取失败");
			}
			for (String p : porps) {
				String[] str = p.split("-");
				if (str[0].equals("ftpHost")) {
					ftpHost = str[1];
					logger.info("ftpHost---" + ftpHost);
				}
				if (str[0].equals("ftpUserName")) {
					ftpUserName = str[1];
					logger.info("ftpUserName---" + ftpUserName);
				}
				if (str[0].equals("ftpPassWord")) {
					ftpPassWord = str[1];
				}
				if (str[0].equals("ftpPendingCnt")) {
					ftpPendingCnt = Integer.parseInt(str[1]);
				}
			}
		} catch (Exception e) {
			logger.error("配置文件读取错误!" + e.getMessage());
		}
		Ftp ftp = new Ftp(ftpHost, ftpPort, ftpUserName, ftpPassWord);
		ftp.ftpLogin();
		for (String path : readEqpList) {
			ftp.listPendingPath(path, ftpPendingCnt);
		}
		ftp.ftpLogOut();
		logger.info("-------------------------------------------");
	}
}
