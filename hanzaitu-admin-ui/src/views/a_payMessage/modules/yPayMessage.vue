<template>
  <div>
    <el-form ref="sysForm" :model="formPcY" label-width="140px">
      <el-row>
        <el-col :span="24">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="openEdit"
          >编辑</el-button>
        </el-col>
        <el-col :span="24">
          <el-form-item label="商户id" prop="merchantId">
            <div style="word-wrap:break-word;">{{formPcY.merchantId}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="回调地址" prop="notifyUrl">
            <div style="word-wrap:break-word;">{{formPcY.notifyUrl}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="请求地址" prop="postUrl">
            <div style="word-wrap:break-word;">{{formPcY.postUrl}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="页面跳转通知地址">
            <div>{{formPcY.returnUrl}}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="商户id" prop="merchantId">
              <el-input v-model="form.merchantId" placeholder="请输入商户id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="回调地址" prop="notifyUrl">
              <el-input v-model="form.notifyUrl" placeholder="请输入回调地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求地址" prop="postUrl">
              <el-input v-model="form.postUrl" placeholder="请输入请求地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="页面跳转通知地址" prop="returnUrl">
              <el-input v-model="form.returnUrl" placeholder="请输入页面跳转通知地址" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { listFunY,addFunY } from "@/api/payMessage/payMessage";
  export default {
    name: "yPayMessage",
    data() {
      return {
        formPcY:{},
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
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
        listFunY().then(response => {
            this.formPcY = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.reset();
        this.form = Object.assign({}, this.formPcY);
        this.open = true;
        this.title = "编辑源支付配置";
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            addFunY(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          }
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

<style scoped>

</style>
