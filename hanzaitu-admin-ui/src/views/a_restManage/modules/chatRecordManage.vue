<template>
  <div class="app-container">
    <el-form ref="sysForm" :model="formPcZFB" label-width="200px">
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
          <el-form-item label="是否设置聊天记录保存时间">
            <div style="word-wrap:break-word;">{{formPcZFB.ifOn == 0?'已设置':'未设置'}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="保存时间（天）">
            <div style="word-wrap:break-word;">{{formPcZFB.chatExpiredDay}}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="200px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="是否设置聊天记录保存时间" prop="ifOn">
              <el-switch
                v-model="form.ifOn"
                active-color="#13ce66"
                inactive-color="#ff4949"
                active-value="0"
                inactive-value="1">
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.ifOn == 0">
            <el-form-item label="保存时间（天）" prop="chatExpiredDay">
              <el-input type="number" v-model="form.chatExpiredDay" placeholder="请输入保存时间" />
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
  import { listFun,addFun } from "@/api/restManage/restManage";
  import imageUpload from "@/components/ImageUpload"
  export default {
    name: "chatRecordManage",
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
          ifOn: [
            { required: true, message: "是否设置不能为空", trigger: "blur" },
          ],
          chatExpiredDay: [
            { required: true, message: "保存时间不能为空", trigger: "blur" },
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
        this.title = "编辑首页配置";
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
          ifOn: 0,
          chatExpiredDay: 1,
        };
        this.resetForm("form");
      },
    }
  }
</script>

<style scoped>

</style>
