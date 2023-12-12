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
          <el-form-item label="AccessKey" prop="qnyAccessKey">
            <div style="word-wrap:break-word;">{{formPcZFB.qnyAccessKey}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="BucketName" prop="qnyBucketName">
            <div style="word-wrap:break-word;">{{formPcZFB.qnyBucketName}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="图片域名" prop="qnyImageDomain">
            <div style="word-wrap:break-word;">{{formPcZFB.qnyImageDomain}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="SecretKey">
            <div>{{formPcZFB.qnySecretKey}}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="AccessKey" prop="qnyAccessKey">
              <el-input v-model="form.qnyAccessKey" placeholder="请输入AccessKey" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="BucketName" prop="qnyBucketName">
              <el-input v-model="form.qnyBucketName" placeholder="请输入BucketName" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="图片域名" prop="qnyImageDomain">
              <el-input v-model="form.qnyImageDomain" placeholder="请输入图片域名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="SecretKey" prop="qnySecretKey">
              <el-input v-model="form.qnySecretKey" placeholder="请输入SecretKey" />
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
  import { listFunQNY,addFunQNY } from "@/api/restManage/restManage";
  export default {
    name: "qnyManage",
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
        listFunQNY().then(response => {
            this.formPcZFB = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.reset();
        this.form = Object.assign({}, this.formPcZFB);
        this.open = true;
        this.title = "编辑七牛云配置";
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            addFunQNY(this.form).then(response => {
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
          qnyAccessKey: undefined,
          qnyBucketName: undefined,
          qnyImageDomain: undefined,
          qnySecretKey: undefined,
        };
        this.resetForm("form");
      },
    }
  }
</script>

<style scoped>

</style>
