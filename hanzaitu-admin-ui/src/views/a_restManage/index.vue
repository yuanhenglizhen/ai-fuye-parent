<template>
  <div class="app-container">
    <el-tabs v-model="activeName" >
      <el-tab-pane label="首页配置" name="1">
        <homeManage></homeManage>
      </el-tab-pane>
      <el-tab-pane label="聊天记录保存时间配置" name="2">
        <chatRecordManage></chatRecordManage>
      </el-tab-pane>
      <el-tab-pane label="七牛云配置" name="3">
        <qnyManage></qnyManage>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
  import homeManage from "./modules/homeManage";
  import chatRecordManage from "./modules/chatRecordManage";
  import qnyManage from "./modules/qnyManage";
  export default {
    name: "index",
    components:{homeManage,chatRecordManage,qnyManage},
    data() {
      return {
        activeName: '1'
      };
    },
    methods: {

    }
  }
</script>

<style scoped>

</style>
