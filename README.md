# qrpay-api-demo
点点够qrpay接口sdk

公式请求参数：
user_id：商户号
version：版本号，目前为V001
sign：签名
service_id：接口识别码

签名算法：
	所有参数按自然排序组装成：amount=13&order_id=2019010218332233949&service_id=trade.order&trade_type=1&user_id=22&version=V001
	然后拼接上：&key=您的密钥
	进行md5加密
	

请求方式：POST
请求数据类型：json

一、下单接口（trade.order）：
	请求参数：
		order_id：	String	订单编号，长度<=32，请确保唯一
		amount：		String	金额，单位：分
		trade_type： 	String	目前只开放：（1：H5快捷）

