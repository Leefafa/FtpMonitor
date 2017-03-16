package com.fafa.simplemail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * ������������¼У��ģ���ȷ����Ը������з����ʼ���Ȩ��
 * ��д�����getPasswordAuthentication()����
 * @Author Stark
 * @Date 2017��3��14�� ����12:34:13
 * @File MailAuthenticator.java
 */
public class MailAuthenticator extends Authenticator {
	
	private String username;
	private String password;
	
	public MailAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
