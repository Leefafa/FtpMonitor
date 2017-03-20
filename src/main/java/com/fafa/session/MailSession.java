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
 * ���ʼ����������ɵ�����Ⱥ��
 * 
 * @Author Stark
 * @Date 2017��3��14�� ����12:41:50
 * @File MailSender.java
 */
public class MailSession {
	private static Logger logger = LoggerFactory.getLogger(MailSession.class);
	/**
	 * �����ʼ���props�ļ�
	 */
	private final transient Properties props = System.getProperties();

	/**
	 * �ʼ���������¼��֤
	 */
	private transient MailAuthenticator authenticator;

	/**
	 * ����session
	 */
	private transient Session session;
	private transient Session sessionReceive = null;
	
	/**
	 * Store�ࣺ��Transport��һ����javax.mail.Store��Ҳ�̳���Java.mail.Service�ࡣ
	 * Store�����������ʼ����շ��������������ʼ����շ������ϵ�����С�
	 */
	private Store store = null; 

	/**
	 * ��ʼ���ʼ�������
	 * 
	 * @param smtpHostName
	 *            SMTP�ʼ���������ַ
	 * @param username
	 *            �����ʼ����û���(��ַ)
	 * @param password
	 *            �����ʼ�������
	 */
	public MailSession(final String hostName, final String username, final String password) {
		init(username, password, hostName);
		initReceive(username, password, hostName);
	}

	/**
	 * ��ʼ���ʼ�������
	 * 
	 * @param username
	 *            �����ʼ����û���(��ַ)�����Դ˽���SMTP��������ַ
	 * @param password
	 *            �����ʼ�������
	 */
	public MailSession(final String username, final String password) {
		// ���mail.ncpd.com.cn�������
		final String smtpHostName = "mail." + username.split("@")[1];
		init(username, password, smtpHostName);
		final String popHostName = "mail." + username.split("@")[1];
		initReceive(username, password, popHostName);
	}

	/**
	 * ��ʼ��
	 * 
	 * @param username
	 *            �����ʼ����û���(��ַ)
	 * @param password
	 *            ����
	 * @param smtpHostName
	 *            imap������ַ
	 */
	private void init(String username, String password, String smtpHostName) {
		// ��ʼ��props
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", smtpHostName);
		// ��֤
		authenticator = new MailAuthenticator(username, password);
		// ����session
		session = Session.getInstance(props, authenticator);
	}

	/**
	 * �����ʼ�
	 * 
	 * @param recipient
	 *            �ռ��������ַ
	 * @param subject
	 *            �ʼ�����
	 * @param content
	 *            �ʼ�����
	 */
	public void send(String recipient, String subject, Object content) {
		// ����mime�����ʼ�
		final MimeMessage message = new MimeMessage(session);
		try {
			// ���÷�����
			message.setFrom(new InternetAddress(authenticator.getUsername()));
			// �����ռ���
			message.setRecipient(RecipientType.TO, new InternetAddress(recipient));
			// ��������
			message.setSubject(subject);
			// �����ʼ���Ϣ���͵�ʱ��
			message.setSentDate(new Date());
			// �����ʼ�����
			message.setContent(content.toString(), "text/html;charset=utf-8");
			// ����
			Transport.send(message);
			logger.info("���ͳɹ�");
		} catch (Exception e) {
			logger.error("����ʧ�� " + e.getMessage());
		}
	}

	/**
	 * Ⱥ���ʼ�(������)
	 * 
	 * @param recipients
	 * @param subject
	 * @param content
	 * @throws Exception 
	 */
	public void sendFile(List<String> recipients, String subject, String text, File fileName) throws Exception {
		// ����mime�����ʼ�
		final MimeMessage message = new MimeMessage(session);
		try {
			// ���÷�����
			message.setFrom(new InternetAddress(authenticator.getUsername()));
			// �����ռ�����
			final int num = recipients.size();
			InternetAddress[] addresses = new InternetAddress[num];
			for (int i = 0; i < num; i++) {
				addresses[i] = new InternetAddress(recipients.get(i));
			}
			message.setRecipients(RecipientType.TO, addresses);
			// ��������
			message.setSubject(subject);
			// �����ʼ���Ϣ���͵�ʱ��
			message.setSentDate(new Date());
			
			/*
			// MiniMultipart����һ�������࣬����MimeBodyPart���͵Ķ���
			Multipart mainPart = new MimeMultipart();
			// ��Ӹ���
			// ����һ�µ�MimeBodyPart
			MimeBodyPart mdp = new MimeBodyPart();
			// �õ��ļ�����Դ
			FileDataSource fds = new FileDataSource(content);
			// �õ�������������BodyPart
			mdp.setDataHandler(new DataHandler(fds));
			// �õ��ļ���ͬ������BodyPart
			mdp.setFileName(fds.getName());
			mainPart.addBodyPart(mdp);
			*/

			// �����ʼ��ĸ��� MimeBodyPart ���� 
			MimeBodyPart content = createContent(text, fileName);
			// ���ʼ��и���������ϵ�һ��"mixed"�͵� MimeMultipart ���� 
		    MimeMultipart allPart = new MimeMultipart("mixed");
		    allPart.addBodyPart(content); 
			// �����ʼ�����
			message.setContent(allPart);
			// ����
			Transport.send(message);
		} catch (Exception e) {
			logger.error("����ʧ��" + e.getMessage());
		}
	}

