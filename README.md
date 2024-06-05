Ant Design Jeecg Vue（JeecgBoot 低代码平台）
====

当前最新版本： 3.4.3（发布日期：20221107）

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zhangdaiscott/jeecg-boot/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-北京敲敲云科技-orange.svg)](http://www.jeecg.com)
[![](https://img.shields.io/badge/Blog-官方博客-blue.svg)](https://jeecg.blog.csdn.net)
[![](https://img.shields.io/badge/version-3.4.3-brightgreen.svg)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub stars](https://img.shields.io/github/stars/zhangdaiscott/jeecg-boot.svg?style=social&label=Stars)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub forks](https://img.shields.io/github/forks/zhangdaiscott/jeecg-boot.svg?style=social&label=Fork)](https://github.com/zhangdaiscott/jeecg-boot)



Overview
----

基于 [Ant Design of Vue](https://vuecomponent.github.io/ant-design-vue/docs/vue/introduce-cn/) 实现的 Ant Design Pro  Vue 版
Jeecg-boot 的前端UI框架，采用前后端分离方案，提供强大代码生成器的低代码平台。前端页面代码和后端功能代码一键生成，不需要写任何代码，保持jeecg一贯的强大！！
 
> 强大的代码生成器让前后端代码一键生成! JeecgBoot引领低代码开发模式(OnlineCoding-> 代码生成-> 手工MERGE)， 帮助解决Java项目70%的重复工作，让开发更多关注业务。既能快速提高效率，节省成本，同时又不失灵活性


## 项目介绍 
说明：JeecgBoot前端提供两套解决方案，一套VUE2和一套VUE3版本，目前vue2版本最新代码只支持到jeecgboot 3.4.3版本，一定注意。


## Vue2与Vue3版本区别
> - VUE3版本彻底抛弃IE兼容，不兼容IE和低版本浏览器，只适配高版本谷歌和Edge
 （政府、事业类单位项目需要谨慎选择——国产化迁移是一个漫长的过程，万一过程中要求IE兼容，这个不可逆）
> - 所以如果对浏览器有要求的项目，请选择VUE2版本。
> - VUE3版是全新的技术栈，紧跟主流（前端重写），各个功能都做了优化，拥有更好的体验效果




##  项目源码

| 仓库  | 前端源码Vue2版 | 后端源码 |
|-|-|-|
| Github   | [ant-design-vue-jeecg](https://github.com/jeecgboot/ant-design-vue-jeecg) | [jeecg-boot](https://gitee.com/jeecg/jeecg-boot/tree/v3.4.3last) |
| 码云  | [ant-design-vue-jeecg](https://gitee.com/jeecg/ant-design-vue-jeecg)  | [jeecg-boot](https://github.com/jeecgboot/jeecg-boot/tree/v3.4.3) |


## 项目说明

| 项目名                | 说明                     | 
|--------------------|------------------------|
| `jeecg-boot`    | JAVA后台（支持微服务）        | 
| `ant-design-vue-jeecg`  |Vue2版前端代码   |   


## 技术支持


本项目关闭issue，使用中遇到问题或者BUG可以在 [JeecgBoot主项目上提Issues](https://github.com/jeecgboot/jeecg-boot/issues/new)

官方支持： http://jeecg.com/doc/help

技术文档： http://doc.jeecg.com




## 前端技术栈
 
  > 此处是Vue2版的技术栈介绍

- 基础框架：[ant-design-vue](https://github.com/vueComponent/ant-design-vue) - Ant Design Of Vue 实现
- JavaScript框架：Vue
- node
- yarn
- @vue/cli 3.2.1
- [vue-cropper](https://github.com/xyxiao001/vue-cropper) - 头像裁剪组件
- [@antv/g2](https://antv.alipay.com/zh-cn/index.html) - Alipay AntV 数据可视化图表
- [Viser-vue](https://viserjs.github.io/docs.html#/viser/guide/installation)  - antv/g2 封装实现
- [Vue 2.6.10](https://cn.vuejs.org/),[Vuex](https://vuex.vuejs.org/zh/),[Vue Router](https://router.vuejs.org/zh/)
- [Axios](https://github.com/axios/axios)
- [webpack](https://www.webpackjs.com/),[yarn](https://yarnpkg.com/zh-Hans/)
- eslint，[@vue/cli 3.2.1](https://cli.vuejs.org/zh/guide)
- vue-print-nb-jeecg - 打印





## 项目下载和运行


- 拉取项目代码
```bash
git clone https://github.com/zhangdaiscott/jeecg-boot.git
cd  jeecg-boot/ant-design-vue-jeecg
```

- 安装依赖
```
yarn install
```

- 开发模式运行
```
yarn run serve
```

- 编译项目
```
yarn run build
```

- Lints and fixes files
```
yarn run lint
```

Docker镜像启动前端（单体模式）
----

 ``` 
# 1.配置host

    127.0.0.1   jeecg-boot-system

# 2.修改前端项目的后台域名
    .env.development
    域名改成： http://jeecg-boot-system:8080/jeecg-boot
   
# 3.进入项目根目录，执行打包命令
  yarn run build

# 4.构建镜像
  docker build -t jeecgboot-ui2 .

# 5.启动镜像
  docker run --name jeecgboot-ui-vue2 -p 80:80 -d jeecgboot-ui2

# 6.访问前台项目
  http://localhost
``` 



其他说明
----

- 项目使用的 [vue-cli3](https://cli.vuejs.org/guide/), 请更新您的 cli

- 关闭 Eslint (不推荐) 移除 `package.json` 中 `eslintConfig` 整个节点代码

- 修改 Ant Design 配色，在文件 `vue.config.js` 中，其他 less 变量覆盖参考 [ant design](https://ant.design/docs/react/customize-theme-cn) 官方说明
```ecmascript 6
  css: {
    loaderOptions: {
      less: {
        modifyVars: {
          /* less 变量覆盖，用于自定义 ant design 主题 */

          'primary-color': '#F5222D',
          'link-color': '#F5222D',
          'border-radius-base': '4px',
        },
        javascriptEnabled: true,
      }
    }
  }
```



附属文档
----
- [Ant Design Vue](https://vuecomponent.github.io/ant-design-vue/docs/vue/introduce-cn)

- [报表 viser-vue](https://viserjs.github.io/demo.html#/viser/bar/basic-bar)

- [Vue](https://cn.vuejs.org/v2/guide)

- [路由/菜单说明](https://github.com/zhangdaiscott/jeecg-boot/tree/master/ant-design-vue-jeecg/src/router/README.md)

- [ANTD 默认配置项](https://github.com/zhangdaiscott/jeecg-boot/tree/master/ant-design-vue-jeecg/src/defaultSettings.js)

- 其他待补充...


备注
----

> @vue/cli 升级后，eslint 规则更新了。由于影响到全部 .vue 文件，需要逐个验证。既暂时关闭部分原本不验证的规则，后期维护时，在逐步修正这些 rules



系统效果
----
##### 大屏模板
![输入图片说明](https://static.oschina.net/uploads/img/201912/25133248_Ag1C.jpg "在这里输入图片标题")

![输入图片说明](https://static.oschina.net/uploads/img/201912/25133301_k9Kc.jpg "在这里输入图片标题")

##### PC端
![输入图片说明](https://static.oschina.net/uploads/img/201904/14155402_AmlV.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160657_cHwb.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160813_KmXS.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160935_Nibs.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14161004_bxQ4.png "在这里输入图片标题")


##### 在线接口文档
![输入图片说明](https://static.oschina.net/uploads/img/201908/27095258_M2Xq.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160957_hN3X.png "在这里输入图片标题")


##### 报表
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160828_pkFr.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160834_Lo23.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160842_QK7B.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160849_GBm5.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160858_6RAM.png "在这里输入图片标题")

##### 流程
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160623_8fwk.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160917_9Ftz.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201904/14160633_u59G.png "在这里输入图片标题")
![输入图片说明](https://static.oschina.net/uploads/img/201907/05165142_yyQ7.png "在这里输入图片标题")


##### 手机端
![](https://oscimg.oschina.net/oscnet/da543c5d0d57baab0cecaa4670c8b68c521.jpg)
![](https://oscimg.oschina.net/oscnet/fda4bd82cab9d682de1c1fbf2060bf14fa6.jpg)

##### PAD端
![](https://oscimg.oschina.net/oscnet/e90fef970a8c33790ab03ffd6c4c7cec225.jpg)
![](https://oscimg.oschina.net/oscnet/d78218803a9e856a0aa82b45efc49849a0c.jpg)
![](https://oscimg.oschina.net/oscnet/0404054d9a12647ef6f82cf9cfb80a5ac02.jpg)
![](https://oscimg.oschina.net/oscnet/59c23b230f52384e588ee16309b44fa20de.jpg)




