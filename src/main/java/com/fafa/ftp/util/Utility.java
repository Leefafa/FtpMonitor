package com.fafa.ftp.util;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Stark
 * @Date 2017年3月13日 下午5:05:27
 * @File Utility.java
 */
public class Utility {
	
	/**
	 * 日期格式种类
	 */
	public static enum DatePattern {
		HourMin("HHmm"), 
		HourMinSec("HHmmss"), 
		HourMinSecWithColon("HH:mm:ss"), 
		MonthDayHour("MMddHH"), 
		YearMonth("yyyy/MM"), 
		YearMonthDay("yyyy/MM/dd"), 
		YearMonthNoSlash("yyyyMM"), 
		TwoDigitYearMonthDay("yyMMdd"), 
		TwoDigitYearMonthDayWithSlash("yy/MM/dd"), 
		YearMonthDayNoSlash("yyyyMMdd"), 
		YearMonthDayWithDash("yyyy-MM-dd"), 
		YearMonthDayHourMin("yyyy/MM/dd HH:mm"), 
		YearMonthDayHourMinSec("yyyy/MM/dd HH:mm:ss"), 
		YearMonthDayHourMinSecColonMSec("yyyy/MM/dd HH:mm:ss:SSS"), 
		YearMonthDayHourMinSecMsec("yyyyMMddHHmmssSSS"), 
		YearMonthDayHourMinSecWithDash("yyyy-MM-dd HH:mm:ss"), 
		YearMonthDayHourMinSecMsecWithSlash("yyyy/MM/dd HH:mm:ss.SSS"), 
		YearMonthDayHourMinSecDotMsec("yyyyMMddHHmmss.SSS"), 
		YearMonthDayHourMinSecNoDelimiter("yyyyMMddHHmmss"), 
		YearMonthDayHourMinSecWeekNoDelimiter("yyyyMMddHHmmssFF"), 
		YearMonthDayHourMinNoDelimiter("yyyyMMddHHmm"), 
		YearMonthDayNoSpaceHourMinSec("yyyy/MM/ddHH:mm:ss");

		private String pattern;

		public String getPattern() {
			return pattern;
		}

		private DatePattern(String value) {
			this.pattern = value;
		}
	}

