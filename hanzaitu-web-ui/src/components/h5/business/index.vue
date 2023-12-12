<template>
  <gpt-modal :visible="visible" @visibleChange="handleVisibleChange">
    <template v-slot:header>{{ title }}</template>
    <template>
      <login
        v-if="status === 0"
        @close="handleVisibleChange"
        @redirect="handleRedirect"
      ></login>
      <register
        v-else-if="status === 1"
        @close="handleVisibleChange"
        @redirect="handleRedirect"
      ></register>
      <forgot
        v-else-if="status === 2"
        @close="handleVisibleChange"
        @redirect="handleRedirect"
      ></forgot>
    </template>
  </gpt-modal>
</template>

<script>
import Register from "./register";
import Login from "./login";
import Forgot from "./forgot";
import gptModal from '@/components/h5/gpt/modal/index.vue';

export default {
  components: {
    Register,
    Login,
    Forgot,
    gptModal
  },
  props: {
    visible: Boolean,
  },
  model: {
    event: "visibleChange",
    prop: "visible",
  },
  watch: {
    visible(val) {
      if(val) {
        this.status = 0;
      }
    }
  },
  data() {
    return {
      status: 0,
    };
  },
  computed: {
    title() {
      switch (this.status) {
        case 0:
          return "登录账号";
        case 1:
          return "注册账号";
        case 2:
          return "找回密码";
        default:
          return "";
      }
    },
  },
  methods: {
    handleRedirect(type) {
      switch (type) {
        case "login":
          this.status = 0;
          break;
        case "register":
          this.status = 1;
          break;
        case "forgot":
          this.status = 2;
          break;
      }
    },
    handleVisibleChange() {
      this.$emit("visibleChange", false);
    },
  },
};
</script>

<style>
</style>