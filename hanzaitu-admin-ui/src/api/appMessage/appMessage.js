import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询列表
export function listFun(data) {
  return request({
    url: '/system/notify/user-info',
    method: 'post',
    data: data
  })
}

// 新增
export function addFun(data) {
  return request({
    url: '/system/notify/user-info/save',
    method: 'post',
    data: data
  })
}
// 删除
export function delFun(id) {
  return request({
    url: '/system/notify/user-info/delete/'+id,
    method: 'post',
  })
}

// 查询列表
export function listSysFun(data) {
  return request({
    url: '/system/notify/sys-info',
    method: 'post',
    data: data
  })
}

// 新增
export function addSysFun(data) {
  return request({
    url: '/system/notify/sys-info/save',
    method: 'post',
    data: data
  })
}

