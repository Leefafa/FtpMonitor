package com.fafa.ftp.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fafa.ftp.util.CommonConstant;
import com.fafa.ftp.util.Utility;

/**
 * @Author Stark
 * @Date 2017年3月13日 下午4:59:44
 * @File Start.java
 */
public class Start {

	private static final Logger logger = LoggerFactory.getLogger(Start.class);
	
	public static void main(String[] args) {
		URL url = Start.class.getClassLoader().getResource("");
		writePidFile(url);
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:config" + CommonConstant.FILE_SEPARATOR
				+ "applicationContext.xml");
		SpringContextUtil springContext = new SpringContextUtil();
		springContext.setApplicationContext(ctx);
	}


	private static void writePidFile(URL url){
		String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        File file = new File(Utility.buildString(url.getPath(),CommonConstant.FILE_SEPARATOR, "pid.file"));
        BufferedWriter writer = null;
        try {
        	if (file.createNewFile()) {
        		writer = new BufferedWriter(new FileWriter(file));
				writer.write(pid);
			}
			
		} catch (IOException e) {
			logger.error("创建文件pid文件失败！" + e.getMessage());
		}finally{
			try {
				if(null!=writer){
					writer.close();	
				}
			} catch (IOException e) {
				
			}
		}
		
	}

}
