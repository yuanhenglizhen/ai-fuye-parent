<template>
    <div class="container">
        <gpt-navigation-bar>
			<template>
				操作指南
			</template>
		</gpt-navigation-bar>
        <img class="bg" mode="widthFix" src="@/static/images/Frame 261@2x.png" />
    </div>
</template>

<script>
    import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
    export default {
        components: {gptNavigationBar}
    }
</script>


<style>
    .container {
        min-height: 100%;
		background: var(--primary-color);
		color: var(--white-1);
    }

    .bg {
			width: 100%;
    }
</style>