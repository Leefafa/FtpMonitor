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
     * �ַ���������
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
         * �ļ�������
         * @param path �ļ�Ŀ¼
         * @param oldName  ԭ�����ļ���
         * @param newName ���ļ���
         */
        public static void renameFile(String path, String oldName, String newName) {

            //�µ��ļ�������ǰ�ļ�����ͬʱ,���б�Ҫ����������
            if(!oldName.equals(newName)){
                File oldFile=new File(path + "/" + oldName);
                File newFile=new File(path + "/" + newName);

                // �������ļ�������
                if(!oldFile.exists()){
                    return;
                }

                //���ڸ�Ŀ¼���Ѿ���һ���ļ������ļ�����ͬ��������������
                if(newFile.exists()) {
                    System.out.println(newName + "�Ѿ�����.");
                } else{
                    oldFile.renameTo(newFile);
                }
            }else{
                System.out.println("���ļ����;��ļ�����ͬ");
            }
        }
        
        public static List<String> readProperity(String file){
        	Properties prop = new Properties();  
        	List<String> eqpList = new ArrayList<String>();
            try{
                //��ȡ�����ļ�a.properties
                InputStream in = new BufferedInputStream (new FileInputStream(file));
                prop.load(in);     ///���������б�
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
