import request from "@/utils/h5/request";
import { getToken } from "@/utils/h5/auth";

const baseUrl = "/notify";
const isToken = true;


// 弹窗通知
export function notify() {  
  return request({
    url: `${baseUrl}/sys`,
    headers: {
      isToken,
    },
    method: "post"
  });
}

// 获取消息列表
export function getMsgList() {  
  return request({
    url: `${baseUrl}/user`,
    headers: {
      isToken,
    },
    method: "post"
  });
}