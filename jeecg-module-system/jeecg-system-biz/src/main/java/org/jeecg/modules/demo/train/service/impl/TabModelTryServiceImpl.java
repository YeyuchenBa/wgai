package org.jeecg.modules.demo.train.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.easy.entity.TabEasyPic;
import org.jeecg.modules.demo.easy.mapper.TabEasyPicMapper;
import org.jeecg.modules.demo.train.entity.TabModelTry;
import org.jeecg.modules.demo.train.entity.TabModelTryOrg;
import org.jeecg.modules.demo.train.mapper.TabModelTryMapper;
import org.jeecg.modules.demo.train.mapper.TabModelTryOrgMapper;
import org.jeecg.modules.demo.train.service.ITabModelTryService;
import org.jeecg.modules.demo.train.util.picXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.jeecg.modules.demo.train.util.writeXml.writeXml;

/**
 * @Description: 模型预训练
 * @Author: WGAI
 * @Date: 2024-12-17
 * @Version: V1.0
 */
@Slf4j
@Service
public class TabModelTryServiceImpl extends ServiceImpl<TabModelTryMapper, TabModelTry> implements ITabModelTryService {

    @Autowired
    TabModelTryOrgMapper tabModelTryOrgMapper;
    @Autowired
    TabEasyPicMapper tabEasyPicMapper;
    @Autowired
    TabModelTryMapper tabModelTryMapper;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    //解压压缩包文件并保存文件
    public static Map<String,Object> unzipFiles(String zipFilePath, String destDir) {
        Map<String,Object> map=new HashMap<>();
        List<String> list = new ArrayList<>();
        File dir = new File(destDir);

        // 创建输出目录如果它不存在
        if (!dir.exists()) dir.mkdirs();

        byte[] buffer = new byte[1024];
        long totalSize = 0;  // 用于存储解压文件的总大小，单位是字节
        try {
            FileInputStream fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();

                // 如果这是一个文件夹，就创建它
                if (ze.isDirectory()) {
                    File fmd = new File(destDir + "/" + fileName);
                    fmd.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(destDir + "/" + fileName);
                    BufferedOutputStream stream = new BufferedOutputStream(fos);
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        stream.write(buffer, 0, len);
                    }
                    stream.close();
                    fos.close();
                    // 累加文件大小
                    totalSize += ze.getSize();
                    // 检查是否为图片文件，如果是则进行处理
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                        // 处理图片文件的代码，例如存储到数据库或做其他操作
                        // 这里只是简单打印文件名作为示例
                        log.info("Image file stored: " + destDir + "/" + fileName);
                        list.add(fileName);
                    }
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将文件大小转换为 MB
        double totalSizeInMB = totalSize / (1024.0 * 1024.0);
        log.info("当前文件大小 " + totalSizeInMB + " MB");
        map.put("list",list);
        map.put("size",totalSizeInMB);
        return map;
    }

