<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="语义基础分类" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="messageId">
         <j-dict-select-tag type="list" v-model="model.messageId" dictCode="tab_chat_type,type_name,id" placeholder="请选择语义基础分类" @change="senchType" />
         
            </a-form-model-item>
          </a-col>
     <!--     <a-col :span="24">
            <a-form-model-item label="语义名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="messageName">
              <a-input v-model="model.messageName" placeholder="请输入语义名称"  ></a-input>
            </a-form-model-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-model-item label="关键词" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="messageKey">
  <!--    <j-dict-select-tag type="list" disabled="true" v-model="model.messageKey" dictCode="tab_message_type,message_keywords,id" placeholder="请选择关键词分类" />
        --> <a-input v-model="model.messageKey" placeholder="请输入关键词"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="语句内容" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="messageInfo">
              <a-input v-model="model.messageInfo" placeholder="请输入语句内容"  ></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'TabMessageListForm',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        model:{
         },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
        },
        url: {
          add: "/chat/tabMessageList/add",
          edit: "/chat/tabMessageList/edit",
          queryById: "/chat/tabMessageList/queryById",
           queryBychatTypeId: "/chat/tabMessageType/queryBychatTypeId"
        }
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
       //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      add () {
        this.edit(this.modelDefault);
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      senchType(e){
        let params={};
        httpAction(this.url.queryBychatTypeId+"?id="+e,params,"get").then((res)=>{
          if(res.success){
            console.log(res);
           //sss this.model.messageKey=res.result.id;
          }else{
            that.$message.warning(res.message);
          }
        }).finally(() => {
         
        })
      },
      submitForm () {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
         
        })
      },
    }
  }
</script>