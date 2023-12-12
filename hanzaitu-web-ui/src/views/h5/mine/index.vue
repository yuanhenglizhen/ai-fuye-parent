<template>
	<div class="container">
		<gpt-navigation-bar :rightVisible="false">
			<template> 个人信息 </template>
		</gpt-navigation-bar>
		<div class="scroll-view" ref="scrollBox"  @scroll="scrolltolower($event)">
			<!-- 头像 -->
			<div @click="toggerAvatar()" class="avatar">
				<div class="avatar-icon" :class="'avatar'+userAvatar"></div>
			</div>
			<div class="avatar-group" v-show="showAvatarsGroup">
				<div @click.stop.prevent="changeAvatar(index)" v-for="(item,index) in avatarList"
					 :key="index" class="h5-avatar-icon img" :class="'h5avatar'+index"></div>
			</div>
			<!--用户信息 -->
			<div class="user-info">
				<div class="info-item">
					<div class="label grey">用户名</div>
					<div class="right">
						<div class="text">{{ currentUser.userName }}</div>
						<div class="link-btn" @click="updateUsernameVisible = true">修改</div>
					</div>
				</div>
				<div class="info-item">
					<div class="label grey">手机号码</div>
					<div class="right">
						<span class="text">+86 {{ userMobile }}</span>
						<div class="link-btn" @click="updatePhoneVisible = true">修改</div>
					</div>
				</div>
				<div class="info-item">
					<div class="label grey">登录密码</div>
					<div class="right">
						<div class="text grey">已设置</div>
						<div class="link-btn" @click="updatePasswordVisible = true">修改</div>
					</div>
				</div>
				<div class="info-item">
					<div class="label grey">我的积分</div>
					<div class="right">
						<div class="text">{{ currentUser.points }}</div>
						<div class="link-btn" @click="openWindow('/recharge')">充值</div>
						<div class="link-btn" @click="exchangeVisible=true">卡密兑换</div>
					</div>
				</div>
				<div class="info-item">
					<div class="label grey">我的邀请码</div>
					<div class="right">
						<div class="text link-btn">{{ currentUser.inviteCode || "-" }}</div>
						<div class="link-btn" v-clipboard:copy="currentUser.inviteCode"  v-clipboard:success="copy">复制邀请码</div>
					</div>
				</div>
				<div class="info-item">
					<div class="label grey">邀请人数</div>
					<div class="right">
						<div class="text">{{ currentUser.inviteCount }}人（+{{currentUser.inviteCount * 20 }}积分）</div>
					</div>
				</div>
				<!-- <div class="info-item">
					<text class="label grey">下载链接</text>
					<div class="right">
						<div class="text">www.baidu.com</div>
						<div class="link-btn" @click="copy('www.baidu.com')">复制链接</div>
					</div>
				</div> -->
			</div>

			<div class="btn-group">
				<div class="btn" :class="tabIndex == 0 ? 'bg-linear-radient' : 'black'" @click="tabIndex = 0">积分消费明细
				</div>
				<div class="btn" :class="tabIndex == 1 ? 'bg-linear-radient' : 'black'" @click="tabIndex = 1">邀请记录明细
				</div>
			</div>

			<div>
				<div v-if="tabIndex == 0">
					<div class="time-group">
						<div class="label font-28">按时间筛选</div>
						<div class="time font-28" @click="showTimePicker(walletRecordStartTime, 'walletRecordStartTime')">
							{{format(walletRecordStartTime)}}
							<div class="icon-20 icon-trangle bg-img"></div>
						</div>
						<div>-</div>
						<div class="time font-28" @click="showTimePicker(walletRecordEndTime, 'walletRecordEndTime')">
							{{format(walletRecordEndTime)}}
							<div class="icon-20 icon-trangle bg-img"></div>
						</div>
					</div>
					<div class="table" v-if="walletRecords.length">
						<div class="item" v-for="item in walletRecords" :key="item.id">
							<div class="row_1 font-28">{{item.recordTypeName}}</div>
							<div class="row_2 font-28">{{item.createTime}}</div>
							<div class="row_3 font-28" :class="item.points>0?'red': 'green'">
								{{item.points > 0 ? '+': ''}}
								{{item.points}}
							</div>
						</div>
					</div>
				</div>
				<div v-else>
					<div class="time-group">
						<div class="label font-28">按时间筛选</div>
						<div class="time font-28" @click="showTimePicker(inviteStartTime, 'inviteStartTime')">
							{{format(inviteStartTime)}}
							<div class="icon-20 icon-trangle bg-img"></div>
						</div>
						<div>-</div>
						<div class="time font-28" @click="showTimePicker(inviteEndTime, 'inviteEndTime')">
							{{format(inviteEndTime)}}
							<div class="icon-20 icon-trangle bg-img"></div>
						</div>
					</div>
					<div class="table" v-if="invites.length">
						<div class="item" v-for="item in invites" :key="item.id">
							<div class="row_1 font-28">{{item.userName}}</div>
							<div class="row_2 font-28">{{item.createTime}}</div>
							<div class="row_3 font-28" :class="item.points>0?'red': 'green'">
								{{item.points > 0 ? '+': ''}}{{item.points}}
							</div>
						</div>
					</div>
				</div>
			</div>
			<div style="height: 1px;"></div>
		</div>


		<update-phone v-model="updatePhoneVisible" @visibleChange="refreshList"></update-phone>
		<update-password v-model="updatePasswordVisible" @visibleChange="refreshList"></update-password>
		<update-username v-model="updateUsernameVisible" @visibleChange="refreshList"></update-username>
		<exchange v-model="exchangeVisible" @visibleChange="refreshList"></exchange>

		<my-datetime ref="dateTimePop" :popupShow="popupShow" @ok="timeOk" @close="close"></my-datetime>

	</div>
