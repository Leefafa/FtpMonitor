package com.fafa.simplemail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 此类是用作登录校验的，以确保你对该邮箱有发送邮件的权利
 * 重写里面的getPasswordAuthentication()方法
 * @Author Stark
 * @Date 2017年3月14日 下午12:34:13
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
