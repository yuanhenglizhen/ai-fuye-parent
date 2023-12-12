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
          <el-form-item label="密钥">
            <div style="word-wrap:break-word;">{{formPcZFB.key}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="售价">
            <div style="word-wrap:break-word;">{{formPcZFB.seller}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="链接地址">
            <div style="word-wrap:break-word;">{{formPcZFB.url}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="用户id">
            <div>{{formPcZFB.userid}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="tid">
            <div>{{formPcZFB.tid}}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="密钥" prop="key">
              <el-input v-model="form.key" placeholder="请输入密钥" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="售价" prop="seller">
              <el-input v-model="form.seller" placeholder="请输入售价" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="链接地址" prop="url">
              <el-input v-model="form.url" placeholder="请输入链接地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户id" prop="userid">
              <el-input v-model="form.userid" placeholder="请输入用户id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="tid" prop="tid">
              <el-input v-model="form.tid" placeholder="请输入tid" />
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
  import { listFun,addFun } from "@/api/expressage/expressage";
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
          key: [
            { required: true, message: "密钥不能为空", trigger: "blur" },
          ],
          seller: [
            { required: true, message: "售价不能为空", trigger: "blur" },
          ],
          url: [
            { required: true, message: "链接地址不能为空", trigger: "blur" },
          ],
          userid: [
            { required: true, message: "用户id不能为空", trigger: "blur" },
          ],
          tid: [
            { required: true, message: "tid不能为空", trigger: "blur" },
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
        listFun().then(response => {
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
            addFun(this.form).then(response => {
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
          key: undefined,
          seller: undefined,
          url: undefined,
          userid: undefined,
          tid: undefined,
        };
        this.resetForm("form");
      },
    }
  }
</script>

<style scoped>

</style>
