package com.itapgo.qrpay;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * qrpay支付工具类（有报错请把@Data和@Slf4j注解移除）
 * 
 * @author LiaoZhengHan
 * @date 2018年12月12日
 */
@Data
@Slf4j
public class QrpayPayUtil {

	private String userId;
	private String version = "V001";
	private String key;
	private String reqUrl;

	private String sign;
	private String rspStr;

	private String msg;

	public static void main(String[] args) {
		String reqUrl = "接口地址";
		String orderId = StringUtil.getRandomOrder();
		QrpayPayUtil qrpayPayUtil = new QrpayPayUtil("您的商户编号", "您的接口密钥", reqUrl);
		boolean bl = qrpayPayUtil.doPay(orderId, 1L, "到账银行卡号", "到账人银行预留姓名");
		if (bl) {
			// TODO 1.成功代表请求受理成功，不代表代付成功，请发起查询
			qrpayPayUtil = new QrpayPayUtil("您的商户编号", "您的接口密钥", reqUrl);
			qrpayPayUtil.queryAgentPay(orderId);
		} else {
			// TODO 请求失败处理
		}
	}

	/**
	 * 代付
	 * 
	 * @author LiaoZhengHan
	 * @date 2018年12月12日
	 * @param orderId     订单编号
	 * @param amount      金额（单位：分）
	 * @param bankCard    到账银行卡号
	 * @param accountName 到账人银行预留姓名
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
			try {
				JSONObject json = JSONObject.parseObject(rspStr);
				if ("0000".equals(json.get("code"))) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("解析返回的json数据出错");
			}
		}

		return false;
	}

	/**
	 * 查询代付结果
	 * 
	 * @author LiaoZhengHan
	 * @date 2018年12月12日
	 * @param orderId 代付订单号
	 * @return null未知，true代付成功，false代付失败
	 */
	public Boolean queryAgentPay(String orderId) {
		Map<String, String> reqMap = new HashMap<>();
		reqMap.put("service_id", "agent.pay.query");
		reqMap.put("order_id", orderId);

		if (doPost(reqMap)) {
			System.out.println("【代付请求返回】" + rspStr);
			try {
				JSONObject json = JSONObject.parseObject(rspStr);
				if ("0000".equals(json.get("code"))) {
					if ("2".equals(json.get("status"))) {
						return true;
					} else if ("3".equals(json.get("status"))) {
						return false;
					}
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("解析返回的json数据出错");
				return null;
			}
		}
		return null;
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

	public QrpayPayUtil(String userId, String key, String reqUrl) {
		super();
		this.userId = userId;
		this.key = key;
		this.reqUrl = reqUrl;
	}

	/**
	 * 创建订单
	 * 
	 * @author LiaoZhengHan
	 * @date 2019年1月2日
	 * @param orderId   订单编号，请确保唯一，最好在订单前加上公司简称，如:DDG（点点够）
	 * @param amount    订单金额，单位：分
	 * @param tradeType 下单类型（1：快捷），暂时只有1
	 * @return
	 */
	public boolean createOrder(String orderId, long amount, int tradeType) {
		Map<String, String> reqMap = new HashMap<>();

		reqMap.put("service_id", "trade.order");

		reqMap.put("order_id", orderId);
		reqMap.put("amount", amount + "");
		reqMap.put("trade_type", tradeType + "");

		if (doPost(reqMap)) {
			log.info("【下单请求返回】" + rspStr);
			try {
				JSONObject json = JSONObject.parseObject(rspStr);
				if ("0000".equals(json.get("code"))) {
					return true;
				}
			} catch (Exception e) {
				log.error("解析返回的json数据出错，请求数据：{}，返回数据：{}", reqMap, rspStr);
			}
		}

		return false;
	}
}
