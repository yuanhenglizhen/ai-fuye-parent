import request from "@/utils/h5/request";
const baseUrl = "/config";

// 弹窗通知
export function config() {
	return request({
		//baseUrl: `http://45.143.235.80:6679/config/info`,
		url: `${baseUrl}/info`,
		method: "post"
	});
}