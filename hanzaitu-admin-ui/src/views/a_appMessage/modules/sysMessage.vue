<template>
  <div>
    <el-form ref="sysForm" :model="sysForm" label-width="120px">
      <el-row>
        <el-col :span="24">
          <el-form-item label="标题" prop="title">
            <div>{{sysForm.title}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="内容" prop="content">
            <div>{{sysForm.content}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="通知时间">
            <div>{{sysForm.startTime}}~~{{sysForm.endTime}}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="openEdit"
          >编辑</el-button>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入标题" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容" prop="content">
              <el-input v-model="form.content" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="通知时间" prop="dateRange">
              <el-date-picker
                v-model="form.dateRange"
                style="width:100%"
                type="datetimerange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd HH:mm:ss"
                @change="messageTime"
              ></el-date-picker>
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
  import { listSysFun,addSysFun } from "@/api/appMessage/appMessage.js";
  export default {
    name: "sysMessage",
    data() {
      return {
        sysForm: {},
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
        // 表单校验
        rules: {
          title: [
            { required: true, message: "标题不能为空", trigger: "blur" },
          ],
          content: [
            { required: true, message: "内容不能为空", trigger: "blur" },
          ],
          dateRange: [
            { required: true, message: "通知时间不能为空", trigger: "blur" },
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
        listSysFun().then(response => {
            this.sysForm = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.reset();
        this.form = Object.assign({}, this.sysForm);
        this.open = true;
        this.title = "添加用户消息";
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            addSysFun(this.form).then(response => {
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
          id: undefined,
          title: undefined,
          content: undefined,
          dateRange: undefined,
          startTime: undefined,
          endTime: undefined,
        };
        this.resetForm("form");
      },
      messageTime(e){
        this.form.startTime = e[0];
        this.form.endTime = e[1];
      }
    }
  }
</script>

<style scoped>

</style>
