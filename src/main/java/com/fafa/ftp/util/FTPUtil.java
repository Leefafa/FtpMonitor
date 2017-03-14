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
  
    /** ftp服务器地址 */  
    private String host;  
    /** ftp 端口号 默认21 */  
    private int port = 21;  
    /** ftp服务器用户名 */  
    private String username;  
    /** ftp服务器密码 */  
    private String password;  
    /** ftp远程目录 */  
    private String remoteDir;  
    /** 本地存储目录 */  
    private String localDir;  
    /** 文件路径通配符 默认列出所有*/  
    private String regEx = "*";  
    /** 指定要下载的文件名 */  
    private String downloadFileName;  
  
    /** 
     * 设置连接属性 
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
     * 设置连接属性 
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
     * 连接FTP服务器 
     */  
    private FTPUtil connectServer() {  
        client = new FTPClient();  
        //设置超时时间  
        client.setDataTimeout(30000);  
        try {  
            // 1、连接服务器  
            if(!client.isConnected()){  
                // 如果采用默认端口，可以使用client.connect(host)的方式直接连接FTP服务器  
                client.connect(host, port);  
                // 登录  
                client.login(username, password);  
                // 获取ftp登录应答码  
                int reply = client.getReplyCode();  
                // 验证是否登陆成功  
                if (!FTPReply.isPositiveCompletion(reply)) {  
                    logger.info("未连接到FTP，用户名或密码错误。");  
                    client.disconnect();  
                    throw new RuntimeException("未连接到FTP，用户名或密码错误。");  
                } else {  
                    logger.info("FTP连接成功。IP:"+host +"PORT:" +port);  
                }  
                // 2、设置连接属性  
                client.setControlEncoding(encoding);  
                // 设置以二进制方式传输  
                client.setFileType(FTPClient.BINARY_FILE_TYPE);    
                client.enterLocalPassiveMode();  
            }  
        } catch (SocketException e) {  
            try {  
                client.disconnect();  
            } catch (IOException e1) {  
            }  
            logger.error("连接FTP服务器失败" + e.getMessage());  
            throw new RuntimeException("连接FTP服务器失败" + e.getMessage());  
        } catch (IOException e) {  
        }  
        return this;  
    }  
      
  
    /** 
     * 下载文件 
     */  
    public List<File> download(){  
          
        List<File> files = null;  
          
        this.connectServer();  
        InputStream is = null;  
        File downloadFile = null;  
        try {  
            // 1、设置远程FTP目录  
            client.changeWorkingDirectory(remoteDir);  
            logger.info("切换至工作目录【" + remoteDir + "】");  
            // 2、读取远程文件  
            FTPFile[] ftpFiles = client.listFiles(regEx);  
            if(ftpFiles.length==0) {  
                logger.warn("文件数为0，没有可下载的文件！");  
                return null;  
            }  
            logger.info("准备下载" + ftpFiles.length + "个文件");  
            // 3、保存文件到本地  
            for (FTPFile file : ftpFiles) {  
                //如果有指定下载的文件  
                if(!file.getName().equals(downloadFileName)){  
                    continue;  
                }  
                if(files == null) files = new ArrayList<File>();  
                is = client.retrieveFileStream(file.getName());  
                if(is==null) throw new RuntimeException("下载失败，检查文件是否存在");  
                downloadFile = new File(localDir + file.getName());  
                FileOutputStream fos = FileUtils.openOutputStream(downloadFile);  
                IOUtils.copy(is, fos);  
                client.completePendingCommand();  
                IOUtils.closeQuietly(is);  
                IOUtils.closeQuietly(fos);  
                  
                /* 
                //另外一种方式，供参考 
                OutputStream is = new FileOutputStream(localFile); 
                ftpClient.retrieveFile(ff.getName(), is); 
                is.close(); 
                */  
                  
                files.add(downloadFile);  
            }  
            logger.info("文件下载成功,下载文件路径：" + localDir);  
            return files;  
        } catch (IOException e) {  
            logger.error("下载文件失败" + e.getMessage());  
            throw new RuntimeException("下载文件失败" + e.getMessage());  
        }  
    }  
      
    /** 
     * 下载文件 
     * @param localDir 
     * @param remoteDir 
     */  
    public List<File> download(String remoteDir,String localDir){  
        this.remoteDir = remoteDir;  
        this.localDir = localDir;  
        return this.download();  
    }  
    /** 
     * 下载文件 
     * @param remoteDir 
     * @param regEx 文件通配符 
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
     * 下载文件 
     * @param downloadFileName 指定要下载的文件名称 
     * @return 
     */  
    public List<File> download(String downloadFileName){  
        this.downloadFileName = downloadFileName;  
        return this.download();  
    }  
      
    /** 
     * 上传文件 
     * @param files 
     */  
    public void upload(List<File> files){  
          
        OutputStream os = null;  
        try {  
            // 2、取本地文件  
            if(files == null || files.size()==0) {  
                logger.warn("文件数为0，没有找到可上传的文件");  
                return;  
            }  
            logger.info("准备上传" + files.size() + "个文件");  
            // 3、上传到FTP服务器  
            for(File file : files){  
                this.connectServer();  
                // 1、设置远程FTP目录  
                client.changeWorkingDirectory(remoteDir);  
                logger.info("切换至工作目录【" + remoteDir + "】");  
                os = client.storeFileStream(file.getName());  
                if(os== null) throw new RuntimeException("上传失败，请检查是否有上传权限");  
                IOUtils.copy(new FileInputStream(file), os);  
                IOUtils.closeQuietly(os);  
            }  
            logger.info("文件上传成功,上传文件路径：" + remoteDir);  
        } catch (IOException e) {  
            logger.error("上传文件失败" + e.getMessage());  
            throw new RuntimeException("上传文件失败" + e.getMessage());  
        }  
    }  
      
    public OutputStream getOutputStream(String fileName){  
        OutputStream os = null;  
        this.connectServer();  
        // 1、设置远程FTP目录  
        try {  
            client.changeWorkingDirectory(remoteDir);  
            logger.info("切换至工作目录【" + remoteDir + "】");  
            os = client.storeFileStream(fileName);  
            if(os== null) throw new RuntimeException("服务器上创建文件对象失败");  
            return os;  
        } catch (IOException e) {  
            logger.error("服务器上创建文件对象失败" + e.getMessage());  
            throw new RuntimeException("服务器上创建文件对象失败" + e.getMessage());  
        }  
    }  
    /** 
     * 上传文件 
     * @param files 上传的文件 
     * @param remoteDir 
     */  
    public void upload(List<File> files,String remoteDir){  
        this.remoteDir = remoteDir;  
        this.upload(files);  
    }  
      
    /** 
     * 上传文件 
     * @param file 
     */  
    public void upload(File file){  
        List<File> files = new ArrayList<File>();  
        files.add(file);  
        upload(files);  
    }  
      
    /** 
     * 判断文件在FTP上是否存在 
     * @param fileName 
     * @return 
     */  
    public boolean isFileExist(String fileName) {  
          
        boolean result = false;  
        this.connectServer();  
        try {  
            // 1、设置远程FTP目录  
            client.changeWorkingDirectory(remoteDir);  
            logger.info("切换至工作目录【" + remoteDir + "】");  
            // 2、读取远程文件  
            FTPFile[] ftpFiles = client.listFiles(regEx);  
            if(ftpFiles.length==0) {  
                logger.warn("文件数为0，没有可下载的文件！");  
                return result;  
            }  
            // 3、检查文件是否存在  
            for (FTPFile file : ftpFiles) {  
                if(file.getName().equals(fileName)){  
                    result = true;  
                    break;  
                }  
            }  
        } catch (Exception e) {  
            logger.error("检查文件是否存在失败" + e.getMessage());  
            throw new RuntimeException("检查文件是否存在失败" + e.getMessage());  
        }  
          
        return result;  
    }  
  
     /** 
     * 关闭连接 
     */  
    public void closeConnect() {  
        try {  
            client.disconnect();  
            logger.info(" 关闭FTP连接!!! ");  
        } catch (IOException e) {  
            logger.warn(" 关闭FTP连接失败!!! ",e);  
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
