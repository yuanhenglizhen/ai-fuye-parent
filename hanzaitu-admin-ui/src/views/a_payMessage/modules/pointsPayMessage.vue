<template>
  <div>
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
      <div class="point-item" v-for="(item,index) in formList" :key="index">
        <div><span>积分:</span>{{item.points}}</div>
        <div><span>金额:</span>{{item.amount}}</div>
      </div>
    </div>

    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <div>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="addItem"
        >添加</el-button>
        <span style="margin-left: 1rem">积分设置最多10个</span>
      </div>
      <div class="form-point-box">
        <div class="form-point-item" v-for="(item,index) in form" :key="index">
          <div><span>积分:</span><el-input v-model="item.points" placeholder="请输入积分" /></div>
          <div><span>金额:</span><el-input v-model="item.amount" placeholder="请输入金额" /></div>
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
  import { listFunPoints,addFunPoints } from "@/api/payMessage/payMessage";
  export default {
    name: "pointsPayMessage",
    data() {
      return {
        formList:[],
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
        // 表单校验
        rules: {
          points: [
            { required: true, message: "积分不能为空", trigger: "blur" },
          ],
          amount: [
            { required: true, message: "金额不能为空", trigger: "blur" },
          ],
        }
      }
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询用户列表 */
      getList() {
        this.loading = true;
        listFunPoints().then(response => {
            this.formList = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        [ ...this.form ] = this.formList;
        this.open = true;
        this.title = "编辑积分配置";
      },
      addItem(){
        if(this.form.length<10){
          this.form.push({points:"",amount:""});
        }
      },
      /** 提交按钮 */
      submitForm() {
        let flag = true;
        this.form.map((item)=>{
          if(item.points == "" || item.points == null || item.amount == "" || item.amount == null){
            flag = false
          }
        })
        if(flag){
          addFunPoints(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        }else{
          this.$modal.msgError("积分或金额不能为空！");
        }
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
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  flex-wrap: wrap;
  margin-top: 2rem;
  .point-item{
    width: 8rem;
    text-align: center;
    border: 1px solid #2b2f3a;
    padding: 1rem 0;
    margin-right: 1rem;
  }
}
  .form-point-box{
    width: 100%;
    display: flex;
    justify-content: flex-start;
    align-items: flex-start;
    flex-wrap: wrap;
    .form-point-item{
      width: 10.5rem;
      text-align: center;
      padding: 1rem 0;
      margin-right: 1rem;
    }
  }
</style>
