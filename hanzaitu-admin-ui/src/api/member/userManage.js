import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询定时任务调度列表
export function listUserManage(query) {
  return request({
    url: '/system/user/listCustomerUser',
    method: 'get',
    params: query
  })
}

// 拉黑用户
export function blacklistUser(userId) {
  return request({
    url: '/system/user/defriendCustomerUser/',
    method: 'post',
    data: {id:userId}
  })
}
// 启用用户
export function enableUser(userId) {
  return request({
    url: '/system/user/enableCustomerUser',
    method: 'post',
    data: {id:userId}
  })
}

// 删除用户
export function delUser(userId) {
  return request({
    url: '/system/user/delCustomerUser/',
    method: 'post',
    data: {id:userId}
  })
}
