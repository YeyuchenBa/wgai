
<template>
  <div class="chat-container">
    <a-list class="chat-list" :dataSource="chatMessages" :split="false">
      <a-list-item slot="renderItem" slot-scope="item">
        <a-list-item-meta :description="item.content">
          <span slot="title">{{ item.author }}</span>
          <a-avatar slot="avatar" src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png" />
        </a-list-item-meta>
      </a-list-item>
    </a-list>
    <a-input class="chat-input" placeholder="请输入聊天内容" v-model="inputValue" @pressEnter="sendMessage" />
  </div>
</template>

<script>
  export default {
    data() {
      return {
        chatMessages: [
          // 初始聊天记录列表
          {
            author: 'Alice',
            content: 'Hello, AI_WG!'
          },
          {
            author: 'AI_WG',
            content: 'Hello, admin!'
          }
        ],
        inputValue: ''
      };
    },
    methods: {
      sendMessage() {
        if (this.inputValue) {
          const newMessage = {
            author: 'Alice',
            content: this.inputValue
          };
          this.chatMessages.push(newMessage);
          this.inputValue = '';
          // 在这里可以添加发送消息到服务器的逻辑
        }
      }
    }
  };
</script>

<style scoped>
  .chat-container {
    display: flex;
    flex-direction: column;
    height: 500px;
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
