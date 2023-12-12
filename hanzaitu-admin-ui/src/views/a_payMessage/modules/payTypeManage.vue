<template>
    <div class="app-container">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="微信" prop="wx">
              <el-switch
                v-model="form.wx"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="0"
                :inactive-value="1"
                @change="changeSwitch">
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付宝" prop="zfb">
              <el-switch
                v-model="form.zfb"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="0"
                :inactive-value="1"
                @change="changeSwitch">
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="源支付" prop="yzf">
              <el-switch
                v-model="form.yzf"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="0"
                :inactive-value="1"
                @change="changeSwitch">
              </el-switch>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
</template>

<script>
  import { getPayType,savePayType } from "@/api/payMessage/payMessage";
  export default {
    name: "payTypeManage",
    data() {
      return {
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
        // 表单校验
        rules: {}
      }
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询用户列表 */
      getList() {
        this.loading = true;
        getPayType().then(response => {
            this.form = response.data;
            this.loading = false;
          }
        );
      },
      changeSwitch() {
        this.loading = true;
        savePayType(this.form).then(response => {
          if(response.code == 200){
            this.$modal.msgSuccess("修改成功");
            this.getList();
            this.loading = false;
          }
        });
      },
    }
  }
</script>

<style scoped>

</style>
