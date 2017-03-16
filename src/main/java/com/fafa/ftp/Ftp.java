package com.fafa.ftp;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;
import java.util.TimeZone;  
import org.apache.commons.net.ftp.FTPClient;  
import org.apache.commons.net.ftp.FTPClientConfig;  
import org.apache.commons.net.ftp.FTPFile;  
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fafa.test.sendMail;
  
public class Ftp {  
    private FTPClient ftpClient;  
    private String strIp;  
    private int intPort;  
    private String user;  
    private String password;  
    
    private static Logger logger = LoggerFactory.getLogger(Ftp.class);
  
    /* * 
     * Ftp���캯�� 
     */  
    public Ftp(String strIp, int intPort, String user, String Password) {  
        this.strIp = strIp;  
        this.intPort = intPort;  
        this.user = user;  
        this.password = Password;  
        this.ftpClient = new FTPClient();  
    }  
    /** 
     * @return �ж��Ƿ����ɹ� 
     * */  
    public boolean ftpLogin() {  
        boolean isLogin = false;  
        FTPClientConfig ftpClientConfig = new FTPClientConfig();  
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());  
        this.ftpClient.setControlEncoding("GBK");  
        this.ftpClient.configure(ftpClientConfig);  
        try {  
            if (this.intPort > 0) {  
                this.ftpClient.connect(this.strIp, this.intPort);  
            } else {  
                this.ftpClient.connect(this.strIp);  
            }  
            // FTP���������ӻش�  
            int reply = this.ftpClient.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                this.ftpClient.disconnect();  
                logger.error("��¼FTP����ʧ�ܣ�");  
                return isLogin;  
            }else{
            	this.ftpClient.login(this.user, this.password);  
            	// ���ô���Э��  
            	this.ftpClient.enterLocalPassiveMode();  
            	this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            	logger.info("��ϲ" + this.user + "�ɹ���½FTP������");  
            	isLogin = true;  
            }
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error(this.user + "��¼FTP����ʧ�ܣ�" + e.getMessage());  
        }  
        this.ftpClient.setBufferSize(1024 * 2);  
        this.ftpClient.setDataTimeout(30 * 1000);  
        return isLogin;  
    }  
  
    /** 
     * @�˳��رշ��������� 
     * */  
    public void ftpLogOut() {  
        if (null != this.ftpClient && this.ftpClient.isConnected()) {  
            try {  
                boolean reuslt = this.ftpClient.logout();// �˳�FTP������  
                if (reuslt) {  
                    logger.info("�ɹ��˳�������");  
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.warn("�˳�FTP�������쳣��" + e.getMessage());  
            } finally {  
                try {  
                    this.ftpClient.disconnect();// �ر�FTP������������  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    logger.warn("�ر�FTP�������������쳣��");  
                } 
            }  
        }  
    }  
  
    /*** 
     * �ϴ�Ftp�ļ� 
     * @param localFile �����ļ� 
     * @param romotUpLoadePath�ϴ�������·�� - Ӧ����/���� 
     * */  
    
    /*
    public boolean uploadFile(File localFile, String romotUpLoadePath) {  
        BufferedInputStream inStream = null;  
        boolean success = false;  
        try {  
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// �ı乤��·��  
            inStream = new BufferedInputStream(new FileInputStream(localFile));  
            logger.info(localFile.getName() + "��ʼ�ϴ�.....");  
            success = this.ftpClient.storeFile(localFile.getName(), inStream);  
            if (success == true) {  
                logger.info(localFile.getName() + "�ϴ��ɹ�");  
                return success;  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            logger.error(localFile + "δ�ҵ�");  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (inStream != null) {  
                try {  
                    inStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return success;  
    }  
     */
    /*** 
     * �����ļ� 
     * @param remoteFileName   �������ļ����� 
     * @param localDires ���ص������Ǹ�·���� 
     * @param remoteDownLoadPath remoteFileName���ڵ�·�� 
     * */  
  /*
    public boolean downloadFile(String remoteFileName, String localDires,  
            String remoteDownLoadPath) {  
        String strFilePath = localDires + remoteFileName;  
        BufferedOutputStream outStream = null;  
        boolean success = false;  
        try {  
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);  
            outStream = new BufferedOutputStream(new FileOutputStream(  
                    strFilePath));  
            logger.info(remoteFileName + "��ʼ����....");  
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);  
            if (success == true) {  
                logger.info(remoteFileName + "�ɹ����ص�" + strFilePath);  
                return success;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error(remoteFileName + "����ʧ��");  
        } finally {  
            if (null != outStream) {  
                try {  
                    outStream.flush();  
                    outStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        if (success == false) {  
            logger.error(remoteFileName + "����ʧ��!!!");  
        }  
        return success;  
    }  
  */
    /*** 
     * @�ϴ��ļ��� 
     * @param localDirectory 
     *            �����ļ��� 
     * @param remoteDirectoryPath 
     *            Ftp ������·�� ��Ŀ¼"/"���� 
     * */ 
    /*
    public boolean uploadDirectory(String localDirectory,  
            String remoteDirectoryPath) {  
        File src = new File(localDirectory);  
        try {  
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";  
            this.ftpClient.makeDirectory(remoteDirectoryPath);  
            // ftpClient.listDirectories();  
        } catch (IOException e) {  
            e.printStackTrace();  
            logger.info(remoteDirectoryPath + "Ŀ¼����ʧ��");  
        }  
        File[] allFile = src.listFiles();  
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
            if (!allFile[currentFile].isDirectory()) {  
                String srcName = allFile[currentFile].getPath().toString();  
                uploadFile(new File(srcName), remoteDirectoryPath);  
            }  
        }  
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
            if (allFile[currentFile].isDirectory()) {  
                // �ݹ�  
                uploadDirectory(allFile[currentFile].getPath().toString(),  
                        remoteDirectoryPath);  
            }  
        }  
        return true;  
    }  
  */
    /*** 
     * @�����ļ��� 
     * @param localDirectoryPath���ص�ַ 
     * @param remoteDirectory Զ���ļ��� 
     * */  
    /*
    public boolean downLoadDirectory(String localDirectoryPath,String remoteDirectory) {  
        try {  
            String fileName = new File(remoteDirectory).getName();  
            localDirectoryPath = localDirectoryPath + fileName + "//";  
            new File(localDirectoryPath).mkdirs();  
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);  
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
                if (!allFile[currentFile].isDirectory()) {  
                    downloadFile(allFile[currentFile].getName(),localDirectoryPath, remoteDirectory);  
                }
            }  
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
                if (allFile[currentFile].isDirectory()) {  
                    String strremoteDirectoryPath = remoteDirectory + "/"+ allFile[currentFile].getName();  
                    downLoadDirectory(localDirectoryPath,strremoteDirectoryPath);  
                }
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
            logger.info("�����ļ���ʧ��");  
            return false;  
        }  
        return true;  
    }  
    */
    public void listPendingFiles(String remotePath, int setCount){
    	int ftpPendingNum = 0;
    	try {
			if (remotePath.startsWith("/") && remotePath.endsWith("/")) {
				FTPFile[] files = this.ftpClient.listFiles(remotePath);
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						logger.warn(remotePath + "---pendingĿ¼�°������ļ��У�" + files[i].getName());
					}else if (files[i].isFile()) {
						ftpPendingNum++;
					}
				}
				logger.info(remotePath + "--����--" + ftpPendingNum +"���ļ�");
				if (ftpPendingNum > setCount) {
					logger.warn(remotePath + "�ļ��������趨ֵ!---PendingFileCnt---" + ftpPendingNum);
				}
			}
		} catch (IOException e) {
			logger.error("��ȡpending�ļ�����" + e.getMessage());
		}
    }
    
    public void listPendingPath(String remotePath, int setCount){
    	try {
			if (remotePath.startsWith("/") && remotePath.endsWith("/")) {
				FTPFile[] files = this.ftpClient.listFiles(remotePath);
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						// logger.info(remotePath + files[i].getName());
						if (files[i].getName().equals("pending")) {
							remotePath = remotePath + files[i].getName() + "/";
//							logger.info("listPendingPath----" + remotePath);
							listPendingFiles(remotePath, setCount);
						}else{
							listPendingPath(remotePath + files[i].getName() + "/", setCount);
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("��ȡpending�ļ�·������" + e.getMessage());
		}
    }
    
    public boolean listDirectory(String remoteDirectory) {  
        try {  
            //String fileName = new File(remoteDirectory).getName();  
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);  
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
                if (allFile[currentFile].isDirectory()) {  
                	logger.info("��ȡ��"); 
                	String strremoteDirectoryPath = remoteDirectory + "/" + allFile[currentFile].getName();  
                	System.out.println(allFile[currentFile].getName());
                	listDirectory(strremoteDirectoryPath);
                }
            } 
        } catch (IOException e) {  
            e.printStackTrace();  
            logger.error("��ȡ�ļ���ʧ�ܣ�");  
            return false; 
        }  
        return true;  
    }  
    
    // FtpClient��Set �� Get ����  
    public FTPClient getFtpClient() {  
        return ftpClient;
    }
    public void setFtpClient(FTPClient ftpClient) {  
        this.ftpClient = ftpClient;  
    }  
}  
