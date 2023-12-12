<template>
    <div class="cont">
        <img src="../../../assets/img/other/xsczzn.png" alt="">
    </div>
</template>

<script>
    export default {
        name: "instructions",
        mounted() {
            this.$store.commit('OPEN_LOGIN', false);
            this.getInfo();
        },
        methods: {
            getInfo(){
                this.$GET('GETUSERINFO', {},0).then(res => {
                    if (res.code == 200) {
                        window.localStorage.setItem('infoData', JSON.stringify(res.data));
                    } else if(res.code == 401){
                        this.$store.commit('OPEN_LOGIN', true);
                        window.localStorage.clear();
                    }
                })
            },
        }
    }
</script>

<style lang="scss" scoped>
.cont{
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    flex: 1;
    overflow-y: auto;
    img{
        width: 80%;
    }
}
</style>