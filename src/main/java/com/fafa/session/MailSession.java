package com.fafa.session;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fafa.simplemail.MailAuthenticator;

/**
 * 简单邮件发送器，可单发，群发
 * 
 * @Author Stark
 * @Date 2017年3月14日 下午12:41:50
 * @File MailSender.java
 */
public class MailSession {
	private static Logger logger = LoggerFactory.getLogger(MailSession.class);
	/**
	 * 发送邮件的props文件
	 */
	private final transient Properties props = System.getProperties();

	/**
	 * 邮件服务器登录验证
	 */
	private transient MailAuthenticator authenticator;

	/**
	 * 邮箱session
	 */
	private transient Session session;
	private transient Session sessionReceive = null;
	
	/**
	 * Store类：与Transport类一样，javax.mail.Store类也继承了Java.mail.Service类。
	 * Store类用于连接邮件接收服务器，并访问邮件接收服务器上的邮箱夹。
	 */
	private Store store = null; 

	/**
	 * 初始化邮件发送器
	 * 
	 * @param smtpHostName
	 *            SMTP邮件服务器地址
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            发送邮件的密码
	 */
	public MailSession(final String hostName, final String username, final String password) {
		init(username, password, hostName);
		initReceive(username, password, hostName);
	}

	/**
	 * 初始化邮件发送器
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)，并以此解析SMTP服务器地址
	 * @param password
	 *            发送邮件的密码
	 */
	public MailSession(final String username, final String password) {
		// 针对mail.ncpd.com.cn邮箱服务
		final String smtpHostName = "mail." + username.split("@")[1];
		init(username, password, smtpHostName);
		final String popHostName = "mail." + username.split("@")[1];
		initReceive(username, password, popHostName);
	}

	/**
	 * 初始化
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            密码
	 * @param smtpHostName
	 *            imap主机地址
	 */
	private void init(String username, String password, String smtpHostName) {
		// 初始化props
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", smtpHostName);
		// 验证
		authenticator = new MailAuthenticator(username, password);
		// 创建session
		session = Session.getInstance(props, authenticator);
	}

	/**
	 * 发送邮件
	 * 
	 * @param recipient
	 *            收件人邮箱地址
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 */
	public void send(String recipient, String subject, Object content) {
		// 创建mime类型邮件
		final MimeMessage message = new MimeMessage(session);
		try {
			// 设置发信人
			message.setFrom(new InternetAddress(authenticator.getUsername()));
			// 设置收件人
			message.setRecipient(RecipientType.TO, new InternetAddress(recipient));
			// 设置主题
			message.setSubject(subject);
			// 设置邮件消息发送的时间
			message.setSentDate(new Date());
			// 设置邮件内容
			message.setContent(content.toString(), "text/html;charset=utf-8");
			// 发送
			Transport.send(message);
			logger.info("发送成功");
		} catch (Exception e) {
			logger.error("发送失败 " + e.getMessage());
		}
	}

	/**
	 * 群发邮件(带附件)
	 * 
	 * @param recipients
	 * @param subject
	 * @param content
	 * @throws Exception 
	 */
	public void sendFile(List<String> recipients, String subject, String text, File fileName) throws Exception {
		// 创建mime类型邮件
		final MimeMessage message = new MimeMessage(session);
		try {
			// 设置发信人
			message.setFrom(new InternetAddress(authenticator.getUsername()));
			// 设置收件人们
			final int num = recipients.size();
			InternetAddress[] addresses = new InternetAddress[num];
			for (int i = 0; i < num; i++) {
				addresses[i] = new InternetAddress(recipients.get(i));
			}
			message.setRecipients(RecipientType.TO, addresses);
			// 设置主题
			message.setSubject(subject);
			// 设置邮件消息发送的时间
			message.setSentDate(new Date());
			
			/*
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 添加附件
			// 创建一新的MimeBodyPart
			MimeBodyPart mdp = new MimeBodyPart();
			// 得到文件数据源
			FileDataSource fds = new FileDataSource(content);
			// 得到附件本身并至入BodyPart
			mdp.setDataHandler(new DataHandler(fds));
			// 得到文件名同样至入BodyPart
			mdp.setFileName(fds.getName());
			mainPart.addBodyPart(mdp);
			*/

			// 创建邮件的各个 MimeBodyPart 部分 
			MimeBodyPart content = createContent(text, fileName);
			// 将邮件中各个部分组合到一个"mixed"型的 MimeMultipart 对象 
		    MimeMultipart allPart = new MimeMultipart("mixed");
		    allPart.addBodyPart(content); 
			// 设置邮件内容
			message.setContent(allPart);
			// 发送
			Transport.send(message);
		} catch (Exception e) {
			logger.error("发送失败" + e.getMessage());
		}
	}

