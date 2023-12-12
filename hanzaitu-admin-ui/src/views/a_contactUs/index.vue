<template>
  <div class="app-container">
    <el-form ref="sysForm" :model="lockForm" label-width="120px">
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
          <el-form-item label="背景图" prop="title">
            <div>
              <div>
                背景文字：{{lockForm.mainImgMessage}}
              </div>
              <div>
                <el-image
                  style="width:100%; height: 100%"
                  :src="lockForm.mainImg"
                  :preview-src-list="[lockForm.mainImg]">
                </el-image>
              </div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="图1" prop="content">
            <div>
              <div>标题：{{lockForm.dy1ImgMessage}}</div>
              <div>
                <el-image
                  style="width:100%; height: 100%"
                  :src="lockForm.dy1Img"
                  :preview-src-list="[lockForm.dy1Img]">
                </el-image>
              </div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="图2" prop="content">
            <div>
              <div>标题：{{lockForm.dy12mgMessage}}</div>
              <div>
                <el-image
                  style="width:100%; height: 100%"
                  :src="lockForm.dy2Img"
                  :preview-src-list="[lockForm.dy2Img]">
                </el-image>
              </div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="图3" prop="content">
            <div>
              <div>标题：{{lockForm.joinImgMessage}}</div>
              <div>
                <el-image
                  style="width:100%; height: 100%"
                  :src="lockForm.joinImg"
                  :preview-src-list="[lockForm.joinImg]">
                </el-image>
              </div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="图4" prop="content">
            <div>
              <div>标题：{{lockForm.wxImgMessage}}</div>
              <div>
                <el-image
                  style="width:100%; height: 100%"
                  :src="lockForm.wxImg"
                  :preview-src-list="[lockForm.wxImg]">
                </el-image>
              </div>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog :title="title" :visible.sync="open" width="1100px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <div>
              <div style="font-size: 18px;text-align: center">背景图</div>
              <el-form-item label="背景文字" prop="dy1ImgMessage">
                <el-input v-model="form.mainImgMessage" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="背景图" prop="mainImg">
                <imageUpload :limit="1" :fileSize="5" v-model="form.mainImg" ></imageUpload>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <div>
              <div style="font-size: 18px;text-align: center">图1</div>
              <el-form-item label="标题" prop="dy1ImgMessage">
                <el-input v-model="form.dy1ImgMessage" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="图片" prop="dy1Img">
                <imageUpload :limit="1" :fileSize="5" v-model="form.dy1Img" ></imageUpload>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="6">
            <div>
              <div style="font-size: 18px;text-align: center">图2</div>
              <el-form-item label="标题" prop="dy2ImgMessage">
                <el-input v-model="form.dy2ImgMessage" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="图片" prop="dy2Img">
                <imageUpload :limit="1" :fileSize="5" v-model="form.dy2Img" ></imageUpload>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="6">
            <div>
              <div style="font-size: 18px;text-align: center">图3</div>
              <el-form-item label="标题" prop="joinImgMessage">
                <el-input v-model="form.joinImgMessage" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="图片" prop="joinImg">
                <imageUpload :limit="1" :fileSize="5" v-model="form.joinImg" ></imageUpload>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="6">
            <div>
              <div style="font-size: 18px;text-align: center">图4</div>
              <el-form-item label="标题" prop="wxImgMessage">
                <el-input v-model="form.wxImgMessage" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="图片" prop="wxImg">
                <imageUpload :limit="1" :fileSize="5" v-model="form.wxImg"></imageUpload>
              </el-form-item>
            </div>
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
  import { listFun,addFun } from "@/api/contactUs/contactUs";
  import imageUpload from "@/components/ImageUpload"
  export default {
    name: "index",
    components:{imageUpload},
    data() {
      return {
        lockForm: {},
        // 是否显示弹出层
        open: false,
        title:"",
        form: {},
        // 表单校验
        rules: {
          mainImg: [
            { required: true, message: "背景图不能为空", trigger: "blur" },
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
            this.lockForm = response.data;
            this.loading = false;
          }
        );
      },
      openEdit(){
        this.reset();
        this.form = Object.assign({}, this.lockForm);
        this.open = true;
        this.title = "修改联系我们";
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
          dy1Img: undefined,
          dy1ImgMessage:null,
          dy2Img:undefined,
          dy2ImgMessage: null,
          joinImg: undefined,
          joinImgMessage: null,
          mainImg:undefined,
          mainImgMessage: null,
          wxImg: undefined,
          wxImgMessage: null,
        };
        this.resetForm("form");
      },

    }
  }
</script>

<style scoped>

</style>
