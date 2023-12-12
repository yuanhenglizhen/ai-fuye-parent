import request from "@/utils/h5/request";
import { getToken } from "@/utils/h5/auth";

const baseUrl = "/draw";
const isToken = true;

// 提交Describe图生文任务
export function drawDescribe(imgUrl, notifyHook, state) {
  const data = {
    imgUrl,
    notifyHook,
    state,
  };
  return request({
    url: `${baseUrl}/trigger/describe`,
    headers: {
      isToken,
    },
    method: "post",
    data,
  });
}

// 提交Imagine或UV任务
export function submitPromptTask(prompt) {
  const data = {
    action: 'IMAGINE',
    prompt
  };
  return request({
    url: `${baseUrl}/trigger/submit`,
    headers: {
      isToken,
    },
    method: "post",
    data,
  });
}
// 提交UV任务
export function submitTransformTask(taskId, op) {
  return request({
    url: `${baseUrl}/trigger/submit`,
    headers: {
      isToken,
    },
    method: "post",
    data: {
      action: op.action,
      index: op.index,
      taskId: taskId,
    }
  });
}
// 删除任务
export function deleteTask(taskId) {
  return request({
    url: `${baseUrl}/task/del`,
    headers: {
      isToken,
    },
    method: "post",
    data: {
      taskId
    }
  });
}

// 查询提示词列表
export function getDrawPromptList() {
  return request({
    url: `${baseUrl}/task/promp`,
    headers: {
      isToken,
    },
    method: "get",
  });
}

// 获取任务列表
export function getTaskList(data) {
  return request({
    url: `${baseUrl}/task/list`,
    headers: {
      isToken,
    },
    method: "post",
    data: data
  });
}

// 指定ID获取任务
export function getTaskById(id) {
  return request({
    url: `${baseUrl}/task/${id}/fetch`,
    headers: {
      isToken,
    },
    method: "get",
  });
}