	// 不规则字符
	private static final String IRREGULAR_STRING = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}]";


	/**
	 * Getter的首字母大写化
	 * 
	 * @param fildeName
	 *            域名
	 * @return 域名(首字母大写)
	 * @throws Exception
	 */
	public static String getMethodName(String fildeName) throws Exception {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	/**
	 * 用指定的分隔符将字符串分成数组<br>
	 * 
	 * @param str
	 *            要分割的字符串(必须项)
	 * @param delimiter
	 *            指定的分隔符
	 * @return 分割好的字符串数组
	 */
	public static String[] split(String str, String delimiter) {
		return str.split(delimiter, -1);
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmptyString(String str) {
		boolean result = false;
		if (str == null || "".equals(str.trim())) {
			result = true;
		}
		return result;
	}

	/**
	 * 转换Object为Integer
	 * 
	 * @param obj
	 * @return
	 */
	public static Integer convertObjToInteger(Object obj) {
		if (obj == null) {
			return null;
		} else {
			return Integer.valueOf(String.valueOf(obj));
		}
	}

	/**
	 * 转换String型的列号为Int 约定规则: 列的序号从1开始
	 * 
	 * @param str
	 *            字符串
	 * @return 0:非列的序号; 其他:返回转好的列号
	 */
	public static int convertStringColNumToInt(String str) {
		str = str.trim();
		int result = 0;
		if (str == null) {
			return 0;
		}
		try {
			result = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return 0;
		}
		return result;
	}

	/**
	 * 用指定的分隔符将字符串分成数组<br>
	 * 
	 * @param str
	 *            要分割的字符串(必须项)
	 * @param delimiter
	 *            指定的分隔符
	 * @return 分割好的字符串数组
	 */
	public static String[] splitIntoStrings(String str, String delimiter) {
		if (str.startsWith(delimiter)) {
			str = str.substring(str.indexOf(delimiter) + 1, str.length());
		}
		if (delimiter.matches(IRREGULAR_STRING)) {
			delimiter = "\\" + delimiter;
		}

		String[] strings = str.split(delimiter, -1);
		return strings;
	}

	/**
	 * 截取部分字符串<BR>
	 * nullまたは空文字列が渡された場合、nullを返す, 指定した位置で文字列取得できなかった場合、nullを返す<br>
	 * 例："hamburger".substring(4, 8) returns "urge"
	 * 
	 * @param str
	 *            字符串（原）
	 * @param beginIndex
	 *            开始位置(0~length-1)
	 * @param endIndex
	 *            结束位置(1~length)
	 * @return 结果(长度=endIndex-beginIndex)
	 */
	public static String subStringUtility(String str, int beginIndex,
			int endIndex) {
		String result = null;
		int strLength = 0;

		if (!isEmptyString(str)) {
			strLength = str.length();
			if (beginIndex >= 0 && beginIndex < strLength && endIndex >= 0
					&& endIndex > beginIndex) {
				if (strLength >= endIndex) {
					result = str.substring(beginIndex, endIndex);
				} else {
					result = str.substring(beginIndex);
				}
			}
		}

		return result;
	}

	/**
	 * 日期转字符串
	 * 
	 * @param aDate
	 *            日期
	 * @param datePattern
	 *            日期格式
	 * @return 结果
	 * 
	 */
	public static String convertDateToString(Date aDate, DatePattern datePattern) {
		String strDate = "";
		SimpleDateFormat dateMan = new SimpleDateFormat(
				datePattern.getPattern());

		if (aDate != null) {
			strDate = dateMan.format(aDate);
		}
		return strDate;
	}

	/**
	 * 
	 * @param src
	 * @param find
	 * @return
	 */
	public static int getOccur(String src, String find) {
		int o = 0;
		int index = -1;
		while ((index = src.indexOf(find, index)) > -1) {
			++index;
			++o;
		}
		return o;
	}

	/**
	 * 将字符串严格按照指定格式转为日期<br>
	 * 
	 * @param strDate
	 * @param datePattern
	 * @return Date型日期
	 *
	 */
	public static Date convertStringToDate(String strDate,
			DatePattern datePattern) {
		SimpleDateFormat dateMan = new SimpleDateFormat(
				datePattern.getPattern());
		Date rDate = null;
		dateMan.setLenient(false);

		try {
			if (strDate != null) {
				rDate = dateMan.parse(strDate);
			}
		} catch (ParseException e) {
			// 转换失败返回NULL
			rDate = null;
		}

		return rDate;
	}

	/**
	 *
	 * 字符型日期的格式转换
	 *
	 * @param stringDate
	 *            字符型日期
	 * @param orgPattern
	 *            原格式
	 * @param newPattern
	 *            转换后格式
	 * @return 格式转换后的字符型日期
	 */
	public static String convertStringDatePattern(String stringDate,
			DatePattern orgPattern, DatePattern newPattern) {
		String newStringDate = "";

		SimpleDateFormat orgDate = new SimpleDateFormat(orgPattern.getPattern());
		orgDate.setLenient(false);
		SimpleDateFormat newDate = new SimpleDateFormat(newPattern.getPattern());
		newDate.setLenient(false);

		if (stringDate != null) {
			try {
				newStringDate = newDate.format(orgDate.parse(stringDate));

			} catch (ParseException e) {
				// 转换失败返回空字符串
				newStringDate = "";
			}
		}

		return newStringDate;
	}

	/**
	 *
	 * 判断日期B是否早于日期A
	 * 
	 * @param dateA
	 *            日期A
	 * @param dateB
	 *            日期B
	 * @param pattern
	 *            日期格式
	 * @return true:日期B早于日期A
	 *
	 */
	public static boolean isBefore(Date dateA, Date dateB, DatePattern pattern) {
		boolean isBefore = false;
		SimpleDateFormat dateMan = new SimpleDateFormat(pattern.getPattern());
		dateMan.setLenient(false);

		try {
			if (dateA != null && dateB != null) {
				isBefore = dateMan.parse(dateMan.format(dateA)).before(
						dateMan.parse(dateMan.format(dateB)));
			}
		} catch (ParseException e) {
			// 変換失敗の場合、falseを戻る。
			isBefore = false;

		}

		return isBefore;
	}

	/**
	 * 
	 * @param anyMap
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> anyMap) {
		boolean result = true;
		if (anyMap != null && anyMap.size() > 0) {
			result = false;
		}
		return result;
	}

	/**
	 * 
	 * @param anyList
	 * @return
	 */
	public static boolean isEmpty(List<?> anyList) {
		boolean result = true;
		if (anyList != null && anyList.size() > 0) {
			result = false;
		}
		return result;
	}

	public static String getLocalIP() {
		String ip = null;
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
	

	/**
	 * 将文件路径字符串中的路径分隔符统一转换为当前系统的路径分隔符
	 * @param filePath 路径
	 * @return
	 */
	public static String unifyFileSeparator(String filePath){
		String result = filePath;
        if (isEmptyString(result)) {
            return result;
        }
        
        if (!CommonConstant.FILE_SEPARATOR.equals("\\") && filePath.lastIndexOf("\\")>0){
        	result = filePath.replaceAll("\\", CommonConstant.FILE_SEPARATOR);
        }
        if (!CommonConstant.FILE_SEPARATOR.equals("/") && filePath.lastIndexOf("/") > 0) {
        	result = filePath.replaceAll("/", CommonConstant.FILE_SEPARATOR+CommonConstant.FILE_SEPARATOR);
        }
        
        return result;
	}
	
	/**
	 * 将长字符串拼接为一个字符串
	 * @param strings
	 * @return
	 */
	public static String buildString(String ...strings){
		StringBuffer tmpStringBf = new StringBuffer("");
		if (strings == null){
			return null;
		}
		for (String tmpString : strings){
			tmpStringBf.append(tmpString);
		}
		return tmpStringBf.toString();
	}
	
	
	/**
	 * 求最大值
	 * @param list
	 * @return
	 */
	public static Double max(List<Object> list){
		Double value = null;
		if(list != null && list.size() >0){
			value = Double.valueOf(list.get(0).toString());
			for(Object obj : list){
				if(value < Double.valueOf(obj.toString())){
					value = Double.valueOf(obj.toString());
				}
			}
		}
		return value;
	}
	
	/**
	 * 求最小值
	 * @param list
	 * @return
	 */
	public static Double min(List<Object> list){
		Double value = null;
		if(list != null && list.size() >0){
			value = Double.valueOf(list.get(0).toString());
			for(Object obj : list){
				if(value > Double.valueOf(obj.toString())){
					value = Double.valueOf(obj.toString());
				}
			}
		}
		return value;
	}
	
	/**
	 * 求和
	 * @param list
	 * @return
	 */
	public static Double sum(List<Object> list){
		Double value = null;
		if(list != null && list.size() >0){
			value = 0.0;
			for(Object obj : list){
				value += Double.valueOf(obj.toString());
			}
		}
		return value;
	}
	
	/**
	 * 求平均值
	 * @param list
	 * @return
	 */
	public static Double avg(List<Object> list){
		Double value = sum(list);
		if(list != null && list.size() >0){
			value = value/list.size();
		}
		return value;
	}
	
	/**
	 * 求平方和
	 * @param list
	 * @return
	 */
	public static Double sum_power(List<Object> list){
		Double value = null;
		if(list != null && list.size() >0){
			value = 0.0;
			for(Object obj : list){
				value += Math.pow(Double.valueOf(obj.toString()), 2);
			}
		}
		return value;
	}
	
	/**
	 * 求count数
	 * @param list
	 * @return
	 */
	public static int count(List<Object> list){
		return list == null ? 0 : list.size();
	}
	
	public static Double rate(List<Object> list){
		Double value = null;
		if(list != null && list.size() >0){
			value = 0.0;
			for(Object obj : list){
				value += Double.valueOf(obj.toString());
			}
		}
		return count(list) == 0? 0.0 : value/count(list);
	}
	
	public static int noNullValue(Short value){
		return value = value == null ? 0: value;
	}
	
	public static int noNullValue(Integer value){
		return value = value == null ? 0: value;
	}
	
	public static long noNullValue(Long value){
		return value = value == null ? (long)0: value;
	}
	
	public static BigDecimal noNullValue(BigDecimal value){
		return value = value == null ? new BigDecimal(0): value;
	}
	
}
