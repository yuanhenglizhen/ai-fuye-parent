<template>
  <div class="app-container">
    <el-tabs v-model="activeName" >
      <el-tab-pane label="用户消息" name="1">
        <userMessage></userMessage>
      </el-tab-pane>
      <el-tab-pane label="系统消息" name="2">
        <sysMessage></sysMessage>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
  import userMessage from "./modules/userMessage";
  import sysMessage from "./modules/sysMessage";
  export default {
    name: "index",
    components:{userMessage,sysMessage},
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
