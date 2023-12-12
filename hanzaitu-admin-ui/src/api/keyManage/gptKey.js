import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询列表
export function listFun(query) {
  return request({
    url: '/gpt-key/list',
    method: 'post',
    params: query
  })
}
// 查询模型
export function getModel(query) {
  return request({
    url: '/gpt-key/get-model',
    method: 'post',
    params: query
  })
}

// 新增
export function addFun(data) {
  return request({
    url: '/gpt-key/add',
    method: 'post',
    data: data
  })
}

// 修改
export function updateFun(data) {
  return request({
    url: '/gpt-key/update',
    method: 'post',
    data: data
  })
}

// 删除
export function delFun(ids) {
  return request({
    url: '/gpt-key/delete/' + ids,
    method: 'post'
  })
}
