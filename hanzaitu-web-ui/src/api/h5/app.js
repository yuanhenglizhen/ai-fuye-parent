import request from "@/utils/h5/request";
import {
	getToken
} from "@/utils/h5/auth";


const baseUrl = "/app";
const isToken = true;

// 
function getAppProperty() {
	return new Promise((resolve, reject) => {
		// #ifdef APP-PLUS
		plus.runtime.getProperty(plus.runtime.appid, function(wgtinfo) {
			resolve(wgtinfo)
		});
		// #endif
		// #ifndef APP-PLUS
		resolve({
			"appid": "HBuilder",
			"version": "1.0",
			"name": "ChatGpt",
			"versionCode": "10",
			"description": "",
			"author": "",
			"email": "",
			"features": [
				"payment",
				"camera",
				"push",
				"uninview"
			]
		})
		// #endif
	});
}

/**
 * 检查更新
 */
export async function checkVersion() {
	const wgtinfo = await getAppProperty();
	let version = wgtinfo.version;

	request({
		url: `${baseUrl}/release/list`,
		headers: {
			isToken,
		},
		method: "post",
		data: {
			version,
			appType: 'APK'
		}
	}).then(res => {
		let data = res.data;
		if (data.length > 0) {
			data = data.sort((a, b) => {
				return b.version - a.version
			});
			uni.showModal({
				title: '提示',
				content: '检测到有新版本，是否现在下载？',
				confirmText: '是',
				cancelText: '否',
				success: function(res) {
					// #ifdef APP-PLUS
					if (res.confirm) {
						download(data[0].download);
					}
					// #endif
				}
			})
		}

	})
}




//下载安装包
function download(url) {
	plus.nativeUI.showWaiting('正在下载...');
	var callback = function(f, status) {
		// 下载完成	
		if (status == 200) {
			plus.nativeUI.showWaiting('下载完毕，开始安装...');
			plus.nativeUI.closeWaiting();

			var path = f.filename; //下载apk
			plus.runtime.install(path); // 自动安装apk文件
		} else {
			plus.nativeUI.toast("下载失败: " + status)
		}
	}

	var downloader = plus.downloader.createDownload(url, {}, callback);
	downloader.start();
}

//下载
function downloadWgt(url) {
	var callback = function(f, status) {
		// 下载完成	
		if (status == 200) {
			var path = f.filename; //下载apk
			// 自动安装文件
			plus.runtime.install(path, {}, function() {
				uni.showModal({
					title: '提示',
					content: '资源更新完成，是否现在重启应用？',
					confirmText: '是',
					cancelText: '否',
					success: function(res) {
						if (res.confirm) {
							plus.runtime.restart();
						}
					}
				});
			});
		} else {
			plus.nativeUI.toast("下载失败: " + status)
		}
	}

	var downloader = plus.downloader.createDownload(url, {}, callback);
	downloader.start();
}