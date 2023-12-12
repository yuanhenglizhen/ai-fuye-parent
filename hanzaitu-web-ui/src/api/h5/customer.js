import request from "@/utils/h5/request";

const baseUrl = "/customer";
const isToken = true;

// 获取用户信息
export function getUserInfo() {
	return request({
		url: `${baseUrl}/getUserInfo`,
		headers: {
			isToken,
		},
		method: "get",
	});
}


// 编辑手机号
export function editPhoneNumber(phoneNumber, verificationCode) {
	const data = {
		phoneNumber,
		verificationCode,
	};
	return request({
		url: `${baseUrl}/editPhoneNumber`,
		headers: {
			isToken,
		},
		method: "post",
		data
	});
}

// 编辑用户名
export function editUserName(userName) {
	const data = {
		userName
	};
	return request({
		url: `${baseUrl}/editUserName`,
		headers: {
			isToken,
		},
		method: "post",
		data
	});
}

// 获取用户邀请明细信息
export function listInviteUser(startTime, endTime, pageNo = 1, pageSize = 10) {
	return request({
		url: `${baseUrl}/listInviteUser`,
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

// 修改头像
export function editAvatar(avatar) {
	const data = {
		avatar
	};
	return request({
		url: `${baseUrl}/editAvatar`,
		headers: {
			isToken,
		},
		method: "post",
		data
	});
}