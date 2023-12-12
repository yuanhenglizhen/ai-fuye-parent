import request from "@/utils/h5/request";
import { getToken } from "@/utils/h5/auth";
const { EventSourcePolyfill } = require("@/utils/h5/event-source.js");
import {fetchEventSource} from '@microsoft/fetch-event-source';
import { HTTP_URI,baseUrl } from '../api'
const prefixUrl = "/chat";
const isToken = true;

// 发送消息
export function sendMsg(winId, msgId, prompt, model, isGpt4) {
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
        "Access-Token": getToken(),
        "Access-Token-Hzt": getToken(),
      },
      method: 'POST',
      body: JSON.stringify(postData),
      signal: controller.signal,
    },
  ); //请求路径

  return source;
}

// 获取聊天记录
export function getContent(winId) {
  return request({
    url: `${prefixUrl}/history/content`,
    headers: {
      isToken,
    },
    method: "post",
    data: {
      winId,
    },
  });
}

// 查询窗口列表
export function getTitleList(isGpt4) {
  return request({
    url: isGpt4 ?
     `${prefixUrl}/history/gpt4-title-list` : `${prefixUrl}/history/title-list`,
    headers: {
      isToken,
    },
    method: "post",
  });
}

// 查询提示词列表
export function getPromptList() {
  return request({
    url: `${prefixUrl}/prompt/list`,
    headers: {
      isToken,
    },
    method: "post",
  });
}

// 查询收藏列表
export function getCollectList() {
  return request({
    url: `${prefixUrl}/prompt/collect-list`,
    headers: {
      isToken,
    },
    method: "post",
  });
}

// 添加收藏
export function addCollect(data) {
  return request({
    url: `${prefixUrl}/prompt/add-collect`,
    headers: {
      isToken,
    },
    method: "post",
    data,
  });
}

// 添加收藏
export function delAllWindow(isGpt4) {
  return request({
    url: `${prefixUrl}/history/del-win`,
    headers: {
      isToken,
    },
    method: "post",
    data: {
      model: isGpt4 ? 'GPT_4' : 'GPT_3',
      operate: "clean"
    }
  });
}

// 添加收藏
export function delWindow(winIds, isGpt4) {
  return request({
    url: `${prefixUrl}/history/del-win`,
    headers: {
      isToken,
    },
    method: "post",
    data: {
      model: isGpt4 ? 'GPT_4' : 'GPT_3',
      operate: "choose",
      winId: winIds.join(',')
    }
  });
}
// 获取GPT模型
export function getGptModel() {
	return request({
	  url: `${prefixUrl}/model/GPT_3`,
	  headers: {
	    isToken,
	  },
	  method: "post"
	});
}

