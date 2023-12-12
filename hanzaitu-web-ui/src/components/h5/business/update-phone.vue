<template>
  <gpt-modal :visible="visible" @visibleChange="handleVisibleChange">
    <template v-slot:header>更换手机</template>
    <template>
      <div class="tips">
        <div class="font-24">解锁后无法使用当前手机号码登录此账号</div>
        <div class="font-24">当前手机号码：{{ userMobile }}</div>
      </div>
      <div class="form">
        <div class="form-item">
          <div class="form-label">新手机号码</div>
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
            <div class="getcode font-28" @click="getCode">{{
              getCodeText
            }}</div>
            <div class="error-text font-24">{{ phoneNumberErrorText }}</div>
          </div>
        </div>
        <div class="form-item">
          <div class="form-label">验证码</div>
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
          <div class="form-control">
            <button
              class="form-button margin-top_30 font-wt-500"
              @click="handleConfirm"
            >
              完成
            </button>
          </div>
        </div>
      </div>
    </template>
  </gpt-modal>
</template>
    
    <script>
import { mapState, mapActions } from "vuex";
import { sendRegisterCode, editPhoneNumber } from "@/api/h5";
import gptModal from '@/components/h5/gpt/modal/index.vue';

let timer;

export default {
  components: {
    gptModal,
  },
  props: {
    visible: Boolean,
  },
  model: {
    event: "visibleChange",
    prop: "visible",
  },
  data() {
    return {
      phoneNumber: "",
      verificationCode: "",
      getCodeText: "获取验证码",
      phoneNumberErrorText: "",
      verificationCodeErrorText: "",
      disabledGetCode: false,
      second: 60, //默认60秒
    };
  },
  computed: {
    ...mapState("user", ["currentUser"]),
    userMobile() {
      return (
        this.currentUser.phoneNumber?.substr(0, 3) +
        "****" +
        this.currentUser.phoneNumber?.substr(7)
      );
    },
  },
  watch: {
    phoneNumber(val) {
      if (val?.length > 0) {
        this.phoneNumberErrorText = "";
      }
    },
    verificationCode(val) {
      if (val?.length > 0) {
        this.verificationCodeErrorText = "";
      }
    },
  },
  methods: {
    redirect(type) {
      this.$emit("redirect", type);
    },
    handleVisibleChange() {
      this.$emit("visibleChange", false);
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
      if (this.phoneNumber?.trim() == this.currentUser.phoneNumber) {
        this.phoneNumberErrorText = "新手机号码不能与当前手机号码相同";
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

      if (this.phoneNumber?.trim() == this.currentUser.phoneNumber) {
        this.phoneNumberErrorText = "新手机号码不能与当前手机号码相同";
        formError = true;
      }

      if (!this.verificationCode?.trim()) {
        this.verificationCodeErrorText = "请输入验证码";
        formError = true;
      }

      if (formError) {
        return;
      }

      this.$ui.usingLoading(async () => {
        await editPhoneNumber(
          this.phoneNumber?.trim(),
          this.verificationCode?.trim()
        );

        this.$ui.showToast("修改成功");
        this.handleVisibleChange();
      });
    },
  },
};
</script>
    
<style lang="scss" scoped>
@import "./common.scss";
</style>