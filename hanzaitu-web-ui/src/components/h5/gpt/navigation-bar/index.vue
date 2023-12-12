<template>
	<div>
		<gpt-status-bar></gpt-status-bar>
		<div class="navigation-bar">
			<div class="top">
				<div class="left">
					<div @click="toggleLeftNavs" class="menu-fold bg-img"></div>
				</div>
				<div class="title">
					<slot></slot>
				</div>
				<div class="right">
					<div v-if="rightVisible">
						<div class="right-yes" v-if="isAuth">
							<div @click.stop="openWindow('/message')" class="icon-64 icon-message bg-img">
							</div>
							<div @click.stop="toggleRightNavs" class="logo_1 bg-img"></div>
						</div>
						<div v-else class="login-btn bg-linear-radient" @click="handleLogin">登录</div>
					</div>
				</div>
			</div>
		</div>
		<div class="navigation_placeholder"></div>
		<div class="left-navs-wrapper" :class="showLeftNavs ? 'visible' : 'hide'" @click="showLeftNavs = false">
			<div class="navs">
				<div class="nav-item" @click.stop="switchNav('/')">首页</div>
				<div class="nav-item" @click.stop="switchNav('/chat')">AI聊天</div>
				<div class="nav-item" @click.stop="switchNav('/draw')">AI绘画</div>
				<div class="nav-item" @click.stop="switchNav('/instruction')">操作指南</div>
				<div class="nav-item" @click.stop="switchNav('/contact-us')">联系我们</div>
			</div>
		</div>

		<div class="right-navs-wrapper" :class="showRightNavs ? 'visible' : 'hide'" @click="showRightNavs = false">
			<div class="navs">
				<div class="nav-item" @click.stop="openWindow('/mine')">个人信息</div>
				<!-- <div class="nav-item" @click.stop="openWindow('/pages/mine/index')">会员中心</div>
				<div class="nav-item" @click.stop="openWindow('/pages/mine/index')">邀请好友</div> -->
				<div class="nav-item" @click.stop="handleLogout">退出登录</div>
			</div>
		</div>

		<business v-model="registerDialogVisible"></business>
	</div>
</template>

<script>
	import {
		mapState,
		mapActions,
		mapMutations
	} from "vuex";
	import business from "@/components/h5/business/index.vue";
	import gptStatusBar from "@/components/h5/gpt/status-bar/index.vue";


	export default {
		components: {
			business,
			gptStatusBar
		},
		props: {
			rightVisible: {
				type: Boolean,
				default: true,
			},
		},
		data() {
			return {
				showLeftNavs: false,
				showRightNavs: false,
				registerDialogVisible: false,
			};
		},
		computed: {
			...mapState("user", ["token"]),
			isAuth() {
				return !!this.token;
			},
		},
		created() {
			// 登录 
			this.$eventBus.$on("need-login", () => {
				this.registerDialogVisible = true;
			});
			
			// 退出登录
			this.$eventBus.$on("need-logout", () => {
				this.logOutSync();
				this.$eventBus.$emit("logout-success");
				this.registerDialogVisible = true;
			});
		},
		mounted() {},
		methods: {
			...mapActions("user", ["LogOut"]),
			...mapMutations("user", ["logOutSync"]),
			toggleLeftNavs() {
				this.showLeftNavs = !this.showLeftNavs;
				this.showRightNavs = false;
			},
			toggleRightNavs() {
				this.showLeftNavs = false;
				this.showRightNavs = !this.showRightNavs;
			},
			openWindow(page) {
				this.showLeftNavs = false;
				this.showRightNavs = false;
				this.$router.push(page);
			},
			switchNav(page) {
				this.showLeftNavs = false;
				this.showRightNavs = false;
				this.$router.push(page);
			},
			handleLogin() {
				this.registerDialogVisible = true;
			},
			handleLogout() {
				this.showLeftNavs = false;
				this.showRightNavs = false;

				this.LogOut();
				this.$eventBus.$emit("logout-success");
			},
		},
	};
</script>

