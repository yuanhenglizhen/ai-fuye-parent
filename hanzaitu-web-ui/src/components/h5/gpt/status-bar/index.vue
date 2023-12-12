<template>
	<div>
		<div class="status_bar_fixed" :class="transparent ? 'transparent' : ''"></div>
		<div class="status_bar_placeholder">
			<!-- 这里是状态栏 -->
		</div>
	</div>
</template>
<script>
	export default {
		props: {
			transparent: Boolean
		}
	}
</script>
<style lang="scss" scoped>
	.status_bar_fixed {
		z-index: 9999999;
		position: fixed;
		top: 0;
		left: 0;
		background: var(--primary-color);

		&.transparent {
			background: transparent;
		}
	}

	.status_bar_fixed,
	.status_bar_placeholder {
		width: 100%;
		height: 0;
		background: var(--primary-color);
	}
</style>