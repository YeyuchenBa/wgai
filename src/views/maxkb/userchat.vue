<template>
  <div class="chat-container">
       <iframe 
       :src="url"
       style="width: 100%; height: 100%;" 
       frameborder="0" 
       allow="microphone">
       </iframe>

  </div>
</template>

<script>
  export default {
    data() {
      return {
        url:"https://chat.baidu.com/"
      };
    },
   // 在目标组件中
   created() {
     const value = this.$route.query.key;
     console.log("输出内容",this.$route.query)
    // alert(value)
    if(value){
        this.url=value;
    }
   
   },
    methods: {
      
      
    }
  };
</script>

<style scoped>
  .chat-container {
    display: flex;
    flex-direction: column;
    height: 800px;
  }

  .chat-list {
    flex: 1;
    overflow-y: scroll;
    padding-bottom: 10px;
  }

  .chat-input {
    height: 40px;
    border: 1px solid #e9e9e9;
  }
</style>