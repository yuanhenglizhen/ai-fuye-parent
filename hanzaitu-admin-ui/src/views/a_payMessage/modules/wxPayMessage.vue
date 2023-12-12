<template>
  <div>
    <el-form ref="sysForm" :model="formWX" label-width="120px">
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
          <el-form-item label="api密钥" prop="apiV3key">
            <div style="word-wrap:break-word;">{{formWX.apiV3key}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="私钥证书地址" prop="privateKeyPath">
            <div>{{formWX.privateKeyPath}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="应用id" prop="appId">
            <div style="word-wrap:break-word;">{{formWX.appId}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="商家号" prop="merchantId">
            <div style="word-wrap:break-word;">{{formWX.merchantId}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="证书序列号" prop="merchantSerialNumber">
            <div style="word-wrap:break-word;">{{formWX.merchantSerialNumber}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="回调地址" prop="notifyUrl">
            <div style="word-wrap:break-word;">{{formWX.notifyUrl}}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="api密钥" prop="apiV3key">
              <el-input v-model="form.apiV3key" placeholder="请输入api密钥" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="私钥证书" prop="privateKeyPath">
              <el-input v-model="form.privateKeyPath" placeholder="请输入私钥证书" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="应用id" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入应用id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商家号" prop="merchantId">
              <el-input v-model="form.merchantId" placeholder="请输入商家号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证书序列号" prop="merchantSerialNumber">
              <el-input v-model="form.merchantSerialNumber" placeholder="请输入证书序列号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="回调地址" prop="notifyUrl">
              <el-input v-model="form.notifyUrl" placeholder="请输入回调地址" />
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
  import { listFunWX,addFunWX } from "@/api/payMessage/payMessage";
  export default {
    name: "wxPayMessage",
    data() {
      return {
        formWX:{},
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
        // 表单校验
        rules: {
          apiV3key: [
            { required: true, message: "api密钥不能为空", trigger: "blur" },
          ],
          privateKeyPath: [
            { required: true, message: "私钥证书不能为空", trigger: "blur" },
          ],
          appId: [
            { required: true, message: "应用id不能为空", trigger: "blur" },
          ],
          merchantId: [
            { required: true, message: "商家号不能为空", trigger: "blur" },
          ],
          merchantSerialNumber: [
            { required: true, message: "证书序列号不能为空", trigger: "blur" },
          ],
          notifyUrl: [
            { required: true, message: "回调地址不能为空", trigger: "blur" },
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
        listFunWX().then(response => {
            this.formWX = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.reset();
        this.form = Object.assign({}, this.formWX);
        this.open = true;
        this.title = "编辑微信配置";
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            addFunWX(this.form).then(response => {
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
          apiV3key: undefined,
          privateKeyPath: undefined,
          notifyUrl: undefined,
          appId: undefined,
          merchantId: undefined,
          merchantSerialNumber: undefined,
        };
        this.resetForm("form");
      },
    }
  }
</script>

<style scoped>

</style>
