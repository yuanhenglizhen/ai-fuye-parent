export function getDaysInMonth(year, month) {
	return new Date(year, month, 0).getDate();
}

export function format(date, fmt) {
	let o = {
		"M+": date.getMonth() + 1, //月份   
		"d+": date.getDate(), //日
		"H+": date.getHours(), //小时（24小时制）
		"h+": date.getHours() % 12, //小时（12小时制）      
		"m+": date.getMinutes(), //分   
		"s+": date.getSeconds(), //秒   
		"q+": Math.floor((date.getMonth() + 3) / 3), //季度   
		"f": date.getMilliseconds() //毫秒   
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
	for (let k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

export function addMonth(date, month) {
	month = parseInt(month);
	if (isNaN(month)) {
		throw Error("month is invalid.");
	}
	let y = date.getFullYear();
	let m = date.getMonth() + month;
	let d = date.getDate();
	return new Date(y, m, d);
}

export function addDays(date, day) {
	day = parseInt(day);
	if (isNaN(day)) {
		throw Error("day is invalid.");
	}
	let y = date.getFullYear();
	let m = date.getMonth();
	let d = date.getDate() + day;
	return new Date(y, m, d);
}



export function toShortDateString(date) {
	return format(date, 'yyyy-MM-dd');
}

/*
 * description 计算时间差
 * param{Date} date1
 * param{Date} date2
 */
export function diff(date1, date2) {
	let date3 = date2.getTime() - date1.getTime();

	let days = Math.floor(date3 / (24 * 3600 * 1000));
	let leave1 = date3 % (24 * 3600 * 1000);
	let hours = Math.floor(leave1 / (3600 * 1000));

	let leave2 = leave1 % (3600 * 1000);
	let minutes = Math.floor(leave2 / (60 * 1000));

	let leave3 = leave2 % (60 * 1000);
	let seconds = Math.round(leave3 / 1000);

	return {
		totalDays: days,
		totalHours: hours,
		totalMinutes: minutes,
		totalSeconds: seconds
	};
};
