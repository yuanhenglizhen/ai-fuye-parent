export function format(str) {

	if(arguments.length < 2) {
		return str;
	}

	for(let i = 0; i < arguments.length - 1; i++) {
		let reg = new RegExp("({)" + i + "(})", "g");
		str = str.replace(reg, arguments[i + 1]);
	}
	return str;
}



export function padLeft(str, len, charStr) {
	let s = str + '';
	return new Array(len - s.length + 1).join(charStr, '') + s;
}

export function padRight(str, len, charStr) {
	let s = str + '';
	return s + new Array(len - s.length + 1).join(charStr, '');
}