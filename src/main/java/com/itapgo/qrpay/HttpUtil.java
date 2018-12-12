package com.itapgo.qrpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * http请求工具类
 * @作者 廖正瀚
 * @日期 2017年10月30日
 */
public class HttpUtil {
	
	public static final int JSON = 1;
	public static final int FORM = 2;
	public static final int STR = 3;
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	private static SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * http请求返回对象
	 * @作者 廖正瀚
	 * @日期 2017年10月30日
	 */
	public static class Response{
		private Header[] headers;
		private String body;
		public Header[] getHeaders() {
			return headers;
		}
		public void setHeaders(Header[] headers) {
			this.headers = headers;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		
	}
	
	
	
	/**
	 * 发送json数据
	 * @作者 廖正瀚
	 * @日期 2017年11月29日
	 * @param url
	 * @param reqMap
	 * @return
	 */
	public static String httpPostJson(String url, Map<String, String> reqMap){
		return httpPostJson(url, null, reqMap);
	}
	
	/**
	 * 发送json数据
	 * @作者 廖正瀚
	 * @日期 2017年12月1日
	 * @param url
	 * @param headerMap
	 * @param reqMap
	 * @return
	 */
	public static String httpPostJson(String url, Map<String, String> headerMap, Map<String, String> reqMap){
		Response response = httpPost(url, headerMap, reqMap, null, JSON);
		if (response == null) {
			return null;
		}
		return response.getBody();
	}
	
	
	
	/**
	 * 发送form表单数据
	 * @作者 廖正瀚
	 * @日期 2017年11月29日
	 * @param url
	 * @param reqMap
	 * @return
	 */
	public static String httpPostForm(String url, Map<String, String> reqMap){
		Map<String, String> headerMap = new HashMap<String, String>();
		Response response = httpPost(url, headerMap, reqMap, null, FORM);
		if (response == null) {
			return null;
		}
		return response.getBody();
	}
	
	/**
	 * 发送字符串数据
	 * @作者 廖正瀚
	 * @日期 2017年11月29日
	 * @param url
	 * @param data
	 * @return
	 */
	public static String httpPost(String url, String data){
		Map<String, String> headerMap = new HashMap<String, String>();
		Response response = httpPost(url, headerMap, null, data, STR);
		if (response == null) {
			return null;
		}
		return response.getBody();
	}
	
	/**
	 * post请求
	 * @作者 廖正瀚
	 * @日期 2017年11月29日
	 * @param url
	 * @param headerMap
	 * @param data
	 * @return
	 */
	public static Response httpPost(String url, Map<String, String> headerMap, String data){
		return httpPost(url, headerMap, null, data, STR);
	}
	
	
	/**
	 * 发起post请求
	 * @作者 廖正瀚
	 * @日期 2017年11月29日
	 * @param url
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String url, String data){
		
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Accept-Charset", "utf-8");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded");
		
		Response response = httpPost(url, headerMap, null, data, STR);
        if (response == null) {
        	return null;
        }
        return response.getBody();
    }
	
	
	/**
	 * post请求
	 * @作者 廖正瀚
	 * @日期 2017年11月29日
	 * @param url
	 * @param headerMap
	 * @param reqMap
	 * @param reqStr
	 * @param dataType
	 * @return
	 */
	public static Response httpPost(String url, Map<String, String> headerMap, Map<String, String> reqMap, String reqStr, int dataType){
		
		// （1）构造HttpClient的实例
		HttpClient httpClient = new HttpClient();
		
		// （2）创建POST方法的实例  
		PostMethod postMethod = new PostMethod(url);
		
		// （3）设置 request头 
		Iterator<Entry<String, String>> it = null;
		if (headerMap != null) {
			it = headerMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				if (StringUtils.isNotEmpty(entry.getKey()) && StringUtils.isNotEmpty(entry.getValue())) {
					postMethod.addRequestHeader(new Header(entry.getKey(),entry.getValue()));
				}
			}
		}
        
        // （4）执行postMethod 
        try {
			//放入请求数据
        	switch (dataType) {
			case FORM:
				it = reqMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					if (StringUtils.isNotEmpty(entry.getKey()) && StringUtils.isNotEmpty(entry.getValue())) {
						postMethod.addParameter(entry.getKey(), entry.getValue());
					}
				}
				postMethod.addRequestHeader(new Header("Accept-Charset","UTF-8"));
				postMethod.addRequestHeader(new Header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8"));
				break;
				
			case JSON:
				reqStr = JSONObject.toJSONString(reqMap);
			case STR:
			default:
				postMethod.addRequestHeader(new Header("Content-Type","application/json;charset=UTF-8"));
				RequestEntity requestEntity = new StringRequestEntity(reqStr, "text/xml", "UTF-8");
				postMethod.setRequestEntity(requestEntity);
				break;
			}
			postMethod.addRequestHeader(new Header("Content-Length", postMethod.getRequestEntity().getContentLength() + ""));
			
			postMethod.releaseConnection();
			
			//发起请求
			int statusCode = httpClient.executeMethod(postMethod);
			if (statusCode != HttpStatus.SC_OK) {  
				System.err.println("Method failed:" + postMethod.getStatusLine());  
			}
			
			// （5）读取response头信息  
			Header[] respHeaders = postMethod.getResponseHeaders();
			
			// （6）读取内容 
			InputStream inputStream = postMethod.getResponseBodyAsStream(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str= "";  
			while((str = br.readLine()) != null){  
				stringBuffer.append(str);  
			}  
			String responseBody = stringBuffer.toString();
			
			Response response = new Response();
			response.setBody(responseBody);
			response.setHeaders(respHeaders);
			return response;
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			logger.error(sim.format(new Date()) + "Please check your provided http address!", e);
		} catch (IOException e) {
			logger.error(sim.format(new Date()), e);
		} catch (Exception e) {
			logger.error(sim.format(new Date()), e);
		} finally {
			// 释放连接  
			postMethod.releaseConnection(); 
		}
		return null;
		
	}
	
}
