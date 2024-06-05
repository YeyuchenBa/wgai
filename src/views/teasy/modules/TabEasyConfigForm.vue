<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="任务名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="confName">
              <a-input v-model="model.confName" placeholder="请输入任务名称"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="训练种类" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="typeNum">
              <a-input-number v-model="model.typeNum" placeholder="请输入训练种类" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="训练物体大小px" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="boxSize">
              <a-input-number v-model="model.boxSize" placeholder="请输入训练物体大小px" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="图片数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="picNum">
              <a-input-number v-model="model.picNum" placeholder="请输入图片数量" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="可信阈值" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="pth">
              <a-input-number v-model="model.pth" placeholder="请输入可信阈值" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="背景图片" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="backUrl">
              <j-image-upload isMultiple v-model="model.backUrl"></j-image-upload>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="是否打印训练日志" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="logStatic">
              <!--  <a-input v-model="model.logStatic" placeholder="请输入是否训练日志"></a-input> -->
              <j-dict-select-tag v-model="model.logStatic" placeholder="是否打印训练日志" dictCode="is_print" />
            </a-form-model-item>
          </a-col>
          <!--  <a-col :span="12">
            <a-form-model-item label="任务状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status">
              <a-input v-model="model.status" placeholder="请输入任务状态"></a-input>
            </a-form-model-item>
          </a-col> -->
          <!--  <a-col :span="12">
            <a-form-model-item label="绑定训练图片" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="picList">
              <j-popup
                v-model="model.picList"
                field="picList"
                org-fields=""
                dest-fields=""
                code=""
                :multi="true"
                @input="popupCallback"
                />
            </a-form-model-item>
          </a-col> -->
          <a-col :span="12">
            <a-form-model-item label="识别类别" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="picTypeId">
              <j-multi-select-tag type="list_multi" v-model="model.picTypeId" dictCode="pic_type"
                placeholder="请选择识别类别" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>
  import {
    httpAction,
    getAction
  } from '@/api/manage'
  import {
    validateDuplicateValue
  } from '@/utils/util'

  export default {
    name: 'TabEasyConfigForm',
    components: {},
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data() {
      return {
        model: {},
        labelCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 8
          },
        },
        wrapperCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 16
          },
        },
        confirmLoading: false,
        validatorRules: {},
        url: {
          add: "/easy/tabEasyConfig/add",
          edit: "/easy/tabEasyConfig/edit",
          queryById: "/easy/tabEasyConfig/queryById"
        }
      }
    },
    computed: {
      formDisabled() {
        return this.disabled
      },
    },
    created() {
      //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      add() {
        this.edit(this.modelDefault);
      },
      edit(record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      submitForm() {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if (!this.model.id) {
              httpurl += this.url.add;
              method = 'post';
            } else {
              httpurl += this.url.edit;
              method = 'put';
            }
            httpAction(httpurl, this.model, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.$emit('ok');
              } else {
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }

        })
      },
      popupCallback(value, row) {
        this.model = Object.assign(this.model, row);
      },
    }
  }
</script>
