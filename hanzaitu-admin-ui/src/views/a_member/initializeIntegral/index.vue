<template>
  <div class="app-container">
    <div>
      <el-button
        type="primary"
        plain
        icon="el-icon-plus"
        size="mini"
        @click="openEdit"
      >编辑</el-button>
    </div>
    <div class="point-box">
      <div class="point-item">
        <div><span>新注册用户积分:</span>{{formList.initPoints}}</div>
        <div style="margin-top: 20px"><span>拉新人积分:</span>{{formList.invitePoints}}</div>
      </div>
    </div>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <div class="form-point-box">
        <div class="form-point-item">
          <span>新注册用户积分:</span>
          <el-input v-model="form.initPoints" placeholder="请输入新注册用户积分" />
        </div>
        <div class="form-point-item">
          <span>拉新人积分:</span>
          <el-input v-model="form.invitePoints" placeholder="请输入拉新人积分" />
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { listData,addFun } from "@/api/member/initializeIntegral";
  export default {
    name: "index",
    data() {
      return {
        formList:{
          initPoints:0,
          invitePoints:0,
        },
        // 是否显示弹出层
        open: false,
        title:"",
        form: {
          initPoints:0,
          invitePoints:0,
        },
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
        listData().then(response => {
            this.formList = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.form = Object.assign({}, this.formList);
        this.open = true;
        this.title = "编辑积分配置";
      },
      /** 提交按钮 */
      submitForm() {
        addFun(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.getList();
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
      },
    }
  }
</script>

<style scoped lang="scss">
  .point-box{
    .point-item{
      padding: 1rem 0;
    }
  }
  .form-point-box{
    width: 100%;
    .form-point-item{
      display: flex;
      justify-content: flex-start;
      align-items: flex-start;
      padding: 1rem 0;
      margin-right: 1rem;
      span{
        width: 140px;
      }
    }
  }
</style>
