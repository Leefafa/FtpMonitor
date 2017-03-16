package com.fafa.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fafa.ftp.tools.Tools;
import com.fafa.session.MailSender;

public class sendMail {
	/**
     * ��������
     */
    private static MailSender serviceSms = null;
    private static Logger logger = LoggerFactory.getLogger(sendMail.class);
    private static long fileSize = Tools.FileTools.fileSize(path());
	public static void send() throws Exception{
		String Email = "";
		String EmailPwd = "";
		Object Content = "";
		String text = Tools.readString(System.getProperty("user.dir")+"\\Message.properties");
		List<String> readRecipient = new ArrayList<String>();
		List<String> senderProps = new ArrayList<String>();
		readRecipient = Tools.FileTools.readProperity(System.getProperty("user.dir")+"\\mail.properties");
		senderProps = Tools.FileTools.readProperity(System.getProperty("user.dir")+"\\sender.properties");
		for (String sender : senderProps) {
			String[] str = sender.split("-");
			if (str[0].equals("Email")) {
				Email = str[1];
			}
			if (str[0].equals("EmailPwd")) {
				EmailPwd = str[1];
			}
		}
		String flag = isChanged();
		if (flag != "" && Content != null) {
			logger.info(flag);
			Content = new File(flag);
			if (serviceSms == null) {
				serviceSms = new MailSender(Email, EmailPwd);
				serviceSms.sendFile(readRecipient, "FtpMonitor", text, (File)Content);
				logger.info("���ͳɹ�");
			}
		}
		
	}
	
	/**
	 * �ж�log�ļ���С�Ƿ����仯
	 * @return
	 */
    public static String isChanged() {
    	long size = Tools.FileTools.fileSize(path());
		if (size != fileSize) {
			fileSize = size;
			logger.info("filesize�����仯--" + size);
			return path();
		}
		return "";
	}
    /**
     * ����log�ļ�·��
     * @return
     */
    public static String path(){
    	String logPath = "";
		String fileName = "";
		String fileWarn = "";
		String fullPath = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
		logPath = System.getProperty("user.dir") + "\\FtpMonitorLog";
		//logger.info(logPath);
		fileName = sdf.format(new Date());
		fileWarn = "warn." + fileName + ".0.log";
		logger.info(fileWarn);
		fullPath = logPath + "\\" + fileWarn;
		return fullPath;
    }
}
