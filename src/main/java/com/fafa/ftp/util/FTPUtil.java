package com.fafa.ftp.util;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.net.SocketException;  
import java.util.ArrayList;  
import java.util.List;  
  
import org.apache.commons.io.FileUtils;  
import org.apache.commons.io.IOUtils;  
import org.apache.commons.net.ftp.FTPClient;  
import org.apache.commons.net.ftp.FTPFile;  
import org.apache.commons.net.ftp.FTPReply;  
import org.apache.log4j.Logger;  
  
public class FTPUtil {  
  
    private static final Logger logger = Logger.getLogger(FTPUtil.class);  
    private static String encoding = System.getProperty("file.encoding");  
      
    FTPClient client;  
  
    /** ftp��������ַ */  
    private String host;  
    /** ftp �˿ں� Ĭ��21 */  
    private int port = 21;  
    /** ftp�������û��� */  
    private String username;  
    /** ftp���������� */  
    private String password;  
    /** ftpԶ��Ŀ¼ */  
    private String remoteDir;  
    /** ���ش洢Ŀ¼ */  
    private String localDir;  
    /** �ļ�·��ͨ��� Ĭ���г�����*/  
    private String regEx = "*";  
    /** ָ��Ҫ���ص��ļ��� */  
    private String downloadFileName;  
  
    /** 
     * ������������ 
     *  
     * @param host 
     * @param username 
     * @param password 
     * @return 
     */  
    public FTPUtil setConfig(String host, String username, String password) {  
        this.host = host;  
        this.username = username;  
        this.password = password;  
        return this;  
    }  
  
    /** 
     * ������������ 
     *  
     * @param host 
     * @param port 
     * @param username 
     * @param password 
     */  
    public FTPUtil setConfig(String host, int port, String username,String password) {  
        this.host = host;  
        this.port = port;  
        this.username = username;  
        this.password = password;  
        return this;  
    }  
  
    /** 
     * ����FTP������ 
     */  
    private FTPUtil connectServer() {  
        client = new FTPClient();  
        //���ó�ʱʱ��  
        client.setDataTimeout(30000);  
        try {  
            // 1�����ӷ�����  
            if(!client.isConnected()){  
                // �������Ĭ�϶˿ڣ�����ʹ��client.connect(host)�ķ�ʽֱ������FTP������  
                client.connect(host, port);  
                // ��¼  
                client.login(username, password);  
                // ��ȡftp��¼Ӧ����  
                int reply = client.getReplyCode();  
                // ��֤�Ƿ��½�ɹ�  
                if (!FTPReply.isPositiveCompletion(reply)) {  
                    logger.info("δ���ӵ�FTP���û������������");  
                    client.disconnect();  
                    throw new RuntimeException("δ���ӵ�FTP���û������������");  
                } else {  
                    logger.info("FTP���ӳɹ���IP:"+host +"PORT:" +port);  
                }  
                // 2��������������  
                client.setControlEncoding(encoding);  
                // �����Զ����Ʒ�ʽ����  
                client.setFileType(FTPClient.BINARY_FILE_TYPE);    
                client.enterLocalPassiveMode();  
            }  
        } catch (SocketException e) {  
            try {  
                client.disconnect();  
            } catch (IOException e1) {  
            }  
            logger.error("����FTP������ʧ��" + e.getMessage());  
            throw new RuntimeException("����FTP������ʧ��" + e.getMessage());  
        } catch (IOException e) {  
        }  
        return this;  
    }  
      
  
    /** 
     * �����ļ� 
     */  
    public List<File> download(){  
          
        List<File> files = null;  
          
        this.connectServer();  
        InputStream is = null;  
        File downloadFile = null;  
        try {  
            // 1������Զ��FTPĿ¼  
            client.changeWorkingDirectory(remoteDir);  
            logger.info("�л�������Ŀ¼��" + remoteDir + "��");  
            // 2����ȡԶ���ļ�  
            FTPFile[] ftpFiles = client.listFiles(regEx);  
            if(ftpFiles.length==0) {  
                logger.warn("�ļ���Ϊ0��û�п����ص��ļ���");  
                return null;  
            }  
            logger.info("׼������" + ftpFiles.length + "���ļ�");  
            // 3�������ļ�������  
            for (FTPFile file : ftpFiles) {  
                //�����ָ�����ص��ļ�  
                if(!file.getName().equals(downloadFileName)){  
                    continue;  
                }  
                if(files == null) files = new ArrayList<File>();  
                is = client.retrieveFileStream(file.getName());  
                if(is==null) throw new RuntimeException("����ʧ�ܣ�����ļ��Ƿ����");  
                downloadFile = new File(localDir + file.getName());  
                FileOutputStream fos = FileUtils.openOutputStream(downloadFile);  
                IOUtils.copy(is, fos);  
                client.completePendingCommand();  
                IOUtils.closeQuietly(is);  
                IOUtils.closeQuietly(fos);  
                  
                /* 
                //����һ�ַ�ʽ�����ο� 
                OutputStream is = new FileOutputStream(localFile); 
                ftpClient.retrieveFile(ff.getName(), is); 
                is.close(); 
                */  
                  
                files.add(downloadFile);  
            }  
            logger.info("�ļ����سɹ�,�����ļ�·����" + localDir);  
            return files;  
        } catch (IOException e) {  
            logger.error("�����ļ�ʧ��" + e.getMessage());  
            throw new RuntimeException("�����ļ�ʧ��" + e.getMessage());  
        }  
    }  
      
