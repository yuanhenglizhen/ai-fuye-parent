<template>
  <div v-if="show" class="ui-uploader">
    <el-image class="image" :src="filePath" ></el-image>
    <div v-if="progressPercent < 100">
      <div class="progress" :style="{ height: progressHeight }"></div>
      <div class="progressText">{{ progressPercent }}%</div>
    </div>
    <div class="icon-36 icon-prohibit bg-img" @click.stop="onCancel"></div>
  </div>
</template>
<script>
import { getToken } from "@/utils/h5/auth";
import { HTTP_URI,baseUrl } from '@/api/api'

const M10 = 10 * 1024 * 1024;

export default {
  props: {
    ukey: [Number, String],
    oldFileName: String,
    filePath: String,
    size: Number,
  },
  data() {
    return {
      uploadTask: null,
      progressPercent: 0,
      isCompleted: false,
      show: true,
    };
  },
  computed: {
    progressHeight() {
      return (96 - (this.progressPercent / 100) * 96) + "rem";
    },
  },
  mounted() {
    this.upload();
  },
  methods: {
    upload() {
      const self = this;

      if (this.size > M10) {
        this.show = false;
        this.$ui.showToast("超过10M无法上传");
        return;
      }

      this.uploadTask = uni.uploadFile({
        url: `${baseUrl}/draw/trigger/upload-img`,
        filePath: self.filePath,
        name: "file",
        header: {
          "Access-Token": getToken(),
          "Access-Token-Hzt": getToken(),
        },
        success(res) {
          self.isCompleted = true;
          self.$emit("completed", res.data);
        },
      });

      this.uploadTask.onProgressUpdate(function (res) {
        self.progressPercent = res.progress;
      });
    },
    onCancel() {
      
      if (!this.isCompleted) {       
        this.uploadTask && this.uploadTask.abort();       
      }

      this.$emit("cancel");
        this.show = false;
    },
  },
};
</script>
<style lang="scss" scoped>
.ui-uploader {
  position: relative;

  .image,
  .progress {
    width: 4.8rem;
    height: 4.8rem;
    border-radius: 0.4rem;
  }

  .icon-prohibit {
    position: absolute;
    top: -0.4px;
    left: 2px;
  }

  .progress {
    position: absolute;
    left: 0;
    top: 0;
    background: #fff;
    opacity: 0.7;
  }

  .progressText {
    position: absolute;
    top: 15px;
    color: red;
    font-size: 12px;
    left: 5px;
    color: grey;
  }
}
</style>
