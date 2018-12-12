package com.itapgo.qrpay;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * 字符串工具类
 * 
 * @author LiaoZhengHan
 * @date 2018年12月12日
 */
public class StringUtil {

	/**
	 * 返回字符串yyyyMMddHHmmss
	 * 
	 * @作者 入梦炼心
	 * @创建时间 2017年8月18日
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date) {
		return String.format("%tY%<tm%<td%<tH%<tM%<tS", date);
	}

	/**
	 * 获取随机订单号:时间+5位随机数
	 * 
	 * @作者 廖正瀚
	 * @创建时间 2017年5月9日
	 * @return "yyyyMMddHHmmss"+getRandomString(5)
	 */
	public static String getRandomOrder() {
		return getDateStr(new Date()) + getRandomString(5, 10);
	}

	/**
	 * 生成字母和数字的随机字符串
	 * 
	 * @作者 廖正瀚
	 * @创建时间 2017年5月4日
	 * @param length
	 * @param radix
	 * @return
	 */
	public static String getRandomString(int length, int radix) {
		if (radix > 36) {
			radix = 36;
		}
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		char[] charAora = { 'a', 'A' };

		for (int i = 0; i < length; i++) {
			int number = random.nextInt(radix);
			if (number >= 10) {
				int index = random.nextInt(2);
				sb.append((char) (charAora[index] + (number - 10)));
			} else {
				sb.append(number);
			}
		}

		return sb.toString();
	}

	/**
	 * 拼接url参数
	 * 
	 * @作者 廖正瀚
	 * @创建时间 2017年6月15日
	 * @param params
	 * @return
	 */
	public static String getUrlParam(Map<String, String> params) {
		return getUrlParam(params, true, "=", "&");
	}

	public static String getUrlParam(Map<String, String> params, boolean containKey, String keyContactValue,
			String contact) {

		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = entry.getKey();
			String v = entry.getValue();
			if (null != v && !"".equals(v.trim()) && !"null".equalsIgnoreCase(v.trim())) {
				if (containKey) {
					sb.append(k);
					sb.append(keyContactValue);
				}
				sb.append(v);
				sb.append(contact);
			}
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 拼接url参数
	 * 
	 * @作者 廖正瀚
	 * @创建时间 2017年6月15日
	 * @param params
	 * @return
	 */
	public static String getUrlParamNull(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = entry.getKey();
			String v = entry.getValue();
			sb.append(k + "=" + v + "&");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 参数拼接成字符串
	 * 
	 * @作者 入梦炼心
	 * @创建时间 2017年8月18日
	 * @param params
	 * @return
	 */
	public static String getVaulesStr(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			Object v = entry.getValue();
			if (null != v && !"".equals(v)) {
				sb.append(v);
			}
		}
		return sb.toString();
	}
}
