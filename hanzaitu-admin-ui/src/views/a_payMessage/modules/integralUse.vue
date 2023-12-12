<template>
  <div>
    <div style="margin-bottom: 1rem">
      <el-button
        type="primary"
        plain
        icon="el-icon-plus"
        size="mini"
        @click="openEdit"
      >编辑</el-button>
    </div>
    <div class="cont-box">
      <el-table :data="integralUseType" >
        <el-table-column label="扣费项目" align="center" key="label" prop="label"/>
        <el-table-column label="扣分数额" align="center" key="points" prop="points"/>
      </el-table>
    </div>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
        <table>
          <tr>
            <th>扣费项目</th>
            <th>扣分数额</th>
          </tr>
          <tr v-for="(item,index) in form">
            <td>{{item.label}}</td>
            <td>
              <el-input v-model="item.points" placeholder="请输入扣分数额" type="number"/>
            </td>
          </tr>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
  import { listIntegralUseType,listIntegralUse,addIntegralUse } from "@/api/payMessage/payMessage";
  export default {
    name: "integralUse",
    data() {
      return {
        formPcY:{},
        // 是否显示弹出层
        open: false,
        title:"",
        form: [],
        // 表单校验
        rules: {
          merchantId: [
            { required: true, message: "商户id不能为空", trigger: "blur" },
          ],
          notifyUrl: [
            { required: true, message: "回调地址不能为空", trigger: "blur" },
          ],
          postUrl: [
            { required: true, message: "请求地址不能为空", trigger: "blur" },
          ],
          returnUrl: [
            { required: true, message: "页面跳转通知地址不能为空", trigger: "blur" },
          ],
        },
        integralUseType:[],
      }
    },
    created() {
      this.getTypeList();
    },
    methods: {
      /** 查询用户列表 */
      getTypeList() {
        this.loading = true;
        this.integralUseType = [];
        listIntegralUseType().then(response => {
          let listType = [];
          response.data.map((item)=>{
            Object.keys(item).forEach(key=>{
              let obj = {
                "label":item[key],
                "expendType":key,
                "points":"",
              }
              listType.push(obj);
            })
          })
          this.getList(listType);
          this.loading = false;
        });
      },
      /** 查询用户列表 */
      getList(obj) {
        this.loading = true;
        listIntegralUse().then(response => {
          this.formPcY = response.data;
          this.loading = false;
          for(let i=0;i<obj.length;i++){
            for(let s=0;s<this.formPcY.length;s++){
              if(obj[i].expendType == this.formPcY[s].expendType){
                obj[i].points = this.formPcY[s].points;
              }
            }
          }
          this.integralUseType = obj;
          this.$forceUpdate();
        });
      },
      openEdit(){
        this.reset();
        [ ...this.form ] = this.integralUseType;
        this.open = true;
        this.title = "编辑源支付配置";
      },
      /** 提交按钮 */
      submitForm() {
        addIntegralUse(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.getTypeList();
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          merchantId: undefined,
          notifyUrl: undefined,
          postUrl: undefined,
          returnUrl: undefined,
        };
        this.resetForm("form");
      },
    }
  }
</script>

<style lang="scss" scoped>

</style>
