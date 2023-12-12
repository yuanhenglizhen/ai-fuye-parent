<template>
	<div class="modal">
		<!-- <div class="mask"></div> -->
		<div class="line"></div>
		<div class="model-container" :class="disabled?'disabled':''" @click="showList = !showList">
			<div class="text font-28 font-wt-500">{{current}}</div>
			<div class="icon-28 icon-down bg-img"></div>
		</div>
		<div class="list" :class="showList? 'show' : ''">
			<div class="item" @click="chooseModel(item)" v-for="item in models" :key="item.model">
				<div class="font-28 font-wt-500" :class="current == item.model ? 'font-linear-radient' : ''">
					{{ item.model }}
				</div>
				<div class="icon-28 icon-right bg-img" :class="current == item.model ? 'show' : ''"></div>
			</div>
		</div>
	</div>
</template>

<script>
	import {
		getGptModel
	} from '@/api/h5';

	import {
		getToken
	} from "@/utils/h5/auth";

	export default {
		props: {
			disabled: Boolean
		},
		data() {
			return {
				showList: false,
				current: '',
				models: []
			}
		},
		created() {
			this.$eventBus.$on("login-success", () => {
				this.getModels();
			});

			if (getToken()) {
				this.getModels();
			}
		},
		methods: {
			getModels() {
				this.$ui.usingLoading(async () => {
					const {
						data
					} = await getGptModel()
					this.models = data;
					this.current = data[0].model;

					this.$emit('gptModelChange', this.current);
				});

			},
			chooseModel(val) {
				this.current = val.model
				this.showList = false;
				this.$emit('gptModelChange', this.current);
			}
		}
	}
</script>

<style lang="scss">
	.modal {
		// position: fixed;
		// z-index: 99999;
		// top: 0;
		// left: 0;
		// width: 100%;
		// height: 100%;

	}

	.mask {
		width: 100%;
		height: 100%;
		background: var(--white-1);
		opacity: 0;
	}

	.line,
	.model-container,
	.list {
		position: absolute;
		z-index: 1;
		left: 50%;
		transform: translateX(-50%);
		display: flex;
		justify-content: space-between;
		align-items: center;
		background: var(--primary-color);
	}

	.model-container {
		top: calc(10.5rem - 0.1rem);
		padding: 0.8rem 1.6rem;
		width: 21.6rem;
		height: 3.6rem;
		border-radius: 0 0 0.8rem 0.8rem;
		border: 1px solid #2B3A73;

	}
	.disabled {
		pointer-events: none;
	}

	.line {
		top: calc(10.5rem - 0.1rem);
		width: 100%;
		height: 0.05rem;
		background: #2B3A73;
	}

	.text {
		color: var(--white-1);
	}

	.list {
		overflow: hidden;
		top: 14.6rem;
		flex-direction: column;
		border-radius: 0.8rem;
		transition: height .3s;
		height: 0;

		&.show {
			height: 18rem;
		}


		.item {
			position: relative;
			padding: 0 1.6rem;
			width: 21.6rem;
			height: 3.6rem;
			line-height: 3.6rem;
			background: #2E3653;

			&:hover {
				background: #4A588C;
			}
		}

		.icon-right {
			display: none;
			position: absolute;
			top: 1.1rem;
			right: 1.6rem;

			&.show {
				display: block;
			}
		}
	}
</style>