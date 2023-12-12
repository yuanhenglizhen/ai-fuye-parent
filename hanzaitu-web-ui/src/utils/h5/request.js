import store from "@/store";
import axios from 'axios'
import { HTTP_URI,baseUrl } from '@/api/api'
import {
	getToken,
	removeToken
} from "@/utils/h5/auth";
import errorCode from "@/utils/h5/errorCode";
import {
	toast,
	showConfirm,
	tansParams
} from "@/utils/h5/common";
import Vue from "vue";

let timeout = 10000;

const request = (config) => {
	// 是否需要设置 token
	const isToken = (config.headers || {}).isToken === false;
	config.header = config.header || {};
	config.header["Access-Control-Allow-Headers"] = "*";
	// config.header["Access-Control-Allow-Headers"] = 'accept, content-type, origin, Access-Token, Access-Token-Hzt';
	if (getToken() && !isToken) {
		config.header["Access-Token"] = getToken();
		config.header["Access-Token-Hzt"] = getToken();
		config.header['Hzt-Origin'] = window.location.origin;
		// config.header['Hzt-Origin'] = 'http://45.14.106.237:6679';
	}
	// get请求映射params参数
	if (config.params) {
		let url = config.url + "?" + tansParams(config.params);
		url = url.slice(0, -1);
		config.url = url;
	}
	return new Promise((resolve, reject) => {
		const instance = axios.create({
			method: config.method || "get",
			timeout: config.timeout || timeout,
			baseURL: config.baseUrl || baseUrl,
			data: config.data,
			headers: config.header,
			dataType: "json",
		})
		instance(config).then((res) => {
				const code = res.data.code || 200;
				const msg = res.data.msg || errorCode[code] || errorCode["default"];
				if (code === 401) {
					// showConfirm(
					//   "登录状态已过期，您可以继续留在该页面，或者重新登录?"
					// ).then((res) => {
					//   if (res.confirm) {
					//     store.dispatch("LogOut").then((res) => {
					//       uni.reLaunch({ url: "/pages/login" });
					//     });
					//   }
					// });
					Vue.prototype.$eventBus.$emit('need-logout');

					toast(msg);
					reject(401);
					return;
				} else if (code === 500) {
					toast(msg);
					reject("500");
					return;
				} else if (code !== 200) {
					toast(msg);
					reject(code);
					return;
				}
				resolve(res.data);
			})
			.catch((error) => {
				let {
					message
				} = error;
				if (message === "Network Error") {
					message = "后端接口连接异常";
				} else if (message.includes("timeout")) {
					message = "系统接口请求超时";
				} else if (message.includes("Request failed with status code")) {
					message = "系统接口" + message.substr(message.length - 3) + "异常";
				}
				toast(message);
				reject(error);
			});
	});
};

export default request;