<template>
    <div>
      <div class="form">
        <div class="form-item">
          <!--<div class="form-label">手机号码</div>-->
          <div class="form-control">
            <input
              v-model="phoneNumber"
              type="number"
              maxlength="11"
              placeholder-class="form-input-placeholder"
              class="form-input"
              :class="phoneNumberErrorText ? 'error' : ''"
              placeholder="请输入你的手机号码"
            />
            <div class="getcode font-28" @click="getCode">{{ getCodeText }}</div>
            <div class="error-text font-24">{{ phoneNumberErrorText }}</div>
          </div>
        </div>
        <div class="form-item">
          <!--<div class="form-label">验证码</div>-->
          <div class="form-control">
            <input
              v-model="verificationCode"
              type="number"
              maxlength="6"
              placeholder-class="form-input-placeholder"
              class="form-input"
              :class="verificationCodeErrorText ? 'error' : ''"
              placeholder="请输入你的验证码"
            />
            <div class="error-text font-24">{{
              verificationCodeErrorText
            }}</div>
          </div>
        </div>
        <div class="form-item">
          <!--<div class="form-label">新密码</div>-->
          <div class="form-control">
            <input
              v-model="password"
              password
              maxlength="25"
              placeholder-class="form-input-placeholder"
              class="form-input"
              :class="passwordErrorText ? 'error' : ''"
              placeholder="请输入你的新密码"
            />
            <div class="error-text font-24">{{ passwordErrorText }}</div>
          </div>
        </div>
        
        <div class="form-item">
          <div class="form-control">
            <button class="form-button margin-top_30 font-wt-500" @click="handleConfirm">
              重置密码
            </button>
          </div>
        </div>
      </div>
      <div class="other">
        <div class="left" @click="redirect('login')">返回登录</div>
      </div>

      <van-popup v-model="codeFlag" get-container="body" style="background: #000">
        <div style="width: 360px;overflow: hidden">
          <imageAuthCode :l="42"  ref="dialogopen"
                         :r="10"
                         :w="360"
                         :h="197"
                         :block_y="block_y"
                         :imgurl="imgurl"
                         :miniimgurl="miniimgurl"
                         @success="onSuccess"
          ></imageAuthCode>
        </div>
      </van-popup>
    </div>
  </template>
    
    <script>
  import { mapActions } from "vuex";
  import {sendRegisterCode, retrievePassword, register} from "@/api/h5";
  import imageAuthCode from '@/components/graphValidateCode/imageAuthCode.vue'
  let timer;
  
  export default {
    components:{imageAuthCode},
    data() {
      return {
        phoneNumber: "",
        verificationCode: "",
        password: "",
        getCodeText: "获取验证码",
        phoneNumberErrorText: "",
        passwordErrorText: "",
        verificationCodeErrorText: "",
        disabledGetCode: false,
        second: 60, //默认60秒

        imgurl:"",
        miniimgurl:"",
        block_y:0,
        codeFlag:false,
        chenckMoveid:"",
      };
    },
    watch: {
      phoneNumber(val) {
        if (val .length > 0) {
          this.phoneNumberErrorText = "";
        }
      },
      password(val) {
        if (val .length > 0) {
          this.passwordErrorText = "";
        }
      },
      verificationCode(val) {
        if (val .length > 0) {
          this.verificationCodeErrorText = "";
        }
      }
    },
    methods: {
      redirect(type) {
        this.$emit("redirect", type);
      },
      handleVisibleChange() {
        this.$emit("close", false);
      },
      getCode() {
        if (this.disabledGetCode) {
          return;
        }
  
        if (!this.phoneNumber?.trim()) {
          this.phoneNumberErrorText = "请输入手机号码";
          return;
        }
        if (!/^1[3456789]\d{9}$/.test(this.phoneNumber?.trim())) {
          this.phoneNumberErrorText = "手机号码格式不正确";
          return;
        }
        this.disabledGetCode = true;
  
        this.$ui.usingLoading(async () => {
          try {
            await sendRegisterCode(this.phoneNumber?.trim());
            this.startTimer();
            this.$ui.showToast("短信验证码已发送，请注意查收");
          } catch (error) {
            this.disabledGetCode = false;
          }
        });
      },
      startTimer() {
        this.getCodeText = this.second + "s";
  
        timer = setTimeout(() => {
          if (--this.second > 0) {
            this.startTimer();
          } else {
            this.disabledGetCode = false;
            this.second = 60;
            clearTimeout(timer);
          }
        }, 1000);
      },
      handleConfirm() {
        let formError = false;
        if (!this.phoneNumber?.trim()) {
          this.phoneNumberErrorText = "请输入手机号码";
          formError = true;
        }
        if (!/^1[3456789]\d{9}$/.test(this.phoneNumber?.trim())) {
          this.phoneNumberErrorText = "手机号码格式不正确";
          formError = true;
        }
        if (!this.verificationCode?.trim()) {
          this.verificationCodeErrorText = "请输入验证码";
          formError = true;
        }
        if (!this.password?.trim()) {
          this.passwordErrorText = "请输入密码";
          formError = true;
        }
        if (formError) {
          return;
        }
        this.codeFlag = true;
        this.imgurl = ''
        this.miniimgurl = ''
        this.getImageVerifyCode();
      },

      getImageVerifyCode: function() {
        var that = this
        this.$https('ImageVerifyCode', {}).then(res => {
          if (!res || res.code != '200') {

          } else {
            var imgobj = JSON.parse(res.data)
            that.block_y = imgobj.yHeight;
            that.imgurl =  'data:image/png;base64,' + imgobj.bigImage;
            that.miniimgurl = 'data:image/png;base64,' + imgobj.smallImage;
            that.chenckMoveid = imgobj.chenckMoveid;
            if (that.$refs.dialogopen) {
              that.$refs.dialogopen.reset(imgobj.yHeight)
            }
          }
        })
      },
      onRefresh() {
        this.imgurl = '';
        this.miniimgurl = '';
        this.getImageVerifyCode();
        this.$emit("refresh",this.chenckMoveid);
      },

      onSuccess(e){
        this.$https('verifyImageVerifyCode', {
          "chenckMoveid": this.chenckMoveid,
          "removing": e
        }).then(res => {
          if (res.code == '200') {
            this.$ui.usingLoading(async () => {
              await retrievePassword(
                      this.phoneNumber?.trim(),
                      this.verificationCode?.trim(),
                      this.password?.trim(),
                      this.chenckMoveid?.trim(),
                      e,
              );
              this.$ui.showToast('重置成功');
              this.redirect('login');
              // this.handleVisibleChange();
            });
          }else{
            this.$message({
              showClose: true,
              message: '验证失败！',
              type: 'error'
            })
            this.onRefresh();
            this.$emit("success",null);
          }
        })
      },

    },
  };
  </script>
    
    <style lang="scss" scoped>
  
  @import "./common.scss";
  
  .other {
    justify-content: center;
    .left {      
      margin-right: 0.4rem;
    }
  }
  </style>