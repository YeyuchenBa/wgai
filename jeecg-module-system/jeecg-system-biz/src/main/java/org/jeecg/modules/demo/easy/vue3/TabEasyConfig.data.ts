import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '创建日期',
    align:"center",
    dataIndex: 'createTime'
   },
   {
    title: '任务名称',
    align:"center",
    dataIndex: 'confName'
   },
   {
    title: '训练种类',
    align:"center",
    dataIndex: 'typeNum'
   },
   {
    title: '训练物体大小px',
    align:"center",
    dataIndex: 'boxSize'
   },
   {
    title: '图片数量',
    align:"center",
    dataIndex: 'picNum'
   },
   {
    title: '可信阈值',
    align:"center",
    dataIndex: 'pth'
   },
   {
    title: '背景图片',
    align:"center",
    dataIndex: 'backUrl',
    customRender:render.renderImage,
   },
   {
    title: '是否训练日志',
    align:"center",
    dataIndex: 'logStatic'
   },
   {
    title: '任务状态',
    align:"center",
    dataIndex: 'status'
   },
   {
    title: '绑定训练图片',
    align:"center",
    dataIndex: 'picList'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "任务名称",
      field: 'confName',
      component: 'Input',
      colProps: {span: 6},
 	},
	{
      label: "可信阈值",
      field: 'pth',
      component: 'Input',
      colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '任务名称',
    field: 'confName',
    component: 'Input',
  },
  {
    label: '训练种类',
    field: 'typeNum',
    component: 'InputNumber',
  },
  {
    label: '训练物体大小px',
    field: 'boxSize',
    component: 'InputNumber',
  },
  {
    label: '图片数量',
    field: 'picNum',
    component: 'InputNumber',
  },
  {
    label: '可信阈值',
    field: 'pth',
    component: 'InputNumber',
  },
  {
    label: '背景图片',
    field: 'backUrl',
     component: 'JImageUpload',
     componentProps:{
      },
  },
  {
    label: '是否训练日志',
    field: 'logStatic',
    component: 'Input',
  },
  {
    label: '任务状态',
    field: 'status',
    component: 'Input',
  },
  {
    label: '绑定训练图片',
    field: 'picList',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"",
            fieldConfig: [
                { source: '', target: '' },
            ],
            multi:true
        }
    },

  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];
//子表单数据
//子表表格配置

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}