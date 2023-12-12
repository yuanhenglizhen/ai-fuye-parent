<template>
  <div class="app-container">
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
          <el-form-item label="LOGO">
            <div style="word-wrap:break-word;">{{formPcZFB.logo}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="底部文字">
            <div style="word-wrap:break-word;">{{formPcZFB.text}}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="LOGO" prop="logo">
              <imageUpload :limit="1" :fileSize="5" v-model="form.logo" ></imageUpload>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="底部文字" prop="text">
              <el-input v-model="form.text" placeholder="请输入底部文字" />
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
  import { listFunHome,addFunHome } from "@/api/restManage/restManage";
  import imageUpload from "@/components/ImageUpload"
  export default {
    name: "homeManage",
    components:{
      imageUpload
    },
    data() {
      return {
        formPcZFB:{},
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
        // 表单校验
        rules: {
          logo: [
            { required: true, message: "LOGO不能为空", trigger: "blur" },
          ],
          text: [
            { required: true, message: "底部文字不能为空", trigger: "blur" },
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
        listFunHome().then(response => {
            this.formPcZFB = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.reset();
        this.form = Object.assign({}, this.formPcZFB);
        this.open = true;
        this.title = "编辑首页配置";
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            addFunHome(this.form).then(response => {
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
          logo: undefined,
          text: undefined,
        };
        this.resetForm("form");
      },
    }
  }
</script>

<style scoped>

</style>
