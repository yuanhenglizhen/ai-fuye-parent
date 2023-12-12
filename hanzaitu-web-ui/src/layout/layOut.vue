<template>
  <el-dialog title="提示"
    :visible.sync="dialogVisibles"
    width="320px"
    :before-close="handleClose">
    确认退出登录吗？
    <span slot="footer"
      class="dialog-footer">
      <el-button
        @click="handleClose">取消</el-button>
      <el-button
        type="primary"
        @click="layOut">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      dialogVisibles: false
    }
  },
  methods: {
    open() {
      this.dialogVisibles = true
    },
    handleClose() {
      this.dialogVisibles = false
    },
    layOut() {
      window.localStorage.clear()
      this.$message.success('正在退出登录')
      setTimeout(() => {
        this.$router.push('/login')
      }, 2000)
    }
  }
}
</script>

<style>
</style>