<template>
  <div>
    <div class="form">
      <div class="form-item">
        <!--<div class="form-label">登录你的账号</div>-->
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
          <div class="error-text font-24">{{ phoneNumberErrorText }}</div>
        </div>
      </div>
      <div class="form-item">
        <!--<div class="form-label">密码</div>-->
        <div class="form-control">
          <input
            v-model="password"
            password
            maxlength="25"
            placeholder-class="form-input-placeholder"
            class="form-input"
            :class="passwordErrorText ? 'error' : ''"
            placeholder="请输入你的密码"
          />
          <div class="error-text font-24">{{ passwordErrorText }}</div>
        </div>
      </div>
      <div class="form-item">
        <div class="form-control">
          <button class="form-button margin-top_30 font-wt-500" @click="handleLogin">
            登录
          </button>
        </div>
      </div>
    </div>
    <div class="other">
      <div class="left" @click="redirect('register')">注册账号 </div>
      <div class="right" @click="redirect('forgot')">忘记密码 </div>
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
import imageAuthCode from '@/components/graphValidateCode/imageAuthCode.vue'

export default {
  components:{imageAuthCode},
  data() {
    return {
      phoneNumber: "",
      password: "",
      phoneNumberErrorText: "",
      passwordErrorText: "",

      imgurl:"",
      miniimgurl:"",
      block_y:0,
      codeFlag:false,
      chenckMoveid:"",
    };
  },
  watch: {
    phoneNumber(val) {
      if (val?.length > 0) {
        this.phoneNumberErrorText = "";
      }
    },
    password(val) {
      if (val?.length > 0) {
        this.passwordErrorText = "";
      }
    },
  },
  mounted() {

  },
  methods: {
    redirect(type) {
      this.$emit("redirect", type);
    },
    handleVisibleChange() {
      this.$emit("close", false);
    },
    ...mapActions("user", ["Login"]),
    handleLogin() {
      let formError = false;
      if (!this.phoneNumber?.trim()) {
        this.phoneNumberErrorText = "请输入手机号码";
        formError = true;
      }
      if (!/^1[3456789]\d{9}$/.test(this.phoneNumber?.trim())) {
        this.phoneNumberErrorText = "手机号码格式不正确";
        formError = true;
      }
      if (!this.password) {
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
            await this.Login({
              username: this.phoneNumber?.trim(),
              password: this.password,
              chenckMoveid: this.chenckMoveid,
              removing: e
            });
            this.$eventBus.$emit("login-success");
            this.handleVisibleChange();
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
</style>