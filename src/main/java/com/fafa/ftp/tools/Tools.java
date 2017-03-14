package com.fafa.ftp.tools;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


public class Tools {
    public static class NetTools {

        public static String hostAddress() {
            String address = "";
            try {
                InetAddress addr =  InetAddress.getLocalHost();
                address = addr.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            return address;
        }
    }

    /**
     * 字符串工具类
     */
    public static class StringTools {
        public static boolean isEmpty(String label) {
            if (label == null || label.length() == 0) {
                return true;
            }
            return false;
        }

        public static String reversal(String label) {
            if (isEmpty(label)) {
                return "";
            }

            return new StringBuffer(label).reverse().toString();
        }

        public static boolean isPalindrome(String label) {
            if (isEmpty(label)) {
                return false;
            }

            return reversal(label).equals(label);
        }
    }

    public static class FileTools {

        /**
         * 文件重命名
         * @param path 文件目录
         * @param oldName  原来的文件名
         * @param newName 新文件名
         */
        public static void renameFile(String path, String oldName, String newName) {

            //新的文件名和以前文件名不同时,才有必要进行重命名
            if(!oldName.equals(newName)){
                File oldFile=new File(path + "/" + oldName);
                File newFile=new File(path + "/" + newName);

                // 重命名文件不存在
                if(!oldFile.exists()){
                    return;
                }

                //若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                if(newFile.exists()) {
                    System.out.println(newName + "已经存在.");
                } else{
                    oldFile.renameTo(newFile);
                }
            }else{
                System.out.println("新文件名和旧文件名相同");
            }
        }
        
        public static List<String> readProperity(String file){
        	Properties prop = new Properties();  
        	List<String> eqpList = new ArrayList<String>();
            try{
                //读取属性文件a.properties
                InputStream in = new BufferedInputStream (new FileInputStream(file));
                prop.load(in);     ///加载属性列表
                Iterator<String> it=prop.stringPropertyNames().iterator();
                while(it.hasNext()){
                    String key=it.next();
                    eqpList.add(key);
                }
                in.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
			return eqpList;
        }
    }
}
