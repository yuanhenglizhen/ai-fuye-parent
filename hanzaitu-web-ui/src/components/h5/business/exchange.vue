<template>
	<gpt-modal :visible="visible" @visibleChange="handleVisibleChange">
		<template v-slot:header>卡密充值</template>
		<template>
			<div class="form">
				<div class="form-item">
					<div class="form-label">卡密充值</div>
					<div class="form-control">
						<input v-model="cipherCode" placeholder-class="form-input-placeholder"
							class="form-input" :class="cipherCodeErrorText ? 'error' : ''" placeholder="请输入你的充值卡密" />
						<div class="error-text font-24">{{ cipherCodeErrorText }}</div>
					</div>
				</div>

				<div class="form-item">
					<div class="form-control">
						<button class="form-button margin-top_30 font-wt-500" @click="handleConfirm">
							充值
						</button>
					</div>
				</div>
			</div>
		</template>
	</gpt-modal>
</template>

<script>
	import {
		editUserName,
		createCipherCodePayOrder
	} from "@/api/h5";
	import gptModal from '@/components/h5/gpt/modal/index.vue';
	let timer;

	export default {
		components: {
			gptModal,
		},
		props: {
			visible: Boolean,
		},
		model: {
			event: "visibleChange",
			prop: "visible",
		},
		data() {
			return {
				cipherCode: "",
				cipherCodeErrorText: "",
			};
		},
		watch: {
			cipherCode(val) {
				if (val?.length > 0) {
					this.cipherCodeErrorText = "";
				}
			},
		},
		methods: {
			redirect(type) {
				this.$emit("redirect", type);
			},
			handleVisibleChange() {
				this.$emit("visibleChange", false);
			},
			handleConfirm() {
				if (!this.cipherCode?.trim()) {
					this.cipherCodeErrorText = "请输入卡密";
					return;
				}

				this.$ui.usingLoading(async () => {
					await createCipherCodePayOrder(this.cipherCode.trim());
					this.$ui.showToast("充值成功");
					this.handleVisibleChange();
				});
			},
		},
	};
</script>

<style lang="scss" scoped>
	@import "./common.scss";
</style>