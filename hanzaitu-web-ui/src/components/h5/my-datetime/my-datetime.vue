<template>
	<div>
		<van-popup v-model="show" position="bottom" @close="close" :style="{ height: '40%' }" >
			<div class="popup-content">
				<div>
					<van-datetime-picker
						v-model="currentDate"
						type="date"
						title="选择年月日"
						:formatter="formatter"
						@confirm="ok"
						@cancel="close"
					/>
				</div>
			</div>
		</van-popup>
	</div>
</template>

<script>
	import {format} from '@/utils/tools'
	export default {
		name: 'MyDatetime',
		components: {

		},
		props: {
			popupShow: {
				type: Boolean,
				default: false
			},
		},
		data() {
			return {
				show:false,
				currentDate: new Date(2021, 0, 17),
			}
		},
		watch: {
			// 月份
			popupShow(val) {
				this.show = val;
			},
		},
		created() {

		},
		methods: {

			formatter(type, val) {
				if (type === 'year') {
					return `${val}年`;
				} else if (type === 'month') {
					return `${val}月`;
				} else if (type === 'day') {
					return `${val}日`;
				}
				return val;
			},
			// 确认
			ok(val) {
				this.$emit('ok', val)
				this.close()
			},

			close() {
				this.$emit('close', false)
			},

		}
	}
</script>

<style scoped>
	.popup-content {
		background-color: #FFFFFF;
		color: #000;
		border-radius: 0.4rem 0.4rem 0px 0px;
	}

	.text1 {
		padding: 0 1.4rem;
		font-size: 1.7rem;
		line-height: 4.5rem;
		color: #888;
		float: left;
	}

	.blue {
		float: right;
		color: #007aff;
	}

	.uni-page-head-title {
		display: inline-block;
		padding: 0 1rem;
		font-size: 1.3rem;
		height: 4.4rem;
		line-height: 4.4rem;
		color: #BEBEBE;
		box-sizing: border-box;
	}

	.headBox {
		display: flex;
		justify-content: space-between;
		/* border-bottom: 1px solid #e5e5e5; */
	}

	picker-view {
		width: 100%;
		height: 22rem;
		margin-top: 1rem;
	}

	.item {
		line-height: 5rem;
		height: 5rem;
		text-align: center;
		font-size: 1.5rem;
	}
</style>