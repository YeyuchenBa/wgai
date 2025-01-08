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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @Description: 模型预训练
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
@Slf4j
@Service
public class TabModelTryServiceImpl extends ServiceImpl<TabModelTryMapper, TabModelTry> implements ITabModelTryService {

    @Autowired
    TabModelTryOrgMapper tabModelTryOrgMapper;
    @Autowired
    TabEasyPicMapper tabEasyPicMapper;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Override
    public Result<String> savePatch(TabModelTry tabModelTry) {
        String fileName = tabModelTry.getPicUrl();
        log.info("当前文件名称{}",fileName);
        if(!"zip".equals(fileName.substring(fileName.lastIndexOf(".")+1))){ //判断后缀是否是zip
            return  Result.error("当前文件压缩为不是ZIP 请上传ZIP压缩包");
        }
        List<String> list=unzipFiles(upLoadPath+"/"+fileName, upLoadPath);
        String id = UUID.randomUUID().toString().replace("-", "");
        if(StringUtils.isNotEmpty(tabModelTry.getId())){
            id=tabModelTry.getId();
            if(tabModelTry.getIsInsert().equals("Y")){//当前覆盖所有图片重新添加
            List<TabModelTryOrg> tabModelTryOrgList=tabModelTryOrgMapper.selectList(new QueryWrapper<TabModelTryOrg>().eq("model_id",tabModelTry.getId()));
                if(tabModelTryOrgList.size()>0){
                    tabModelTryOrgMapper.deleteBatchIds(tabModelTryOrgList.stream().map(TabModelTryOrg::getId).collect(Collectors.toList()));
                    tabEasyPicMapper.deleteBatchIds(tabModelTryOrgList.stream().map(TabModelTryOrg::getPicId).collect(Collectors.toList()));
                }
                tabModelTry.setPicNumber(list.size()+"");
            }else{
                tabModelTry.setPicNumber(Integer.parseInt(tabModelTry.getPicNumber())+list.size()+"");
            }
            this.updateById(tabModelTry);
        }else{
            tabModelTry.setPicNumber(list.size()+"");
            this.save(tabModelTry);
        }


        for (String url:list) {
            String picid = UUID.randomUUID().toString().replace("-", "");
            TabModelTryOrg modelTryOrg=new TabModelTryOrg();
            modelTryOrg.setModelId(id);
            modelTryOrg.setPicId(picid);
            TabEasyPic pic=new TabEasyPic();
            pic.setId(picid);
            pic.setPicUrl(url);
            pic.setMarkType("N");
            pic.setPicType("1");
            pic.setPicName(tabModelTry.getPicName());
            tabEasyPicMapper.insert(pic);
            tabModelTryOrgMapper.insert(modelTryOrg);

        }
        return Result.OK("插入成功");
    }

    //解压压缩包文件并保存文件
    public static List<String> unzipFiles(String zipFilePath, String destDir) {
        List<String> list=new ArrayList<>();
        File dir = new File(destDir);

        // 创建输出目录如果它不存在
        if (!dir.exists()) dir.mkdirs();

        byte[] buffer = new byte[1024];
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

                    // 检查是否为图片文件，如果是则进行处理
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                        // 处理图片文件的代码，例如存储到数据库或做其他操作
                        // 这里只是简单打印文件名作为示例
                        System.out.println("Image file stored: " + destDir + "/" + fileName);
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

        return list;
    }

    public static void main(String[] args) {
        String zipFilePath = "D:\\opt\\upFiles\\temp\\test_1734439662236.zip";
        String destDir = "D:\\opt\\upFiles\\temp\\test";

        unzipFiles(zipFilePath, destDir);
    }

}
