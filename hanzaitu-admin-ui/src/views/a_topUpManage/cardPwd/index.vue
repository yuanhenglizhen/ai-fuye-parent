<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
          <el-form-item label="卡密" prop="cipherCode">
            <el-input
              v-model="queryParams.cipherCode"
              placeholder="请输入卡密"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 240px"
            >
              <el-option label="已使用" value="used"/>
              <el-option label="未使用" value="usable"/>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
            >生成卡密</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="loading" :data="kmList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="卡密" align="center" key="cipherCode" prop="cipherCode" />
          <el-table-column label="积分" align="center" key="points" prop="points" />
          <el-table-column label="金额" align="center" key="totalAmount" prop="totalAmount" />
          <el-table-column label="状态" align="center" key="statusName" prop="statusName" />
        </el-table>
        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNo"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="卡密金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入卡密金额" type="number"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="生成数量" prop="count">
              <el-input v-model="form.count" placeholder="请输入生成数量" type="number"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="积分" prop="points">
              <el-input v-model="form.points" placeholder="请输入积分"  />
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
  import { listFun,addFun,} from "@/api/topUpManage/cardPwd";
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
        // 表格数据
        kmList: null,
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 日期范围
        dateRange: [],
        // 表单参数
        form: {},
        total:0,
        // 查询参数
        queryParams: {
          pageNo: 1,
          pageSize: 10,
          cipherCode: undefined,
          status: undefined,
        },

        // 表单校验
        rules: {
          amount: [
            { required: true, message: "卡密金额不能为空", trigger: "blur" },
          ],
          count: [
            { required: true, message: "生成数量不能为空", trigger: "blur" },
          ],
          points: [
            { required: true, message: "积分不能为空", trigger: "blur" },
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
        listFun(this.queryParams).then(response => {
            this.kmList = response.data.list;
            this.total = response.data.total;
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
          id: undefined,
          amount: undefined,
          count: undefined,
          points: undefined,
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNo = 1;
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
        this.ids = selection.map(item => item.userId);
        this.single = selection.length != 1;
        this.multiple = !selection.length;
      },

      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加用户";
      },

      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            addFun(this.form).then(response => {
              this.$modal.msgSuccess("生成成功");
              this.open = false;
              this.getList();
            });
          }
        });
      },

    }
  };
</script>
