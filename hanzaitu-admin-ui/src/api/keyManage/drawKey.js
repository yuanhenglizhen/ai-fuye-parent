import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询列表
export function listFun(query) {
  return request({
    url: '/draw-key/list',
    method: 'post',
    params: query
  })
}

// 新增
export function addFun(data) {
  return request({
    url: '/draw-key/save',
    method: 'post',
    data: data
  })
}

// 删除
export function delFun(ids) {
  return request({
    url: '/draw-key/del/' + ids,
    method: 'post'
  })
}
