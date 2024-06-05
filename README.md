# 简介
> [<span style="color: red;">@官方文档</span>](http://116.198.227.105/#/)
> 开箱即用的JAVA AI平台融合了AI图像识别opencv、yolo、esayAI内核识别;AI智能客服、AI语言模型、可定制化自主离线化部署并自主化行业化使用
避免占用内存、GPU消耗训练与识别分开使用;支持yolov3、yolov5、yolov8模型 支持exel、txt等文本语言模型；

# 直接上手

## 下载即可使用 
> springboot+vue+mysql 支持国产化数据库 配置内容不再详细赘述 如果java 薄弱建议先学习java 再来使用

> [<span style="color: red;">@gitee仓库地址</span>](https://gitee.com/wggh_admin/wgai)

>[<span style="color: red;">@github仓库地址</span>](https://github.com/YeyuchenBa/wgai)

# 功能截图和功能介绍配置

## - 自主图片视频识别

###  首页       
>监管redis、jvm、服务器cpu 主要是cpu 尤为重要.

![index](./index.jpg)

###  模型库     
>自主添加训练好的模型,训练模型与识别是分开的,避免占用内存.

![model](./model.jpg)

### 模型库绑定     
>支持图片上传、图片地址、视频地址、rtsp、rtmp、flv、fmp4、不支持静态mp4播放 但支持识别 因为播放器控件不允许静态文件播放

![modelBund](./modelBund.jpg)
### 图片识别 
>支持第三方接口传递 识别耗时基本在1秒以内除特殊复杂问题之外 耗时单位目前为ms

![modelBund](./start.gif)

###  视频识别 
>支持第三方接口传递 开启后一直识别不会中断 子线程cpu奔跑需要一定内存量 

![modelBund](./startplay.gif)

### 第三方接入
>报警消息无缝接入接口对接无需集成 支持报警消息、图片、视频流接入

![dingyue](./dingyue.jpg)
![api](./api.jpg)

> 目前已支持的物品识别设置中文翻译以及边框颜色 （持续更新如果你们有特别想要模型的可以联系我1552138571@qq.com 或 qq:1552138571）

## - 自主智能聊天、ChatGpt
### chatGPT
> 支持exel、txt等文本语言模型；场景化特定训练 

![dingyue](./chatplay.gif)


## - 轻量级内核轻训练 

> 这个轻量级主要是使用的easyui内核  [<span style="color: red;">@easyAi轻量级内核地址</span>](https://gitee.com/dromara/easyAi) 他们有自己的内容训练内存消耗少训练时间短面对特定识别内容效率很高
> 由于时间有限暂未完全接入需要的同学可自主继续接入 

### 图片模型
![modelBund](./piclist.jpg)
### 训练结果
![modelBund](./list.jpg)
### 训练任务
![modelBund](./xunlian.jpg)

## - 轻量级智能聊天

> 这个轻量级主要是使用的easyui内核  [<span style="color: red;">@easyAi轻量级内核地址</span>](https://gitee.com/dromara/easyAi) 他们有自己的内容训练内存消耗少训练时间短面对特定识别内容效率很高
> 由于时间有限暂未完全接入需要的同学可自主继续接入 
### 基础分类
![modelBund](./jichufenlei.jpg)
### 语意分类
![modelBund](./yuyifenlei.jpg)
### 语句分类
![modelBund](./yujufenlei.jpg)
### 关键词
![modelBund](./guanjianci.jpg)
### 智能的对话
![modelBund](./zhinengduihua.jpg)
### QA问答
![modelBund](./qawenda.jpg)
### 语义模型训练
![modelBund](./yymoxing.jpg)

## 如果该项目对你有用，请赞助一下作者的劳动力支持开源，请作者吃一顿早饭就好！给作者持续更新easyAi引擎的开发，同时封装更多依赖easyAi的常用应用提供动力！为大家低成本部署AI应用添砖加瓦！万谢!好心人！
## 后续更会持续优化视频、图片识别内容

![支付宝支付](./zfb.jpg)![微信支付](./wechat.jpg)
### 微信交流群
## 加微信技术交流群（目前只有微信交流群，）
![加交流群](./chatpic.jpg) 



