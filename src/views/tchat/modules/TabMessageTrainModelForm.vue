<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="模型标题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelTitle">
              <a-input v-model="model.modelTitle" placeholder="请输入模型标题"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="语义分类数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelTypeNumber">
              <a-input v-model="model.modelTypeNumber" placeholder="请输入语义分类数"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="语义分类嵌入维度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelMessageInlay">
              <a-input v-model="model.modelMessageInlay" placeholder="请输入语义分类嵌入维度"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="问答词嵌入维度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelBackInlay">
              <a-input v-model="model.modelBackInlay" placeholder="请输入问答词嵌入维度"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="用户语句最大长度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userMessageLenght">
              <a-input v-model="model.userMessageLenght" placeholder="请输入用户语句最大长度"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="最大回复长度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelMessageLenght">
              <a-input v-model="model.modelMessageLenght" placeholder="请输入最大回复长度"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="模型训练增强" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelEnhancement">
              <a-input v-model="model.modelEnhancement" placeholder="请输入模型训练增强"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="正则抑制系数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="inhibitionCoefficient">
              <a-input v-model="model.inhibitionCoefficient" placeholder="请输入正则抑制系数"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="生成语义可信阈值" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelThreshold">
              <a-input-number v-model="model.modelThreshold" placeholder="请输入生成语义可信阈值" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="生成语义分类可信阈值" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelTypeThreshold">
              <a-input v-model="model.modelTypeThreshold" placeholder="请输入生成语义分类可信阈值"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="是否打印日志" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="debugStatus">
              <a-input v-model="model.debugStatus" placeholder="请输入是否打印日志"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="日志详细信息" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="debugId">
              <a-input v-model="model.debugId" placeholder="请输入日志详细信息"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="备注 " :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" placeholder="请输入备注 "  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="关键词敏感颗粒度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelSensitivity">
              <a-input v-model="model.modelSensitivity" placeholder="请输入关键词敏感颗粒度"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="使用状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="modelStatus">
              <a-input v-model="model.modelStatus" placeholder="请输入使用状态"  ></a-input>
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
    name: 'TabMessageTrainModelForm',
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
          xs: { span: 10 },
          sm: { span: 10 },
        },
        wrapperCol: {
          xs: { span: 12 },
          sm: { span: 12 },
        },
        confirmLoading: false,
        validatorRules: {
        },
        url: {
          add: "/chat/tabMessageTrainModel/add",
          edit: "/chat/tabMessageTrainModel/edit",
          queryById: "/chat/tabMessageTrainModel/queryById"
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