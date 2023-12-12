<template>
  <div class="app-container">
    <el-tabs v-model="activeName" >
      <el-tab-pane label="支付类型配置" name="0">
        <payTypeManage></payTypeManage>
      </el-tab-pane>
      <el-tab-pane label="支付宝配置" name="1">
        <aliPayMessagePC></aliPayMessagePC>
      </el-tab-pane>
      <el-tab-pane label="微信支付配置" name="2">
        <wxPayMessage></wxPayMessage>
      </el-tab-pane>
      <el-tab-pane label="源支付配置" name="3">
        <yPayMessage></yPayMessage>
      </el-tab-pane>
      <el-tab-pane label="支付积分配置" name="4">
        <pointsPayMessage></pointsPayMessage>
      </el-tab-pane>
      <el-tab-pane label="积分使用配置" name="5">
        <integralUse></integralUse>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
  import payTypeManage from "./modules/payTypeManage";
  import aliPayMessagePC from "./modules/aliPayMessagePC";
  import wxPayMessage from "./modules/wxPayMessage";
  import yPayMessage from "./modules/yPayMessage";
  import pointsPayMessage from "./modules/pointsPayMessage";
  import integralUse from "./modules/integralUse";
  export default {
    name: "index",
    components:{payTypeManage,aliPayMessagePC,wxPayMessage,yPayMessage,pointsPayMessage,integralUse},
    data() {
      return {
        activeName: '0'
      };
    },
    methods: {

    }
  }
</script>

<style scoped>

</style>
