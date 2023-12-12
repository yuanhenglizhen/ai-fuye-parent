import request from "@/utils/h5/request";
import {
	getToken
} from "@/utils/h5/auth";

const baseUrl = "/financePay";
const isToken = true;



// 获取配置
export function listPayConfig(code) {
	return request({
		url: `${baseUrl}/listPayConfig`,
		headers: {
			isToken,
		},
		method: "get"
	});
}
// 获取钱包记录
export function listUserWalletRecord(startTime, endTime, pageNo = 1, pageSize = 10) {
	return request({
		url: `${baseUrl}/listUserWalletRecord`,
		headers: {
			isToken,
		},
		method: "post",
		data: {
			startTime,
			endTime,
			pageNo,
			pageSize
		}
	});
}


// 卡密充值
export function createCipherCodePayOrder(code) {
	return request({
		url: `${baseUrl}/createCipherCodePayOrder`,
		headers: {
			isToken,
		},
		method: "post",
		data: {
			"cipherCode": code,
			"status": ""
		}
	});
}
// 创建支付宝app支付订单
export function createAliPayAppOrder(amount) {
	return request({
		url: `${baseUrl}/createAliPayAppOrder`,
		headers: {
			isToken,
		},
		method: "post",
		data: {
			amount
		}
	});
}
// 创建支付宝支付订单
export function createAliPayOrder(amount) {
	return request({
		url: `${baseUrl}/createAliPayOrder`,
		headers: {
			isToken,
		},
		method: "post",
		data: {
			amount
		}
	});
}

// 创建源支付订单
export function createYPayAppOrder(amount) {
	return request({
		url: `${baseUrl}/createYPayAppOrder`,
		headers: {
			isToken,
		},
		method: "post",
		data: {
			// 支付方式：alipay:支付宝,qqpay:QQ钱包,wxpay:微信支付
			payType: 'alipay',
			amount
		}
	});
}