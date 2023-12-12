<template>
  <gpt-modal :visible="visible" @visibleChange="handleVisibleChange">
    <template v-slot:header>更换密码</template>
    <template>
      <div class="tips">
        <div class="font-24">更改密码后无法使用旧密码进行登录</div>
        <div class="font-24"> </div>
      </div>
      <div class="form">
        <div class="form-item">
          <div class="form-label">新密码</div>
          <div class="form-control">
            <input
              v-model="newPassowrd"
              password
              maxlength="11"
              placeholder-class="form-input-placeholder"
              class="form-input"
              :class="newPassowrdErrorText ? 'error' : ''"
              placeholder="请输入你的新密码"
            />
            <div class="error-text font-24">{{ newPassowrdErrorText }}</div>
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
            <div class="getcode font-28" @click="getCode">{{
              getCodeText
            }}</div>
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
import { sendRegisterCode, resetPassword } from "@/api/h5";
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
      newPassowrd: "",
      verificationCode: "",
      getCodeText: "获取验证码",
      newPassowrdErrorText: "",
      verificationCodeErrorText: "",
      disabledGetCode: false,
      second: 60, //默认60秒
    };
  },
  computed: {
    ...mapState("user", ["currentUser"]),
  },
  watch: {
    newPassowrd(val) {
      if (val?.length > 0) {
        this.newPassowrdErrorText = "";
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
      this.disabledGetCode = true;
      this.$ui.usingLoading(async () => {
        try {
          await sendRegisterCode(this.currentUser.phoneNumber);
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
      if (!this.newPassowrd?.trim()) {
        this.newPassowrdErrorText = "请输入新密码";
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
        await resetPassword(
          this.currentUser.phoneNumber,
          this.verificationCode?.trim(),
          this.newPassowrd?.trim()
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