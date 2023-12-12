<template>
	<div>
		<div class="title-fold" :class="isOpen ? 'active' : ''" @click="toggle">
			<div class="triangle"></div>
			<div class="font-28 font-wt-500">{{classify.categoryName}}</div>
			<div class="font-24">{{ classify.categoryContent.length }}</div>
		</div>
		<div class="switch-group" :style="{ height:height  }">
			<div @click.stop="sendArgs(content)" class="switch active" v-for="content in classify.categoryContent"
				:key="content.unique">
				<div class="left">{{ content.key }}</div>
				<div class="right">{{ content.value }}</div>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		props: {
			classify: Object
		},
		data() {
			return {
				isOpen: false,
				height: 0
			}
		},
		methods: {
			toggle() {
				this.isOpen = !this.isOpen
				if (this.isOpen) {
					this.height = (62 * this.classify.categoryContent.length)/20 + 'rem'
				} else {
					this.height = 0
				}
			},
			sendArgs(val) {
				this.$emit('sendArgs', val);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.title-fold {
		display: flex;
		align-items: center;
		margin: 0 1.6rem;
		color: #94a3b8;

		&.active {
			.triangle {
				transform: rotateZ(0);
			}
		}

		.triangle {
			width: 0;
			height: 0;
			border-top: 0.8rem solid #94a3b8;
			border-left: 0.45rem solid transparent;
			border-right: 0.45rem solid transparent;
			transform: rotateZ(-90deg);
			transition: transform 0.2s;
		}

		.font-28 {
			margin: 0 0.4rem;
		}

		.font-24 {
			// width: 28rem;
			padding: 0 0.25rem;
			height: 1.4rem;
			line-height: 1.4rem;
			background: #3b529d;
			border-radius: 0.2rem;
			color: var(--white-1);
			text-align: center;
		}
	}

	.switch-group {
		overflow: hidden;
		margin: 1.1rem 1.6rem;
		transition: height .5s;

		.switch {
			overflow: hidden;
			display: flex;
			margin: 0.5rem 0;
			font-size: 1.2rem;

			&.active {
				.left {
					background: #390b1b;
					color: #f98bb0;
				}

				.right {
					background: #2e3653;
					color: var(--white-1);
				}
			}

			>div {
				padding: 0 0.4rem;
				height: 2.6rem;
				line-height: 2.6rem;
			}

			.left {
				background: #302629;
				color: #a03e5f;
				border-radius: 0.2rem 0 0 0.2rem;
			}

			.right {
				background: #302629;
				color: #818181;
				border-radius: 0 0.2rem 0.2rem 0;
			}
		}
	}
</style>