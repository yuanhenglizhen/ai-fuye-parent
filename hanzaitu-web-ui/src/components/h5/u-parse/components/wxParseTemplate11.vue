<template>
	<div>
		<!--判断是否是标签节点-->
		<div v-if="node.node == 'element'">
			<!--button类型-->
			<div v-if="node.tag == 'button'">
				<button type="default" size="mini">
				</button>
			</div>

			<!--li类型-->
			<div v-else-if="node.tag == 'li'">
				<div :class="node.classStr" :style="node.styleStr">
					{{node.text}}
				</div>
			</div>

			<!--video类型-->
			<div v-else-if="node.tag == 'video'">
				<wx-parse-video :node="node" />
			</div>

			<!--audio类型-->
			<div v-else-if="node.tag == 'audio'">
				<wx-parse-audio :node="node" />
			</div>

			<!--img类型-->
			<div v-else-if="node.tag == 'img'">
				<wx-parse-img :node="node" />
			</div>

			<!--a类型-->
			<div v-else-if="node.tag == 'a'">
				<div @click="wxParseATap" :class="node.classStr" :data-href="node.attr.href" :style="node.styleStr">
					{{node.text}}
				</div>
			</div>

			<!--br类型-->
			<div v-else-if="node.tag == 'br'">
				<text>\n</text>
			</div>

			<!--其他标签-->
			<div v-else>
				<div :class="node.classStr" :style="node.styleStr">
					{{node.text}}
				</div>
			</div>
		</div>

		<!--判断是否是文本节点-->
		<div v-else-if="node.node == 'text'">{{node.text}}</div>
	</div>
</template>

<script>
	import wxParseImg from './wxParseImg';
	import wxParseVideo from './wxParseVideo';
	import wxParseAudio from './wxParseAudio';

	export default {
		name: 'wxParseTemplate11',
		props: {
			node: {},
		},
		components: {
			wxParseImg,
			wxParseVideo,
			wxParseAudio,
		},
		methods: {
			wxParseATap(e) {
				const {
					href
				} = e.currentTarget.dataset;
				if (!href) return;
				let parent = this.$parent;
				while(!parent.preview || typeof parent.preview !== 'function') {
					parent = parent.$parent;
				}
				parent.navigate(href, e);
			},
		},
	};
</script>
