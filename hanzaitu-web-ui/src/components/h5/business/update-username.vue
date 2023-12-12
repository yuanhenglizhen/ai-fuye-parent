<template>
	<gpt-modal :visible="visible" @visibleChange="handleVisibleChange">
		<template v-slot:header>修改用户名</template>
		<template>
			<div class="form">
				<div class="form-item">
					<div class="form-label">用户名</div>
					<div class="form-control">
						<input v-model="username" maxlength="11" placeholder-class="form-input-placeholder"
							class="form-input" :class="usernameErrorText ? 'error' : ''" placeholder="请输入你的用户名" />
						<div class="error-text font-24">{{ usernameErrorText }}</div>
					</div>
				</div>

				<div class="form-item">
					<div class="form-control">
						<button class="form-button margin-top_30 font-wt-500" @click="handleConfirm">
							完成
						</button>
					</div>
				</div>
			</div>
		</template>
	</gpt-modal>
</template>

<script>
	import gptModal from '@/components/h5/gpt/modal/index.vue';
	import {
		mapState,
		mapActions
	} from "vuex";
	import {
		editUserName
	} from "@/api/h5";

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
				username: "",
				usernameErrorText: "",
			};
		},
		computed: {
			...mapState("user", ["currentUser"]),
		},
		watch: {
			username(val) {
				if (val?.length > 0) {
					this.usernameErrorText = "";
				}
			},
		},
		created() {
			this.username = this.currentUser.userName;
		},
		methods: {
			...mapActions("user", ["GetUserInfo"]),
			redirect(type) {
				this.$emit("redirect", type);
			},
			handleVisibleChange() {
				this.$emit("visibleChange", false);
			},
			handleConfirm() {
				if (!this.username?.trim()) {
					this.usernameErrorText = "请输入用户名";
					return;
				}

				this.$ui.usingLoading(async () => {
					await editUserName(this.username.trim());
					await this.GetUserInfo();
					this.$ui.showToast("修改成功");
					this.handleVisibleChange();
				});
			},
		},
	};
</script>

<style lang="scss" scoped>
	@import "./common.scss";
</style>