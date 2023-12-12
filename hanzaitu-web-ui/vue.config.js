const { defineConfig } = require('@vue/cli-service')
const UglifyPlugin = require('uglifyjs-webpack-plugin')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false,
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',    // 公共路径
  productionSourceMap: false,
  devServer: {
    port: 3000,
    https: false,
  },
  configureWebpack: (config) => {
    //  引入uglifyjs-webpack-plugin
    let UglifyPlugin = require('uglifyjs-webpack-plugin');

    if (process.env.NODE_ENV == 'production') {
      // 为生产环境修改配置
      config.mode = 'production'
      // 将每个依赖包打包成单独的js文件
      let optimization = {
        minimizer: [new UglifyPlugin({
          uglifyOptions: {
            warnings: false,
            compress: {
              drop_console: true,
              drop_debugger: false,
              pure_funcs: ['console.log']
            }
          }
        })]
      }
      Object.assign(config, {
        optimization
      })
    } else {
      // 为开发环境修改配置
      config.mode = 'development'
    }
  },


})