</template>

<script>
	import {
		mapState,
		mapActions
	} from "vuex";
	import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
	import UpdatePhone from "@/components/h5/business/update-phone.vue";
	import UpdatePassword from "@/components/h5/business/update-password.vue";
	import UpdateUsername from "@/components/h5/business/update-username.vue";
	import Exchange from "@/components/h5/business/exchange.vue";
	import MyDatetime from "@/components/h5/my-datetime/my-datetime.vue";
	import {
		listInviteUser,
		editAvatar,
		listUserWalletRecord
	} from "@/api/h5";
	import {
		addDays
	} from '@/utils/h5/date.extension.js'
	import {format} from '@/utils/tools'
	export default {
		components: {
			gptNavigationBar,
			UpdatePhone,
			UpdatePassword,
			UpdateUsername,
			Exchange,
			MyDatetime
		},
		data() {
			return {
				avatarList:20,
				showAvatarsGroup: false,
				avatars: [],
				updatePhoneVisible: false,
				updatePasswordVisible: false,
				updateUsernameVisible: false,
				exchangeVisible: false,
				tabIndex: 0,
				invites: [],
				walletRecords: [],
				timeField: '',
				walletRecordStartTime: addDays(new Date(), -6),
				walletRecordEndTime: new Date(),
				walletRecordPageNum: 1,
				walletRecordHasMore: false,
				inviteStartTime: addDays(new Date(), -6),
				inviteEndTime: new Date(),
				invitePageNum: 1,
				inviteHasMore: false,
				popupShow: false,

			};
		},
		computed: {
			...mapState("user", ["currentUser"]),
			userMobile() {
				return (
					this.currentUser.phoneNumber?.substr(0, 3) +
					"****" +
					this.currentUser.phoneNumber?.substr(7)
				);
			},
			userAvatar() {
				if (!this.currentUser.avatar) {
					return 0
				}else{
					let i = parseInt(this.currentUser.avatar);
					return i
				}
			}
		},
		created() {
			this.$ui.usingLoading(async () => {
				await this.GetUserInfo();
			});
			// 获取用户邀请明细信息
			this.invitePageNum = 1;
			this.invites = [];
			this.getInviteUser();
			// 获取钱包记录
			this.walletRecordPageNum = 1;
			this.walletRecords = [];
			this.getUserWalletRecord();
		},

		methods: {
			...mapActions("user", ["GetUserInfo"]),
			refreshList(){
				this.GetUserInfo();
			},
			scrolltolower(e) {
				let scrollHeight = this.$refs.scrollBox.scrollHeight;
				let clientHeight = this.$refs.scrollBox.clientHeight;
				let scrollTop = this.$refs.scrollBox.scrollTop;
				if(scrollHeight - (scrollTop+clientHeight) <= 1 ){
					if (this.tabIndex == 0) {
						if (this.walletRecordHasMore) {
							this.walletRecordPageNum++;
							this.getUserWalletRecord();
						}
					} else {
						if (this.inviteHasMore) {
							this.invitePageNum++;
							this.getInviteUser();
						}
					}
				}
			},
			getInviteUser() {
				listInviteUser(
						format(this.inviteStartTime),
						format(this.inviteEndTime),
						this.invitePageNum
					)
					.then(({
						data
					}) => {
						if (data.list.length) {
							this.inviteHasMore = true;
							this.invites = this.invites.concat(data.list);
						} else {
							this.inviteHasMore = false;
						}

					});
			},
			getUserWalletRecord() {
				listUserWalletRecord(
					this.format(this.walletRecordStartTime),
					this.format(this.walletRecordEndTime),
					this.walletRecordPageNum
				).then(
					({
						data
					}) => {
						if (data.list.length) {
							this.walletRecordHasMore = true;
							this.walletRecords = this.walletRecords.concat(data.list);
						} else {
							this.walletRecordHasMore = false;
						}
					});
			},
			// 复制文本
			copy() {
				this.$message({
					message:'复制成功！',
					type:'success'
				})
			},
			openWindow(page) {
				this.$router.push(page);
			},
			async changeAvatar(item) {
				await editAvatar(item);
				await this.GetUserInfo();
				this.showAvatarsGroup = false;
			},
			toggerAvatar(){
				if(this.showAvatarsGroup == true){
					this.showAvatarsGroup = false;
				}else{
					this.showAvatarsGroup = true;
				}
			},
			// 关闭弹窗
			timeOk(str) {
				this.checkTime(str);
				if (this.timeField === 'walletRecordStartTime' || this.timeField === 'walletRecordEndTime') {
					this.walletRecordPageNum = 1;
					this.walletRecords = [];
					this.getUserWalletRecord();
				}
				if (this.timeField === 'inviteStartTime' || this.timeField === 'inviteEndTime') {
					this.invitePageNum = 1;
					this.invites = [];
					this.getInviteUser();
				}
			},
			checkTime(ss) {
				if (this.timeField === 'walletRecordStartTime') {
					this.walletRecordStartTime = ss;
				}
				if (this.timeField === 'walletRecordEndTime') {
					this.walletRecordEndTime = ss;
				}
				if (this.timeField === 'inviteStartTime') {
					this.inviteStartTime = ss;
				}
				if (this.timeField === 'inviteEndTime') {
					this.inviteEndTime = ss;
				}
			},
			showTimePicker(time, field) {
				this.timeField = field;
				this.popupShow = true;
			},
			format(date) {
				return format(date)
			},
			close(e){
				this.popupShow = e;
			},

		},
	};
