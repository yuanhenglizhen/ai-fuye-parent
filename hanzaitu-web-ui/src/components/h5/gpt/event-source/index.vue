<template>
	<div v-show="false">{{renderjsVal}}</div>
</template>

<script>
	import {
		getToken
	} from "@/utils/h5/auth";
	import {
		sendMsg
	} from "@/api/h5/chat-eventsource";
	import {
		randomString
	} from "@/utils/h5/common";
	import {fetchEventSource} from '@microsoft/fetch-event-source';
	import {baseUrl} from "@/api/api";
	export default {
		props: {
			eventId: String,
			isGpt4: Boolean,
			prompt: String,
			ran: String,
			gtpModel: String
		},
		computed: {
			renderjsVal() {
				let renderjsVal = `${getToken()};${this.eventId};${this.prompt || ''};${
        this.isGpt4 ? "4" : "3.5"};${this.gtpModel};${this.ran}`;

				// #ifdef H5
				this.openEventSource(renderjsVal);
				// #endif

				return renderjsVal;
			}
		},
		methods: {
			emitMessage(e) {
				// #ifdef H5
				this.$emit('message', JSON.parse(e));
				// #endif
			},
			emitError(e) {
				this.$emit('error', e);
			},
			emitClose(e) {
				this.$emit('close', e);
			},
			emitOpen(e) {
				this.$emit('open', e);
			},
			openEventSource(newValue, oldValue, ownerInstance, instance) {
				if (newValue) {
					var arr = newValue.split(";");

					var token = arr[0];
					var winId = arr[1];
					var content = arr[2];
					var version = arr[3];
					var gtpModel = arr[4];

					if (!token) {
						return
					}
					if (!winId) {
						return
					}
					if (!content) {
						return
					}
					if (!version) {
						return
					}
					if (!gtpModel) {
						return
					}

					let that = this


					const prefixUrl = "/chat";
					const postData = {
						"winId":winId,
						"msgId":randomString(14),
						"model":gtpModel,
						"prompt":content
					}
					let controller = new AbortController()
					var source = fetchEventSource(
							version == '4' ? `${baseUrl}${prefixUrl}/gpt4/sse/post` : `${baseUrl}${prefixUrl}/sse/post`,
							{
								headers: {
									'Content-Type': 'application/json',
									"Access-Token": token,
									"Access-Token-Hzt": token,
								},
								method: 'POST',
								body: JSON.stringify(postData),
								signal: controller.signal,
								onopen(e) {
									console.log('open')
									that.emitOpen(e)
								},
								onmessage(e) {
									that.emitMessage(e.data)
								},
								onclose() {
									console.log("回答结束");
									that.emitClose();
									controller.abort();//出错后不要重试
									source.close();
								},
								onerror(e) {
									console.log('close',e)
									that.emitError(e)
									controller.abort();//出错后不要重试
									source.close();

								}
							}
					); //请求路径




					// 接受数据
					/*const source = sendMsg(
						token,
						winId,
						randomString(14),
						content,
						gtpModel,
						version == '4'
					);

					//建立连接
					source.onopen = (e) => {
						console.log(`onopen`)
						that.emitOpen(e)
					};
					source.onmessage = (e) => {
						that.emitMessage(e.data)
					};
					source.onclose = (e) => {
						console.log(`onclose`);
						that.emitError(e)
						source.close();
					};
					//发送错误
					source.onerror = (e) => {
						console.log(`onerror`);
						that.emitError(e)
						source.close();
					};*/
				}
			},
		}
	}
</script>
