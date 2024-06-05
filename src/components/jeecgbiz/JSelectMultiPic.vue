<template>
  <!-- 定义在这里的参数都是不可在外部覆盖的，防止出现问题 -->
  <j-select-biz-component :value="value" :ellipsisLength="25" :listUrl="url.list" :columns="columns" v-on="$listeners"
    v-bind="attrs" />
</template>

<script>
  import JDate from '@comp/jeecg/JDate'
  import JSelectBizComponent from './JSelectBizComponent'

  export default {
    name: 'JSelectMultiUser',
    components: {
      JDate,
      JSelectBizComponent
    },
    props: {
      value: null, // any type
      queryConfig: {
        type: Array,
        default: () => []
      },
    },
    data() {
      return {
        url: {
        list: "/easy/tabEasyPic/list",
        },
        columns: [ {
            title:'图片类型',
            align:"center",
            dataIndex: 'picType_dictText'
          },
          {
            title:'图片名称',
            align:"center",
            dataIndex: 'picName'
          },
          {
            title:'图片地址',
            align:"center",
            dataIndex: 'picUrl',
            scopedSlots: {customRender: 'imgSlot'}
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remake'
          },
        ],
      }
    },
    computed: {
      attrs() {
        return Object.assign(this.default, this.$attrs, {
          queryConfig: this.queryConfigDefault.concat(this.queryConfig)
        })
      }
    },
    methods: {
      
      show(info) {

        
       
       
      },
    }
  }
</script>

<style lang="less" scoped></style>
