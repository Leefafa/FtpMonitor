package com.fafa.session;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import com.fafa.simplemail.MailAuthenticator;

/**
 * 简单邮件发送器，可单发，群发
 * 
 * @Author Stark
 * @Date 2017年3月14日 下午12:41:50
 * @File MailSender.java
 */
public class MailSender {

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
	public MailSender(final String smtpHostName, final String username, final String password) {
		init(username, password, smtpHostName);
	}

	/**
	 * 初始化邮件发送器
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)，并以此解析SMTP服务器地址
	 * @param password
	 *            发送邮件的密码
	 */
	public MailSender(final String username, final String password) {
		// 针对mail.ncpd.com.cn邮箱服务
		final String smtpHostName = "mail." + username.split("@")[1];
		init(username, password, smtpHostName);
	}

	/**
	 * 初始化
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            密码
	 * @param smtpHostName
	 *            SMTP主机地址
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
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
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
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
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

	/*
	 * public void send(String recipient, SimpleMail mail) throws
	 * AddressException, MessagingException { send(recipient, mail.getSubject(),
	 * mail.getContent()); }
	 * 
	 * public void send(List<String> recipients, SimpleMail mail) throws
	 * AddressException, MessagingException { send(recipients,
	 * mail.getSubject(), mail.getContent()); }
	 */

}
