package org.jeecg.modules.demo.chat.study;

import org.jeecg.modules.demo.chat.entity.TabMessageTrainModel;
import org.wlld.config.SentenceConfig;

/**
 * @author Administrator
 * @date 2024/4/7 17:54
 */
public class studyAndtrain {

    public static void study(TabMessageTrainModel model){

        BeanMangerOnly beanMangerOnly=new BeanMangerOnly() ;
        SentenceConfig sentenceConfig= beanMangerOnly.getConfig();
        sentenceConfig.setMaxWordLength(Integer.parseInt(model.getUserMessageLenght()));// 语言长度
        sentenceConfig.setTrustPowerTh(model.getModelThreshold());//语义分类可信阈值，范围0-1
        sentenceConfig.setSentenceTrustPowerTh(Double.parseDouble(model.getModelTypeThreshold()));//生成语句可信阈值
        sentenceConfig.setMaxAnswerLength(Integer.parseInt(model.getModelMessageLenght()));//回复语句的最长长度
        sentenceConfig.setTimes(Integer.parseInt(model.getModelEnhancement()));//qa模型训练增强   1-10 数据量越大设置小  长度*长度*时间
        sentenceConfig.setParam(Double.parseDouble(model.getInhibitionCoefficient()));//正则抑制系数
        sentenceConfig.setWordVectorDimension(Integer.parseInt(model.getModelMessageInlay()));  //越大越好 速度越慢 词向量维度
        sentenceConfig.setQaWordVectorDimension(Integer.parseInt(model.getModelBackInlay())); //qa向量维度





    }
}
