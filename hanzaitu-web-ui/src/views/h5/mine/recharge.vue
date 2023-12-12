<template>
	<div class="recharge-container">
		<gpt-navigation-bar :rightVisible="false">
			<template>
				充值
			</template>
		</gpt-navigation-bar>

		<div class="title">选择充值套餐</div>

		<div class="price-group">
			<div class="price-label" v-for="item in amountItems" :class="amount == item.amount ?'active':''"
				  :key="item.amount" @click="chooseAmount(item.amount)">
				<div class="icon-40 icon-diamond bg-img"></div>
				<div class="number">
					<div class="num font-linear-radient">{{item.points}}</div>
					<div class="price">￥{{item.amount}}</div>
				</div>
			</div>
		</div>

		<div class="title pay">选择支付方式</div>
		<div class="btn-group">
			<!-- #ifdef APP-PLUS || H5 -->
			<div v-if="payType.zfb == 0" class="btn alipay" @click="payTypeSel = 'alipay'">
				<div class="radio" :class="payTypeSel == 'alipay' ?'active':''"></div>
				<div class="icon-40 icon-alipay bg-img"></div>
				<div class="text" :class="payTypeSel == 'alipay' ?'active':''">支付宝支付</div>
			</div>
			<!-- <div class="btn wxpay" @click="payType = 'wxpay'">
				<div class="radio" :class="payType == 'wxpay' ?'active':''"></div>
				<div class="icon-40 icon-wxpay bg-img"></div>
				<div class="text">微信支付</div>
			</div> -->

			<!-- #endif -->
			<div v-if="payType.yzf == 0" class="btn wxpay" @click="payTypeSel = 'yuanpay'">
				<div class="radio" :class="payTypeSel == 'yuanpay' ?'active':''"></div>
				<div class="icon-40 icon-yuanpay bg-img"></div>
				<div class="text" :class="payTypeSel == 'yuanpay' ?'active':''">源支付</div>
			</div>
		</div>

		<div class="confirm bg-linear-radient" @click="createOrder">确认支付</div>
	</div>

</template>

<script>
	import {
		createAliPayAppOrder,
		createAliPayOrder,
		createYPayAppOrder,
		listPayConfig,
		config,
	} from '@/api/h5'
	import {
		mapState,
		mapActions
	} from "vuex";
	import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
	export default {
		components: {gptNavigationBar},
		data() {
			return {
				amount: 1,
				amountItems: [],
				payTypeSel:'alipay',
				payType: {
					wx: 0,
					yzf: 0,
					zfb: 0
				}
			}
		},
		created() {
			this.$ui.usingLoading(async () => {
				const {
					data
				} = await listPayConfig()
				this.amountItems = data
				const res = await config()
				this.payType = res.data.payType
			})
		},
		methods: {
			//...mapActions("user", ["GetUserInfo"]),
			chooseAmount(val) {
				this.amount = val;
			},
			async createOrder() {
				if (!this.payType) {
					this.$ui.showToast('请选择支付方式')
					return;
				}
				if (this.payType === 'alipay') {
					this.$ui.showLoading();
					const {
						msg
					} =
					// #ifdef H5
					await createAliPayOrder(this.amount);
					this.$ui.hideLoading();

					document.body.innerHTML = msg;
					document.forms[0].submit();
					// #endif
				} else {
					this.$ui.showLoading();
					const {
						msg
					} = await createYPayAppOrder(this.amount);
					this.$ui.hideLoading();

					try {
						const json = JSON.parse(msg);
						if (json.qrcode && json.qrcode.indexOf('alipay') >= 0) {

							// #ifdef H5
							let a = document.createElement('a');
							a.setAttribute('href', 'https://render.alipay.com/p/s/i?scheme=' + (json.qrcode))
							a.setAttribute('target', '_blank');
							a.click()
							// window.open(decodeURIComponent(json.qrcode))
							// #endif

							return;
						}
						throw new Error(json.msg);
					} catch (ex) {
						this.$ui.showToast(ex.message);
					}

				}
			},

		}
	}
</script>

<style lang="scss" scoped>
	.recharge-container {
		overflow: hidden;
		width: 100%;
		min-height: 100%;
		color: var(--white-1);
		background: #080B16;
	}

	.title {
		margin: 1.6rem 1.6rem 0;
		height: 2.2rem;
		line-height: 2.2rem;
		font-size: 1.6rem;
		font-weight: 500;

		&.pay {
			margin-top: 1.6rem;
		}
	}

	.price-group {
		display: flex;
		flex: 1;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 1.6rem 1.6rem 0;

		.price-label {
			position: relative;
			display: flex;
			justify-content: center;
			padding: 0.8rem 1.2rem;
			margin-bottom: 1.6rem;
			width: 157px;
			height: 86px;
			background: #1A1E2D;
			border-radius: 0.4rem;
			font-weight: 500;

			&:before {
				content: "";
				position: absolute;
				top: 1px;
				left: 1px;
				width: 155px;
				height: 84px;
				background: #1A1E2D;
				border-radius: 0.4rem;
			}


			&.active {
				background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
			}

			.icon-diamond {
				z-index: 1;
				margin-top: 1.2rem;
			}

			.number {
				z-index: 1;
				margin-left: 0.5rem;
			}

			.num {
				height: 3.8rem;
				line-height: 3.8rem;
				font-size: 2.8rem;
			}

			.price {
				margin-top: 0.2rem;
				height: 2.4rem;
				line-height: 2.4rem;
				font-size: 1.8rem;
				font-weight: 500;
			}
		}
	}

	.btn-group {
		display: flex;
		flex: 1;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 1.6rem 1.6rem 0;

		.btn {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 0 1.6rem;
			margin-bottom: 1.6rem;
			width: 14.7rem;
			height: 4.3rem;
			background: #1A1E2D;
			border-radius: 0.2rem;

			.radio {
				width: 0.8rem;
				height: 0.8rem;
				background: var(--white-1);
				border-radius: 50%;

				&.active {
					background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
				}
			}

			.icon-40 {
				margin: 0 0.8rem;
			}

			.text {
				width: 7rem;
				font-size: 1.4rem;
				font-weight: 500;
				&.active {
					color: #EC6DC0;
				}
			}
		}
	}

	.confirm {
		margin: 0.8rem auto 0;
		width: 29.5rem;
		height: 4.8rem;
		line-height: 4.8rem;
		border-radius: 6rem;
		border: 0.1rem solid #1A1E2D;
		text-align: center;
	}
</style>