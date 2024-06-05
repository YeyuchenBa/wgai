<template>
  <a-row type="flex" :gutter="16">
 
    <a-col :md="24" :sm="24">
      <test-video style="
    width: 25%;
    float: right;
" v-model="currentOrgCode" />
      <test-video1 style="
    width: 25%;
    float: right;
" v-model="currentOrgCode2" />
       <test-video2 style="
    width: 25%;
    float: right;
"  v-model="currentOrgCode3" />
  <test-video4 style="
    width: 25%;
    float: right;
"  v-model="currentOrgCode4" />
    </a-col>
 
   

    </a-col>
  </a-row>
</template>

<script>

  import testVideo from './testVideo'
  import testVideo1 from './testVideo1'
  import testVideo2 from './testVideo2'
   import testVideo4 from './testVideo4'
  export default {
    name: 'AddressList',
    components: { testVideo,testVideo1,testVideo2,testVideo4 },
    data() {
      return {
        description: 'AI视频分析',
        currentOrgCode: '',
        currentOrgCode2: '',
        currentOrgCode3: '',
          currentOrgCode4: ''
      }
    },

    methods: {}
  }
</script>
<style scoped>
  @import '~@assets/less/commondiy.less';
 .ant-card-body {
   padding: 0 !important;
 }
</style>