package com.itapgo.qrpay;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.Data;

/**
 * qrpay支付工具类（有报错请把@Data注解移除）
 * 
 * @author LiaoZhengHan
 * @date 2018年12月12日
 */
@Data
public class QrpayPayUtil {

	private String userId;
	private String version = "V001";
	private String key;
	private String reqUrl;

	private String sign;
	private String rspStr;
	
	private String msg;
	
	/**
	 * 
	 * @author LiaoZhengHan
	 * @date 2018年12月12日
	 * @param orderId
	 * @param amount
	 * @param bankCard
	 * @param accountName
	 * @return 
	 */
	public boolean doPay(String orderId, Long amount, String bankCard, String accountName) {
		
		Map<String, String> reqMap = new HashMap<>();
		
		reqMap.put("service_id", "agent.pay");
		
		reqMap.put("order_id", orderId);
		reqMap.put("amount", amount.toString());
		reqMap.put("bank_card", bankCard);
		reqMap.put("account_name", accountName);
		
		if (doPost(reqMap)) {
			System.out.println("【代付请求返回】" + rspStr);
		}
		
		return false;
	}

	/**
	 * 发起请求
	 * 
	 * @author LiaoZhengHan
	 * @date 2018年12月12日
	 * @param reqMap
	 * @return
	 */
	public boolean doPost(Map<String, String> reqMap) {

		// 公共参数
		reqMap.put("user_id", userId);
		reqMap.put("version", version);
		
		createSign(reqMap);
		reqMap.put("sign", sign);

		try {
			rspStr = HttpUtil.httpPostJson(reqUrl, reqMap);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 生成签名
	 * 
	 * @author LiaoZhengHan
	 * @date 2018年12月12日
	 * @param reqMap
	 */
	private void createSign(Map<String, String> reqMap) {
		Map<String, String> signMap = new TreeMap<>(reqMap);
		String signStr = StringUtil.getUrlParam(signMap);
		signStr += "&key=" + key;
		System.out.println("【签名字符串】" + signStr);
		sign = DigestUtils.md5Hex(signStr).toUpperCase();
	}

	public QrpayPayUtil(String userId, String key) {
		super();
		this.userId = userId;
		this.key = key;
	}
	
	
}
