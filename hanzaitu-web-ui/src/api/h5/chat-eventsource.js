import { HTTP_URI,baseUrl } from '../api'
import {fetchEventSource} from '@microsoft/fetch-event-source';
import {getToken} from "@/utils/h5/auth";
const {
	EventSourcePolyfill
} = require("@/utils/h5/event-source.js");

const prefixUrl = "/chat";

// 发送消息
export function sendMsg(token, winId, msgId, prompt, model, isGpt4) {
	const postData = {
		"winId":winId,
		"msgId":msgId,
		"model":model,
		"prompt":prompt
	}
	let controller = new AbortController()
	var source = fetchEventSource(
		isGpt4 ? `${baseUrl}${prefixUrl}/gpt4/sse/post` : `${baseUrl}${prefixUrl}/sse/post`,
		{
			headers: {
				'Content-Type': 'application/json',
				"Access-Token": token,
				"Access-Token-Hzt": token,
			},
			method: 'POST',
			body: JSON.stringify(postData),
			signal: controller.signal,
		}
	); //请求路径

	return source;
}