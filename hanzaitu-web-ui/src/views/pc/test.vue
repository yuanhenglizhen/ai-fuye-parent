<template>
    <div class="test-box">
        <div class="test-img"><img src="https://image.hanzaitu.com/static/aiChat/chatuxiaotu4@2x.png" alt=""></div>
        <div class="test-text">测试版本 暂不开放</div>
    </div>
</template>

<script>
    export default {
        name: "test"
    }
</script>

<style scoped lang="scss">
.test-box{
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    .test-img{
        width: 24rem;
        height: 24rem;
        img{
            width: 100%;
            height: 100%;
        }
    }
    .test-text{
        font-size: 2.8rem;
        margin: 3rem 5rem 15rem 0;
        background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
}
</style>