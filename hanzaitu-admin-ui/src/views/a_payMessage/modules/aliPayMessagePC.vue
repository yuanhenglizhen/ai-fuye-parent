<template>
  <div>
    <el-form ref="sysForm" :model="formPcZFB" label-width="120px">
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
          <el-form-item label="公钥" prop="alipayPublicKey">
            <div style="word-wrap:break-word;">{{formPcZFB.alipayPublicKey}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="私钥" prop="privateKey">
            <div style="word-wrap:break-word;">{{formPcZFB.privateKey}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="回调地址" prop="notifyUrl">
            <div style="word-wrap:break-word;">{{formPcZFB.notifyUrl}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="应用id">
            <div>{{formPcZFB.appId}}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="公钥" prop="alipayPublicKey">
              <el-input v-model="form.alipayPublicKey" placeholder="请输入公钥" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="私钥" prop="privateKey">
              <el-input v-model="form.privateKey" placeholder="请输入私钥" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="回调地址" prop="notifyUrl">
              <el-input v-model="form.notifyUrl" placeholder="请输入回调地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="应用id" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入应用id" />
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
  import { listFunPcZFB,addFunZFBPC } from "@/api/payMessage/payMessage";
  export default {
    name: "sysMessage",
    data() {
      return {
        formPcZFB:{},
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
        // 表单校验
        rules: {
          alipayPublicKey: [
            { required: true, message: "公钥不能为空", trigger: "blur" },
          ],
          privateKey: [
            { required: true, message: "私钥不能为空", trigger: "blur" },
          ],
          notifyUrl: [
            { required: true, message: "回调地址不能为空", trigger: "blur" },
          ],
          appId: [
            { required: true, message: "应用id不能为空", trigger: "blur" },
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
        listFunPcZFB().then(response => {
            this.formPcZFB = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.reset();
        this.form = Object.assign({}, this.formPcZFB);
        this.open = true;
        this.title = "编辑支付宝配置";
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            addFunZFBPC(this.form).then(response => {
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
          alipayPublicKey: undefined,
          privateKey: undefined,
          notifyUrl: undefined,
          appId: undefined,
        };
        this.resetForm("form");
      },
    }
  }
</script>

<style scoped>

</style>
