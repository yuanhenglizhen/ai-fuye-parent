<template>
	<div class="base-page">
		<slot></slot>
		<gpt-modal v-model="dialogVisible" width="34.3rem">
			<template v-slot:header>
				{{ message.title }}
			</template>
			<template>
				<pre class="modal-content">{{message.content}}</pre>
			</template>
		</gpt-modal>
	</div>
</template>

<script>
	import gptModal from '@/components/h5/gpt/modal/index.vue';
	export default {
		components: {
			gptModal
		},
		data() {
			return {
				dialogVisible: false,
				message: {},
			};
		},
		created() {
			this.$eventBus.$on("notify", (data) => {
				if (!this.dialogVisible) {
					data.content = data.content
						?.replace(/\r\n/g, "<br />")
						.replace(/\n\n/g, "<br />").replace(/\n/g, "<br/ >");
					this.message = data;
					if (this.message.content) {
						this.dialogVisible = true;
						this.$eventBus.$emit("cancel-notify");
					}
				}
			});
		},
		methods: {},
	};
</script>

<style lang="scss" scoped>
	.base-page {
		width: 100%;
		height: 100%;
	}

	.modal-content {
		display: block;
		overflow: hidden scroll;
		margin-top: 1.5rem;
		height: 28.7rem;
		line-height: 1.5;
		color: #94a3b8;
		font-size: 1.4rem;
	}
</style>