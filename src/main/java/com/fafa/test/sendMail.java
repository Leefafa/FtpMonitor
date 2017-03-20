package com.fafa.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fafa.ftp.tools.Tools;
import com.fafa.session.MailSession;

public class sendMail {
	/**
     * 服务邮箱
     */
    private static MailSession serviceSms = null;
    private static Logger logger = LoggerFactory.getLogger(sendMail.class);
    private static long fileSize = Tools.FileTools.fileSize(path());
	public static void send(){
		String Email = "";
		String EmailPwd = "";
		Object Content = "";
		String text = Tools.readString(System.getProperty("user.dir")+"\\Message.properties");
		List<String> readRecipient = new ArrayList<String>();
		List<String> senderProps = new ArrayList<String>();
		readRecipient = Tools.FileTools.readProperity(System.getProperty("user.dir")+"\\mail.properties");
		senderProps = Tools.FileTools.readProperity(System.getProperty("user.dir")+"\\sender.properties");
		
		//获取发送邮件者地址和邮箱密码
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
				serviceSms = new MailSession(Email, EmailPwd);
				try {
					String subject = serviceSms.receiveMail();
					if (subject.toLowerCase().equals("stop")) {
						logger.info("停止发送。。。");
						serviceSms = null;
					}else if(subject.toLowerCase().equals("start")){
						logger.info("发送中。。。");
						serviceSms.sendFile(readRecipient, "FtpMonitor", text, (File)Content);
						logger.info("发送done。。。");
						serviceSms = null;
						logger.info("发送成功");
					}else{
						logger.info("发送中。。。");
						serviceSms.sendFile(readRecipient, "FtpMonitor", text, (File)Content);
						logger.info("发送done。。。");
						serviceSms = null;
						logger.info("发送成功");
					}
				} catch (Exception e) {
					logger.error("发送失败。。。" + e.getMessage());
					serviceSms = null;
				}
			}
		}
	}
	
	/**
	 * 判断log文件大小是否发生变化
	 * @return
	 */
    public static String isChanged() {
    	long size = Tools.FileTools.fileSize(path());
		if (size != fileSize) {
			fileSize = size;
			logger.info("filesize发生变化--" + size);
			return path();
		}
		return "";
	}
    /**
     * 返回log文件路径
     * @return
     */
    public static String path(){
    	String logPath = "";
		String fileName = "";
		String fileWarn = "";
		String fullPath = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		logPath = System.getProperty("user.dir") + "\\FtpMonitorLog";
		//logger.info(logPath);
		fileName = sdf.format(new Date());
		fileWarn = "warn." + fileName + ".0.log";
		logger.info(fileWarn);
		fullPath = logPath + "\\" + fileWarn;
		return fullPath;
    }
}
