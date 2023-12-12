import request from './request'
import { HTTP_URI,baseUrl } from './api'
export const REQUEST = {
  POST: function (businessType, datas, isToken) {
    if (!HTTP_URI[businessType]) return
    return request({
      url: HTTP_URI[businessType],
      method: 'post',
      data: datas
    })
  },
  POSTPAGE: function (businessType, datas) {
    return request({
      url: baseUrl+businessType,
      method: 'post',
      data: datas
    })
  },
  PUT: function (businessType, datas, isToken) {
    if (!HTTP_URI[businessType]) return
    return request({
      url: HTTP_URI[businessType],
      method: 'put',
      data: datas
    })
  },
  GET: function (businessType, params,numFlag) {
    console.log(businessType);
    if(numFlag == 0){
      return request({
        url: HTTP_URI[businessType],
        method: 'get',
        params: params
      })
    }else{
      return request({
        url: businessType,
        method: 'get',
        params: params
      })
    }

  }
}
export default REQUEST