</script>

<style lang="scss" scoped>
	.scroll-view {
		height: calc(100vh - 4.5rem);
		box-sizing: border-box;
		overflow-y: auto;
	}

	.container {
		overflow: hidden;
		width: 100%;
		min-height: 100%;
		color: var(--white-1);
		background: #080b16;

		.avatar {
			display: block;
			margin: 2rem auto 0;
			width: 9.6rem;
			height: 9.6rem;
			border-radius: 50%;
			position: relative;
		}

		.avatar-group {
			position: absolute;
			left: 2.4rem;
			display: flex;
			flex-wrap: wrap;
			justify-content: center;
			margin-top: 0.4rem;
			padding: 0.875rem 0;
			width: 32.7rem;
			background: #1a1e2d;

			.img {
				overflow: hidden;
				margin: 0.65rem;
				width: 6.4rem;
				height: 6.4rem;
				border-radius: 50%;
				// clip-path: inset(0 round 10px);
			}
			.h5-avatar-icon{
				background: url("../../../assets/img/avatar/personal_avatar.png") no-repeat;
				background-size: 363px 288px;
			}
			.h5avatar0{background-position: 0 0}
			.h5avatar1{background-position: -74.6px 0}
			.h5avatar2{background-position: -150px 0}
			.h5avatar3{background-position: -224px 0}
			.h5avatar4{background-position: -298.4px 0}
			.h5avatar5{background-position: 0 -74.6px}
			.h5avatar6{background-position: -74.6px -74.6px}
			.h5avatar7{background-position: -150px -74.6px}
			.h5avatar8{background-position: -224px -74.6px}
			.h5avatar9{background-position: -298.4 -74.6px}
			.h5avatar10{background-position: 0 -150px}
			.h5avatar11{background-position: -74.6px -150px}
			.h5avatar12{background-position: -150px -150px}
			.h5avatar13{background-position: -224px -150px}
			.h5avatar14{background-position: -298.4px -150px}
			.h5avatar15{background-position: -0px -224px}
			.h5avatar16{background-position: -74.6px -224px}
			.h5avatar17{background-position: -150px -224px}
			.h5avatar18{background-position: -224px -224px}
			.h5avatar19{background-position: -298.4px -224px}

		}

		.user-info {
			display: flex;
			flex-direction: column;
			margin: -3.6rem auto 0;
			padding: 5.9rem 1.6rem 0;
			width: 32.7rem;
			background: #11141e;
			border-radius: 0.8rem;

			.info-item {
				display: flex;
				justify-content: space-between;
				align-items: center;
				height: 5.4rem;
				background: #11141e;

				&:not(:last-child) {
					border-bottom: 0.1rem solid #172042;
				}

				.grey {
					color: #94a3b8;
					font-size: 1.4rem;
				}

				.right {
					display: flex;
					font-size: 1.4rem;
					.text{
						display: flex;
						align-items: center;
					}
					.link-btn {
						max-width: 11rem;
						padding-left: 0.8rem;
						color: #5e86d6;

						overflow: hidden;
						text-overflow: ellipsis;
						white-space: nowrap;
					}
				}
			}
		}

		.btn-group {
			display: flex;
			justify-content: center;
			padding: 4.4rem 0 2.3rem;
			width: 100%;

			.btn {
				margin: 0 1.6rem;
				width: 11.6rem;
				height: 3.2rem;
				line-height: 3.2rem;
				border-radius: 1.6rem;
				font-size: 1.4rem;
				font-weight: 400;
				text-align: center;

				&.black {
					background: #11182f;
				}
			}
		}

		.time-group {
			display: flex;
			align-items: center;
			margin: 0 auto 1.6rem;
			width: 34.3rem;

			.label {
				margin-right: 0.8rem;
				width: 7rem;
				color: #5E86D6;
			}

			.time {
				display: flex;
				align-items: center;
				margin: 0 0.8rem;

				.bg-img {
					margin-left: 0.8rem;
				}
			}
		}

		.table {
			overflow: hidden;
			margin: auto;
			width: 34.3rem;
			border: 0.1rem solid rgba(255, 255, 255, 0.5);
			border-radius: 0.4rem;
			background: #2e354f;
			font-size: 1.4rem;

			&:last-child {
				margin: 1.6rem auto 4.9rem;
			}

			.item {
				position: relative;
				height: 7.3rem;

				// &:first-child {
				// 	background: #1a1e2d;
				// }

				&:not(:first-child) {
					border-top: 0.1rem solid rgba(255, 255, 255, 0.5);
				}

				.row {
					&_1 {
						position: absolute;
						top: 1.2rem;
						left: 1.6rem;
						height: 2.2rem;
					}

					&_2 {
						position: absolute;
						top: 3.6rem;
						left: 1.6rem;
						// width: 260rem;
						height: 2.2rem;
						color: #808DBF;
					}

					&_3 {
						position: absolute;
						top: 2.3rem;
						right: 1.6rem;
						height: 2.2rem;

						&.red {
							color: #F53F3F;
						}

						&.green {
							color: #00B42A;
						}
					}
				}
			}
		}
	}
</style>