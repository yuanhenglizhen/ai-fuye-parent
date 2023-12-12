import request from "@/utils/h5/request";

const baseUrl = "/login";

// 登录方法
export function login(username, password, chenckMoveid, removing) {
	const data = {
		username,
		password,
		chenckMoveid,
		removing,
	};
	return request({
		url: `${baseUrl}/signin`,
		headers: {
			isToken: false,
		},
		method: "post",
		data: data,
	});
}

/**
 * 注册
 * @param {Object} username
 * @param {Object} password
 * @param {Object} code
 * @param {Object} uuid
 */
export function register(phoneNumber, verificationCode, password, inviteCode,chenckMoveid,removing) {
	const data = {
		phoneNumber,
		password,
		inviteCode,
		verificationCode,
		chenckMoveid,
		removing
	};
	return request({
		url: `${baseUrl}/register`,
		headers: {
			isToken: false,
		},
		method: "post",
		data: data,
	});
}

// 退出方法
export function logout() {
	return request({
		url: `${baseUrl}/signout`,
		method: "post",
		headers: {
			isToken: true,
		},
	});
}

// 发送验证码
export function sendRegisterCode(phoneNumber) {
	return request({
		url: `${baseUrl}/sendResetRegisterCode`,
		method: "post",
		headers: {
			isToken: true,
		},
		data: {
			param: phoneNumber,
		},
	});
}

// 用户密码重置
export function resetPassword(phoneNumber, verificationCode, password,chenckMoveid,removing) {
	const data = {
		phoneNumber,
		password,
		verificationCode,
		chenckMoveid,
		removing,
	};
	return request({
		url: `${baseUrl}/resetPassword`,
		method: "post",
		headers: {
			isToken: true,
		},
		data,
	});
}

// 找回密码
export function retrievePassword(phoneNumber, verificationCode, password,chenckMoveid,removing) {
	const data = {
		phoneNumber,
		password,
		verificationCode,
		chenckMoveid,
		removing,
	};
	return request({
		url: `${baseUrl}/resetPassword`,
		method: "post",
		headers: {
			isToken: false,
		},
		data,
	});
}