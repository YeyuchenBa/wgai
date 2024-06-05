package org.jeecg.modules.demo.easy.study;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.jeecg.modules.demo.easy.entity.TabEasyConfig;
import org.jeecg.modules.demo.easy.entity.TabEasyPic;
import org.wlld.config.Config;
import org.wlld.distinguish.Distinguish;
import org.wlld.entity.FoodPicture;
import org.wlld.entity.PicturePosition;
import org.wlld.tools.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2024/3/28 15:36
 */
public class studyPic  implements Runnable {

    public static TabEasyConfig tabEasyConfig;
    public static List<TabEasyPic> picList;
    public studyPic(TabEasyConfig tabEasyConfig,List<TabEasyPic> picList){
            this.tabEasyConfig=tabEasyConfig;
            this.picList=picList;
    }

    @SneakyThrows
    @Override
    public void run() {
        Picture picture = new Picture();//图片解析类
        Config config = new Config();//配置文件类;
        config.setTypeNub(tabEasyConfig.getTypeNum());//设置训练种类数
        config.setBoxSize(tabEasyConfig.getBoxSize());//设置物体大致大小 单位像素 即 125*125 的矩形
        config.setPictureNumber(tabEasyConfig.getPicNum());//设置每个种类训练图片数量 某个类别有几张照片，注意所有种类照片数量要保持一致
        config.setPth(tabEasyConfig.getPth());//设置可信概率，只有超过可信概率阈值，得出的结果才是可信的 数值为0-1之间
        config.setShowLog(true);//输出学习时打印数据
        Distinguish distinguish = new Distinguish(config);//创建识别类
        distinguish.setBackGround(picture.getThreeMatrix(tabEasyConfig.getBackUrl()));//设置识别的背景图片(该api为固定背景)
        List<FoodPicture> foodPictures = new ArrayList<>();//创建训练模板集合
        for (int i = 1; i < tabEasyConfig.getTypeNum(); i++) { //循环实物 次数
            FoodPicture foodPicture = new FoodPicture();//创建每一类图片的训练模板类
            foodPictures.add(foodPicture);//将该类模板加入集合
            List<PicturePosition> picturePositionList = new ArrayList<>();//创建该类模板的训练集合类
            foodPicture.setId(i + 1);//设置该图片类别id
            foodPicture.setPicturePositionList(picturePositionList);
            for (int j = 1; j < picList.size(); j++) {//训练图片数量为 每种五张 注意跟config 中的 pictureNumber 要一致

                PicturePosition picturePosition = new PicturePosition();
                picturePosition.setUrl(picList.get(j).getPicUrl());//加载该类别图片地址
                picturePosition.setNeedCut(false);//是否需要剪切，若训练素材为充满全图图片，则充满全图不需要剪切 写false
                picturePositionList.add(picturePosition);//加载
            }
        }
        distinguish.studyImage(foodPictures);//进行学习 耗时较长
        System.out.println(JSON.toJSONString(distinguish.getModel()));//输出模型保存,将模型实体类序列化为json保存
    }
}