<style lang="scss" scoped>
	$n_height: 4.5rem;

	.navigation_placeholder {
		height: 0;
		height: $n_height;
		color: var(--white-1);
	}

	.navigation-bar {
		z-index: 989;
		position: fixed;
		top: 0;
		left:0;
		box-sizing: border-box;
		overflow: hidden;
		width: 100%;
		height: $n_height;
		background: var(--primary-color);
		border-bottom: 0.1rem solid #172042;

		.top {
			display: flex;
			justify-content: space-between;
			align-items: center;
			height: 100%;

			>div {
				display: flex;
				align-items: center;
				height: 100%;

				&:first-child {
					padding-left: 1.6rem;
				}

				&:last-child {
					padding-right: 1.6rem;
				}
			}

			.left,
			.right {
				width: 10rem;
			}

			.title {
				justify-content: center;
				font-size: 1.8rem;
				color: #fff;
			}

			.right {
				display: flex;
				justify-content: flex-end;
				.right-yes{
					display: flex;
					justify-content: flex-end;
				}
				>div:last-child {
					margin-left: 1.6rem;
				}
			}
		}
	}

	.left-navs-wrapper {
		z-index: 289;
		position: absolute;
		width: 100%;

		&.visible {
			height: calc(100vh - 4.5rem);

			.navs {
				height: 27rem;
			}
		}

		&.hide {
			height: 0;

			.navs {
				height: 0;
			}
		}

		.navs {
			overflow: hidden;
			position: absolute;
			z-index: 989;
			width: 100%;
			transition: height 0.2s ease-out;

			.nav-item {
				width: 100%;
				height: 5.4rem;
				line-height: 5.4rem;
				background: #080b16;
				font-size: 1.4rem;
				text-align: center;
				border-bottom: 0.1rem solid #172042;
			}
		}
	}

	.right-navs-wrapper {
		z-index: 289;
		position: absolute;
		width: 100%;

		&.visible {
			height: calc(100vh - 4.5rem);

			.navs {
				// height: 276rem;
				height: 7.2rem;

				&:before {
					content: "";
				}
			}
		}

		&.hide {
			height: 0;

			.navs {
				height: 0;

				&:before {
					content: none;
				}
			}
		}

		.navs {
			overflow: hidden;
			position: absolute;
			padding-top: 0.6rem;
			top: 0.6rem;
			right: 1.6rem;
			z-index: 989;
			width: 9.6rem;
			// background: red;
			border-radius: 0.4rem;
			transition: height 0.2s ease-out;

			&:before {
				position: absolute;
				top: 0;
				right: 1rem;
				border-bottom: 0.6rem solid #1a1e2d;
				border-left: 0.6rem solid transparent;
				border-right: 0.6rem solid transparent;
			}

			.nav-item {
				width: 100%;
				height: 3.375rem;
				background: #1a1e2d;
				font-size: 1.2rem;
				line-height: 3.375rem;
				text-align: center;
				border-top: 0.1rem solid #d9d9d9;

				&:first-child {
					border-width: 0;
					border-top-left-radius: 0.4rem;
					border-top-right-radius: 0.4rem;
				}
			}
		}
	}

	.menu-fold {
		width: 2.4rem;
		height: 2.4rem;
		background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAAPpJREFUaEPtmLENwjAURO86RqBgJHrYgIKSNRgARqCgZBcGYI+PKBEkin2OI1uX2nfffvflfJlo/GPj+4cPsHSCTsAJiATcQiJAWd5vAhGxAnAEsJExaQZPktchi8EEIuIE4KzVLqbek7z9cxs7wAHApdgWNKMtyUfSAT6LI2IHYK3VltUvkvfkFpLLVjLo9xaqBFAu4wRkhKKBExAByvJ+E/AsJDfHl4FnobI809w8C6Xxqry632u0Msjsck4gG10hoRMoBDLbZuxVwu9C2Vh/hZ6FCsJMtvIslIyspsD/gZq0kx93l97clPpuoSmU5lzjBOakO8W7+QTeR2s8MTv0rpkAAAAASUVORK5CYII=);
	}

	.login-btn {
		width: 6rem;
		height: 2.4rem;
		line-height: 2.4rem;
		text-align: center;
		border-radius: 1.2rem;
		font-size: 1.2rem;
		font-family: Inter-Regular, Inter;
	}

	.logo_1 {
		width: 3.2rem;
		height: 3.2rem;
		background-image: url(data:image/png;base64,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);
	}
</style>