    /** 
     * �����ļ� 
     * @param localDir 
     * @param remoteDir 
     */  
    public List<File> download(String remoteDir,String localDir){  
        this.remoteDir = remoteDir;  
        this.localDir = localDir;  
        return this.download();  
    }  
    /** 
     * �����ļ� 
     * @param remoteDir 
     * @param regEx �ļ�ͨ��� 
     * @param localDir 
     * @return 
     */  
    public List<File> download(String remoteDir,String regEx,String localDir){  
        this.remoteDir = remoteDir;  
        this.localDir = localDir;  
        this.regEx = regEx;  
        return this.download();  
    }  
      
    /** 
     * �����ļ� 
     * @param downloadFileName ָ��Ҫ���ص��ļ����� 
     * @return 
     */  
    public List<File> download(String downloadFileName){  
        this.downloadFileName = downloadFileName;  
        return this.download();  
    }  
      
    /** 
     * �ϴ��ļ� 
     * @param files 
     */  
    public void upload(List<File> files){  
          
        OutputStream os = null;  
        try {  
            // 2��ȡ�����ļ�  
            if(files == null || files.size()==0) {  
                logger.warn("�ļ���Ϊ0��û���ҵ����ϴ����ļ�");  
                return;  
            }  
            logger.info("׼���ϴ�" + files.size() + "���ļ�");  
            // 3���ϴ���FTP������  
            for(File file : files){  
                this.connectServer();  
                // 1������Զ��FTPĿ¼  
                client.changeWorkingDirectory(remoteDir);  
                logger.info("�л�������Ŀ¼��" + remoteDir + "��");  
                os = client.storeFileStream(file.getName());  
                if(os== null) throw new RuntimeException("�ϴ�ʧ�ܣ������Ƿ����ϴ�Ȩ��");  
                IOUtils.copy(new FileInputStream(file), os);  
                IOUtils.closeQuietly(os);  
            }  
            logger.info("�ļ��ϴ��ɹ�,�ϴ��ļ�·����" + remoteDir);  
        } catch (IOException e) {  
            logger.error("�ϴ��ļ�ʧ��" + e.getMessage());  
            throw new RuntimeException("�ϴ��ļ�ʧ��" + e.getMessage());  
        }  
    }  
      
    public OutputStream getOutputStream(String fileName){  
        OutputStream os = null;  
        this.connectServer();  
        // 1������Զ��FTPĿ¼  
        try {  
            client.changeWorkingDirectory(remoteDir);  
            logger.info("�л�������Ŀ¼��" + remoteDir + "��");  
            os = client.storeFileStream(fileName);  
            if(os== null) throw new RuntimeException("�������ϴ����ļ�����ʧ��");  
            return os;  
        } catch (IOException e) {  
            logger.error("�������ϴ����ļ�����ʧ��" + e.getMessage());  
            throw new RuntimeException("�������ϴ����ļ�����ʧ��" + e.getMessage());  
        }  
    }  
    /** 
     * �ϴ��ļ� 
     * @param files �ϴ����ļ� 
     * @param remoteDir 
     */  
    public void upload(List<File> files,String remoteDir){  
        this.remoteDir = remoteDir;  
        this.upload(files);  
    }  
      
    /** 
     * �ϴ��ļ� 
     * @param file 
     */  
    public void upload(File file){  
        List<File> files = new ArrayList<File>();  
        files.add(file);  
        upload(files);  
    }  
      
    /** 
     * �ж��ļ���FTP���Ƿ���� 
     * @param fileName 
     * @return 
     */  
    public boolean isFileExist(String fileName) {  
          
        boolean result = false;  
        this.connectServer();  
        try {  
            // 1������Զ��FTPĿ¼  
            client.changeWorkingDirectory(remoteDir);  
            logger.info("�л�������Ŀ¼��" + remoteDir + "��");  
            // 2����ȡԶ���ļ�  
            FTPFile[] ftpFiles = client.listFiles(regEx);  
            if(ftpFiles.length==0) {  
                logger.warn("�ļ���Ϊ0��û�п����ص��ļ���");  
                return result;  
            }  
            // 3������ļ��Ƿ����  
            for (FTPFile file : ftpFiles) {  
                if(file.getName().equals(fileName)){  
                    result = true;  
                    break;  
                }  
            }  
        } catch (Exception e) {  
            logger.error("����ļ��Ƿ����ʧ��" + e.getMessage());  
            throw new RuntimeException("����ļ��Ƿ����ʧ��" + e.getMessage());  
        }  
          
        return result;  
    }  
  
     /** 
     * �ر����� 
     */  
    public void closeConnect() {  
        try {  
            client.disconnect();  
            logger.info(" �ر�FTP����!!! ");  
        } catch (IOException e) {  
            logger.warn(" �ر�FTP����ʧ��!!! ",e);  
        }  
    }  
    public String getRemoteDir() {  
        return remoteDir;  
    }  
  
    public void setRemoteDir(String remoteDir) {  
        this.remoteDir = remoteDir;  
    }  
  
    public String getLocalPath() {  
        return localDir;  
    }  
  
    public void setLocalPath(String localPath) {  
        this.localDir = localPath;  
    }  
  
    public String getDownloadFileName() {  
        return downloadFileName;  
    }  
  
    public void setDownloadFileName(String downloadFileName) {  
        this.downloadFileName = downloadFileName;  
    }  
      
    @Override  
    public String toString() {  
        return "FTPUtil [host=" + host + ", port=" + port + ", username="  
                + username + ", password=" + password + "]";  
    }  
}  