    /***
     *针对文件重新编码按照顺序后续训练更便捷
     * @param path 原文件地址
     * @param flag true 覆盖 不覆盖
     * @param folder 目标文件目录
     * @return
     */
    public static List<String> changeFileName(List<String> path,boolean flag,String folder,String upLoadPath,String picfolader) {
        if(flag){ //从0 开始
            deleteAllFilesInFolder(new File(folder));
        }

        List<String> renamePic = new ArrayList<String>();
    try {



        File newDir=new File(folder);
        if(!newDir.exists()){
            if (newDir.mkdirs()) {
                log.info("新目录创建成功！");
            } else {
                log.error("新目录创建失败！");
            }
        }

        for (String pathStr : path) {
            String newFileName=sendPicNameNo( flag,pathStr, folder);
            File newFile = new File(newDir, newFileName); //移动到新目录
            File oldFile = new File(upLoadPath+File.separator+pathStr); //移动到新目录
            if (oldFile.renameTo(newFile)) {
                log.info("文件成功移动并重命名！");
            } else {
                log.info("文件移动或重命名失败！");
            }
            //移动后文件
            File Change=new File(folder+File.separator+newFileName);
            //图片统一存储到700*700
            BufferedImage inputImage = ImageIO.read(Change);

            // 创建新的空白图片，大小为 800x800
            BufferedImage resizedImage = new BufferedImage(700, 700, inputImage.getType());

            // 使用 Graphics2D 来缩放图片
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, 700, 700, null);
            g2d.dispose();

            // 保存修改后的图片
            ImageIO.write(resizedImage, "png", Change);
            log.info("图片已成功保存为 700*700 像素");

            renamePic.add(newFileName);

        }
    }catch (Exception ex){
            ex.printStackTrace();
    }
        return renamePic;
    }


    /***
     * 文件生成序号
     * @param flag
     * @param picName
     * @return
     */
    public static String sendPicNameNo(boolean flag, String picName, String folder){


       int number=getFileNum(folder);
        String paddedNumber = String.format("%06d", number)+".png";
       return paddedNumber;
    }

    // 递归删除文件夹中的所有文件和子文件夹
    public static void deleteAllFilesInFolder(File folder) {
        if (folder.isDirectory()) {
            // 获取文件夹下的所有文件和子文件夹
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 如果是文件，直接删除
                    if (file.isFile()) {
                        if (file.delete()) {
                            log.info("文件 " + file.getName() + " 删除成功！");
                        } else {
                            log.info("文件 " + file.getName() + " 删除失败！");
                        }
                    } else {
                        // 如果是文件夹，递归调用删除方法
                        deleteAllFilesInFolder(file);
                    }
                }
            }else{
                log.info("当前文件夹空空如也");
        }
    }
}
    //获取文件下文件个数
    public static int getFileNum(String folderPath){
        File folder = new File(folderPath);  // 替换为你的文件夹路径
        // 获取文件夹中所有文件和子文件夹
        File[] files = folder.listFiles();
        if (files != null) {
            int fileCount = 1;
            // 遍历文件夹中的文件和子文件夹
            for (File file : files) {
                // 只统计文件，不统计子文件夹
                if (file.isFile()) {
                    fileCount++;
                }
            }
            return fileCount;
        } else {
            return 1;
        }
    }
    public static void main(String[] args) {
        String zipFilePath = "D:\\opt\\upFiles\\temp\\test_1734439662236.zip";
        String destDir = "D:\\opt\\upFiles\\temp\\test";

        unzipFiles(zipFilePath, destDir);
    }

    @Override
    public Result<String> savePatch(TabModelTry tabModelTry) {
        try {


            String fileName = tabModelTry.getPicUrl();
            log.info("当前文件名称{}", fileName);
            if (!"zip".equals(fileName.substring(fileName.lastIndexOf(".") + 1))) { //判断后缀是否是zip
                return Result.error("当前文件压缩为不是ZIP 请上传ZIP压缩包");
            }
            //解压压缩包放置
            Map<String,Object> map=unzipFiles(upLoadPath + "/" + fileName, upLoadPath);
            List<String> list = (List<String>) map.get("list");
            Double  PicSize = (Double) map.get("totalSizeInMB");
            String id = UUID.randomUUID().toString().replace("-", "");
            if (StringUtils.isNotEmpty(tabModelTry.getId())) {
                id = tabModelTry.getId();
                if (tabModelTry.getIsInsert().equals("Y")) {//当前覆盖所有图片重新添加
                    List<TabModelTryOrg> tabModelTryOrgList = tabModelTryOrgMapper.selectList(new QueryWrapper<TabModelTryOrg>().eq("model_id", tabModelTry.getId()));
                    if (tabModelTryOrgList.size() > 0) {
                        tabModelTryOrgMapper.deleteBatchIds(tabModelTryOrgList.stream().map(TabModelTryOrg::getId).collect(Collectors.toList()));
                        tabEasyPicMapper.deleteBatchIds(tabModelTryOrgList.stream().map(TabModelTryOrg::getPicId).collect(Collectors.toList()));
                    }
                    tabModelTry.setPicNumber(list.size() + "");
                    tabModelTry.setFileSize(PicSize);
                } else {
                    tabModelTry.setPicNumber(Integer.parseInt(tabModelTry.getPicNumber()) + list.size() + "");
                    tabModelTry.setFileSize(tabModelTry.getFileSize()+PicSize);
                }
                this.updateById(tabModelTry);
            } else {
                tabModelTry.setPicNumber(list.size() + "");
                this.save(tabModelTry);
            }

            //文件处理
            List<String> changeFile=changeFileName(list,tabModelTry.getIsInsert().equals("Y"),upLoadPath + "/"+tabModelTry.getPicName(),upLoadPath ,tabModelTry.getPicName());
            for (String url : changeFile) {
                String picid = UUID.randomUUID().toString().replace("-", "");
                TabModelTryOrg modelTryOrg = new TabModelTryOrg();
                modelTryOrg.setModelId(id);
                modelTryOrg.setPicId(picid);
                TabEasyPic pic = new TabEasyPic();
                pic.setId(picid);
                pic.setModelId(id);
                pic.setPicUrl(tabModelTry.getPicName()+File.separator+url);
                pic.setMarkType("N");
                pic.setPicType("1");
                pic.setPicName(url);
                pic.setRemake(tabModelTry.getPicName());
                tabEasyPicMapper.insert(pic);
                tabModelTryOrgMapper.insert(modelTryOrg);

            }
            return Result.OK("插入成功");
        } catch (Exception exception) {
            exception.printStackTrace();
            return Result.error("插入失败");
        }
    }

    @Override
    public Result<String> saveMake(List<picXml>  picXmll) {
        try{
            log.info("【当前标注内容大小:{}】",picXmll.size());
            TabEasyPic tabEasyPic=tabEasyPicMapper.selectById(picXmll.get(0).getPicId());  //获取图片
            //开始保存xml文件

            String xmlpath=writeXml(upLoadPath,tabEasyPic, picXmll);

            tabEasyPic.setMarkXml(xmlpath);
            tabEasyPic.setMarkType("Y");
            tabEasyPic.setMarkTitle(picXmll.stream().map(picXml::getName).collect(Collectors.joining(",")));
            tabEasyPicMapper.updateById(tabEasyPic);

            Integer yesMark=tabModelTryMapper.getMakeNum(tabEasyPic.getModelId(),"Y");
            Integer noMark=tabModelTryMapper.getMakeNum(tabEasyPic.getModelId(),"N");
            Integer sumPic=yesMark+noMark;
            TabModelTry tabModelTry=tabModelTryMapper.selectById(tabEasyPic.getModelId());
            tabModelTry.setMakeNumber(yesMark+"");
            tabModelTry.setPicNumber(sumPic+"");
            tabModelTryMapper.updateById(tabModelTry);

        }catch (Exception ex){
            return Result.error("标注保存失败");
        }
   
        return Result.ok("标注保存成功");
    }

}
