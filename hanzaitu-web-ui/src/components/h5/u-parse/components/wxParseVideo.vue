<template>
  <!--增加video标签支持，并循环添加-->
  <div :class="node.classStr" :style="node.styleStr">
    <video :class="node.classStr" class="video-video" :src="node.attr.src"></video>
  </div>
</template>

<script>
export default {
  name: 'wxParseVideo',
  props: {
    node: {},
  },
};
</script>
