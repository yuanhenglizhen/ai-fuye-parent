<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="24" :xs="24">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
            >删除</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="编号" align="center" key="tokenId" prop="tokenId" width="50"/>
          <el-table-column label="机器人Token" align="center" key="botToken" prop="botToken" />
          <el-table-column label="用户token" align="center" key="userToken" prop="userToken" />
          <el-table-column label="频道ID" align="center" key="channelId" prop="channelId"/>
          <el-table-column label="服务器Id" align="center" key="guildId" prop="guildId"/>
          <el-table-column label="并发数" align="center" key="coreSize" prop="coreSize"/>
          <el-table-column label="代理ip" align="center" key="proxyHost" prop="proxyHost" width="240"/>
          <el-table-column label="代理端口" align="center" key="proxyPort" prop="proxyPort" width="240"/>

          <el-table-column
            label="操作"
            align="center"
            width="160"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="机器人Token" prop="botToken">
              <el-input v-model="form.botToken" placeholder="请输入机器人Token" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户token" prop="userToken">
              <el-input v-model="form.userToken" placeholder="请输入用户token" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="频道ID" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入频道ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="并发数" prop="coreSize">
              <el-input v-model="form.coreSize" placeholder="请输入并发数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="服务器Id" prop="guildId">
              <el-input v-model="form.guildId" placeholder="请输入服务器Id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="代理ip" prop="proxyHost">
              <el-input v-model="form.proxyHost" placeholder="请输入代理ip" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="代理端口" prop="proxyPort">
              <el-input v-model="form.proxyPort" placeholder="请输入代理端口"/>
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
  import { listFun,addFun,delFun } from "@/api/keyManage/drawKey.js";
  export default {
    name: "index",
    data() {
      return {
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 用户表格数据
        userList: null,
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 日期范围
        dateRange: [],
        // 表单参数
        form: {},
        // 查询参数
        queryParams: {},

        // 表单校验
        rules: {
          botToken: [
            { required: true, message: "机器人Token不能为空", trigger: "blur" },
          ],
          userToken: [
            { required: true, message: "用户token不能为空", trigger: "blur" },
          ],
          channelId: [
            { required: true, message: "频道ID不能为空", trigger: "blur" },
          ],
          guildId: [
            { required: true, message: "服务器Id不能为空", trigger: "blur" },
          ],
          coreSize: [
            { required: true, message: "并发数不能为空", trigger: "blur" },
          ],
        }
      };
    },
    watch: {

    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询用户列表 */
      getList() {
        this.loading = true;
        listFun(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
            this.userList = response.data;
            this.loading = false;
          }
        );
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          tokenId: undefined,
          botToken: undefined,
          userToken: undefined,
          channelId: undefined,
          guildId: undefined,
          coreSize: undefined,
          proxyHost: undefined,
          proxyPort: undefined,
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.dateRange = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.tokenId);
        this.single = selection.length != 1;
        this.multiple = !selection.length;
      },

      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加KEY";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        this.form = Object.assign({}, row);
        this.open = true;
        this.title = "修改KEY";
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.tokenId != undefined) {
              addFun(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addFun(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const id = row.tokenId || this.ids;
        this.$modal.confirm('是否确认删除用户？').then(function() {
          return delFun(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      },

    }
  };
</script>
