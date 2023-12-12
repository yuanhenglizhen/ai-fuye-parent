import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询列表
export function listFun(query) {
  return request({
    url: '/system/config/listCipherCode',
    method: 'get',
    params: query
  })
}

// 新增
export function addFun(data) {
  return request({
    url: '/system/config/createCipherCode',
    method: 'post',
    data: data
  })
}

