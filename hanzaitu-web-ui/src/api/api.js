let src = "http://101.33.209.5:6679"; //当前IP
let wssrc = "http://101.33.209.5:6679"; //当前IP
export let baseUrl = src+"/api/"
export let wsUrl = "wss://"+wssrc+"/api/"
export let staticUrl = 'https://image.hanzaitu.com/static' //静态资源地址

export const HTTP_URI = {
  BASEURL: baseUrl,
  REGISTER: baseUrl + '/login/register',//注册
  registerMail: baseUrl + '/login/registerMail',//邮箱注册
  ImageVerifyCode: baseUrl + '/login/getImageVerifyCode',//图形验证码
  verifyImageVerifyCode: baseUrl + '/login/verifyImageVerifyCode',//图形验证码
  LOGIN: baseUrl + '/login/signin',//登录
  loginPhone: baseUrl + '/login/signinPhoneNumber',//手机号登录
  SIGNOUT: baseUrl + '/login/signout',//登出
  RESETPASSWORD: baseUrl + '/login/resetPassword',//重置密码
  resetPasswordEmail: baseUrl + '/login/resetPasswordEmail',//邮箱重置密码
  SENDREGISTERCODE: baseUrl + '/login/sendRegisterCode',//发送验证码
  sendResetRegisterCode: baseUrl + '/login/sendResetRegisterCode',//发送验证码-无需图形验证码
  sendMailRegisterCode: baseUrl + '/login/sendMailRegisterCode',//发送邮箱验证码
  editAvatar: baseUrl + '/customer/editAvatar',//编辑头像
  GETUSERINFO: baseUrl + '/customer/getUserInfo',//获取用户信息
  EDITUSERNAME: baseUrl + '/customer/editUserName',//用户名
  EDITPHONENUMBER: baseUrl + '/customer/editPhoneNumber',//修改电话
  INVITEUSER: baseUrl + '/customer/listInviteUser',//获取用户邀请明细信息
  listUserWalletRecord: baseUrl + '/financePay/listUserWalletRecord',//获取用户钱包明细记录

  homeMessage: baseUrl + '/notify/home',//首页通知
  homeBanner: baseUrl + '/config/banner',//首页轮播图

  modelTypeGpt3: baseUrl + '/chat/model/GPT_3',//查询模型GPT_3
  modelTypeGpt4: baseUrl + '/chat/model/GPT_4',//查询模型GPT_4
  CHATTINGRECORDSLIST: baseUrl + '/chat/history/title-list',//聊天记录列表
  CHATTINGRECORDSLIST4: baseUrl + '/chat/history/gpt4-title-list',//gpt4聊天记录列表
  CHATDETAIL: baseUrl + '/chat/history/content',//聊天记录
  DELETECHAT: baseUrl + '/chat/history/del-win',//删除窗口
  PROMPTCHAT: baseUrl + '/chat/prompt/list',//提示词列表
  ADDCOLLECT: baseUrl + '/chat/prompt/add-collect',//添加收藏
  COLLECTLIST: baseUrl + '/chat/prompt/collect-list',//收藏列表
  customAdd: baseUrl + '/chat/prompt/custom-add',//添加自定义提示词
  customList: baseUrl + '/chat/prompt/custom-list',//添加自定义提示词

  SUBMITDRAW: baseUrl + '/draw/trigger/submit',//提交请求图片
  DRAWINGLIST: baseUrl + '/draw/task/list',//提交请求图片
  IMGGENERATEDTEXT: baseUrl + '/draw/trigger/describe',//提交图生文
  DELDRAWLIST: baseUrl + '/draw/trigger/del',//提交图生文
  PROMPCALLWORD: baseUrl + '/draw/task/promp',//查询绘画提示词
  GETCONTACTUS: baseUrl + '/contactUs/getContactUs',//查询联系我们

  listPayConfig: baseUrl + '/financePay/listPayConfig',//支付套餐
  WXPAYORDER: baseUrl + '/financePay/createWxPayOrder',//微信支付
  ALIPAYORDER: baseUrl + '/financePay/createAliPayOrder',//支付宝支付
  YPayAppOrder: baseUrl + '/financePay/createYPayWebOrder',//源支付
  getCipherCode: baseUrl + '/financePay/createCipherCodePayOrder',//卡密充值
  getWXresultData:baseUrl + '/financePay/getWxPayResult',//微信支付结果

  notifySys: baseUrl + '/notify/sys',//弹窗通知
  notifyUser: baseUrl + '/notify/user',//消息列表
  configInfo: baseUrl + '/config/info',//配置提示

  drawSquareLabel: baseUrl + '/draw/square/label',//查询绘画广场标签
  drawSquareList: baseUrl + '/draw/square/list',//查询绘画广场列表
  drawTriggerRelease: baseUrl + '/draw/trigger/release',//发布绘画
  drawTriggerCollect: baseUrl + '/draw/trigger/collect',//收藏绘画
  drawSquareSquare: baseUrl + '/draw/square/square',//点赞收藏绘画
  drawSquareCollectSquare: baseUrl + '/draw/square/collect-square',//点赞收藏列表
  drawSquareCollectList: baseUrl + '/draw/square/collect',//创作收藏列表

}
export default { HTTP_URI, baseUrl }