	/**
	 * 根据传入的邮件正文body和文件路径创建图文并茂的正文部分
	 */
	public MimeBodyPart createContent(String text, File fileName) throws Exception {
		// 用于保存最终正文部分
		MimeBodyPart contentBody = new MimeBodyPart();
		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart mainPart = new MimeMultipart();
		// 用于组合文本和图片，"related"型的MimeMultipart对象
//		MimeMultipart contentMulti = new MimeMultipart();

		// 正文的文本部分
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(text, "text/html;charset=gbk");
		mainPart.addBodyPart(textBody);

		// 添加附件
		// 创建一新的MimeBodyPart
		MimeBodyPart mdp = new MimeBodyPart();
		// 得到文件数据源
		FileDataSource fds = new FileDataSource(fileName);
		// 得到附件本身并至入BodyPart
		mdp.setDataHandler(new DataHandler(fds));
		// 得到文件名同样至入BodyPart
		mdp.setFileName(fds.getName());
		mainPart.addBodyPart(mdp);

		// 将上面"related"型的 MimeMultipart 对象作为邮件的正文
		contentBody.setContent(mainPart);
		return contentBody;
	}

	
	public Store initReceive(String username, String password, String popHostName){
		 
		Properties props = new Properties();  
    	//存储接收邮件服务器使用的协议，POP3
    	props.setProperty("mail.store.protocol", "pop3");
    	props.setProperty("mail.pop3.host", popHostName);  
    	//根据属性新建一个邮件会话.
    	sessionReceive=Session.getInstance(props);  
    	//从会话对象中获得POP3协议的Store对象  
    	//如果需要查看接收邮件的详细信息，需要设置Debug标志  
    	sessionReceive.setDebug(false);  
		try {
			store = sessionReceive.getStore();  
			store.connect(username,password);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return store;
	}
	public String receiveMail(){
		// 创建mime类型邮件
		//MimeMessage messages = new MimeMessage(sessionReceive);
		Folder folder = null;
        int messageCount = 0;  
        String subject = "";
        try {
			folder = store.getFolder("INBOX");//打开收件箱  
			folder.open(Folder.READ_ONLY);//设置只读
			//获得邮件夹Folder内的所有邮件个数  
            messageCount = folder.getMessageCount();// 获取所有邮件个数  
            //获取新邮件处理  
            System.out.println("============>>邮件总数："+messageCount);  
            if(messageCount > 0){  
                Message[] messages = folder.getMessages(messageCount,messageCount);//读取最近的一封邮件  
                for(int i = 0;i < messages.length;i++) {
                    logger.info("=====================>>开始显示邮件内容<<=====================");  
                    logger.info("发送人: " + getFrom(messages[i])); 
                    subject = getSubject(messages[i]);
                    logger.info("主题: " + getSubject(messages[i]));  
                    logger.info("发送时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((MimeMessage) messages[i]).getSentDate()));  
                    logger.info("=====================>>结束显示邮件内容<<=====================");  
                }  
            }  
			
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{  
            if(folder != null && folder.isOpen()){  
                try {  
                    folder.close(true);  
                } catch (MessagingException e) {  
                    e.printStackTrace();  
                }  
            }  
            if(store.isConnected()){  
                try {  
                    store.close();  
                } catch (MessagingException e) {  
                    e.printStackTrace();  
                }  
            }  
        }
		return subject;  
        
	}
	
    /** 
    * 获得邮件主题    
    * @param message：Message 
    * @return 邮件主题   
    */  
   private String getSubject(Message message) throws Exception {  
       String subject = "";  
       if(((MimeMessage) message).getSubject() != null){  
           subject = MimeUtility.decodeText(((MimeMessage) message).getSubject());// 将邮件主题解码    
       }  
       return subject;      
   }  
   
   /** 
    * 获得发件人的地址 
    * @param message：Message 
    * @return 发件人的地址 
    */  
   private String getFrom(Message message) throws Exception {    
       InternetAddress[] address = (InternetAddress[]) ((MimeMessage) message).getFrom();      
       String from = address[0].getAddress();      
       if (from == null){  
           from = "";  
       }  
       return from;      
   }  

}