	/**
	 * ���ݴ�����ʼ�����body���ļ�·������ͼ�Ĳ�ï�����Ĳ���
	 */
	public MimeBodyPart createContent(String text, File fileName) throws Exception {
		// ���ڱ����������Ĳ���
		MimeBodyPart contentBody = new MimeBodyPart();
		// MiniMultipart����һ�������࣬����MimeBodyPart���͵Ķ���
		Multipart mainPart = new MimeMultipart();
		// ��������ı���ͼƬ��"related"�͵�MimeMultipart����
//		MimeMultipart contentMulti = new MimeMultipart();

		// ���ĵ��ı�����
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(text, "text/html;charset=gbk");
		mainPart.addBodyPart(textBody);

		// ��Ӹ���
		// ����һ�µ�MimeBodyPart
		MimeBodyPart mdp = new MimeBodyPart();
		// �õ��ļ�����Դ
		FileDataSource fds = new FileDataSource(fileName);
		// �õ�������������BodyPart
		mdp.setDataHandler(new DataHandler(fds));
		// �õ��ļ���ͬ������BodyPart
		mdp.setFileName(fds.getName());
		mainPart.addBodyPart(mdp);

		// ������"related"�͵� MimeMultipart ������Ϊ�ʼ�������
		contentBody.setContent(mainPart);
		return contentBody;
	}

	
	public Store initReceive(String username, String password, String popHostName){
		 
		Properties props = new Properties();  
    	//�洢�����ʼ�������ʹ�õ�Э�飬POP3
    	props.setProperty("mail.store.protocol", "pop3");
    	props.setProperty("mail.pop3.host", popHostName);  
    	//���������½�һ���ʼ��Ự.
    	sessionReceive=Session.getInstance(props);  
    	//�ӻỰ�����л��POP3Э���Store����  
    	//�����Ҫ�鿴�����ʼ�����ϸ��Ϣ����Ҫ����Debug��־  
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
		// ����mime�����ʼ�
		//MimeMessage messages = new MimeMessage(sessionReceive);
		Folder folder = null;
        int messageCount = 0;  
        String subject = "";
        try {
			folder = store.getFolder("INBOX");//���ռ���  
			folder.open(Folder.READ_ONLY);//����ֻ��
			//����ʼ���Folder�ڵ������ʼ�����  
            messageCount = folder.getMessageCount();// ��ȡ�����ʼ�����  
            //��ȡ���ʼ�����  
            System.out.println("============>>�ʼ�������"+messageCount);  
            if(messageCount > 0){  
                Message[] messages = folder.getMessages(messageCount,messageCount);//��ȡ�����һ���ʼ�  
                for(int i = 0;i < messages.length;i++) {
                    logger.info("=====================>>��ʼ��ʾ�ʼ�����<<=====================");  
                    logger.info("������: " + getFrom(messages[i])); 
                    subject = getSubject(messages[i]);
                    logger.info("����: " + getSubject(messages[i]));  
                    logger.info("����ʱ��: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((MimeMessage) messages[i]).getSentDate()));  
                    logger.info("=====================>>������ʾ�ʼ�����<<=====================");  
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
    * ����ʼ�����    
    * @param message��Message 
    * @return �ʼ�����   
    */  
   private String getSubject(Message message) throws Exception {  
       String subject = "";  
       if(((MimeMessage) message).getSubject() != null){  
           subject = MimeUtility.decodeText(((MimeMessage) message).getSubject());// ���ʼ��������    
       }  
       return subject;      
   }  
   
   /** 
    * ��÷����˵ĵ�ַ 
    * @param message��Message 
    * @return �����˵ĵ�ַ 
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
