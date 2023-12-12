<template>
    <div class="container">
        <div class="login-container">
            <el-tabs v-model="activeName" @tab-click="handleTabsClick">
                <el-tab-pane label="点赞收藏" name="0">
                    <div>
                        <pictureWaterfallFlow :urlStr="'drawSquareCollectSquare'" :params="likeParams"></pictureWaterfallFlow>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="创作收藏" name="1">
                    <pictureWaterfallFlow :urlStr="'drawSquareCollectList'" :params="writeParams" :isCZSC="false"></pictureWaterfallFlow>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script>
    import pictureWaterfallFlow from '@/components/pictureWaterfallFlow/index'
    export default {
        name: "personalDynamics",
        components:{
            pictureWaterfallFlow
        },
        data(){
            return {
                activeName:"",
                typeTagList:25,
                tabCurrent:'0',
                likeParams:{
                    allowPaging:true,
                    pageSize:10,
                    pageNo:1,
                },
                writeParams:{
                    allowPaging:true,
                    pageSize:10,
                    pageNo:1,
                },
            }
        },
        mounted() {

        },
        methods:{
            handleTabsClick(e) {
                this.tabCurrent = e.index;
            },
        }
    }
</script>

<style lang="scss" scoped>
    .container{
        padding: 2.4rem;
    }

</style>