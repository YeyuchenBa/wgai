package org.jeecg.modules.demo.train.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.PmsUtil;
import org.jeecg.modules.demo.easy.entity.TabEasyPic;
import org.jeecg.modules.demo.easy.service.ITabEasyPicService;
import org.jeecg.modules.demo.train.entity.TabModelTry;
import org.jeecg.modules.demo.train.entity.TabTrainLog;
import org.jeecg.modules.demo.train.entity.TabTrainPython;
import org.jeecg.modules.demo.train.entity.TabTrainResult;
import org.jeecg.modules.demo.train.mapper.TabTrainPythonMapper;
import org.jeecg.modules.demo.train.service.ITabModelTryService;
import org.jeecg.modules.demo.train.service.ITabTrainLogService;
import org.jeecg.modules.demo.train.service.ITabTrainPythonService;
import org.jeecg.modules.demo.train.service.ITabTrainResultService;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.service.ITabAiModelService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


import static org.jeecg.modules.demo.train.util.markLable.sendLable;

/**
 * @Description: 训练脚本模板
 * @Author: WGAI
 * @Date:   2025-01-14
 * @Version: V1.0
 */
@Slf4j
@Service
public class TabTrainPythonServiceImpl extends ServiceImpl<TabTrainPythonMapper, TabTrainPython> implements ITabTrainPythonService {

    private ITabModelTryService tabModelTryService;
    @Autowired
    private ITabEasyPicService tabEasyPicService;

    private   ITabTrainLogService tabTrainLogService ;
    private  ITabTrainResultService tabTrainResultService;
    private ITabAiModelService iTabAiModelService;
    public static  String yolov5Path="";

    @Autowired
    public TabTrainPythonServiceImpl(ITabAiModelService iTabAiModelService,ITabTrainLogService tabTrainLogService,ITabModelTryService tabModelTryService,ITabTrainResultService tabTrainResultService) {
        this.tabTrainLogService = tabTrainLogService;
        this.tabModelTryService = tabModelTryService;
        this.tabTrainResultService=tabTrainResultService;
        this.iTabAiModelService=iTabAiModelService;
    }




    private static String uploadPath;

    @Value("${jeecg.path.upload}")
    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
    @Override
    public Result<String> startPy(String id,String sort) {
        try {
        if(!checkRunModel( id)){
            log.info("【当前模型任务正在训练中不可重复训练】");
           return Result.error("任务在训练中不可重复训练");
        }
        QueryWrapper<TabTrainPython> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotEmpty(sort)){
            queryWrapper.eq("py_sort",Integer.parseInt(sort));
        }
        queryWrapper.orderByAsc("py_sort");
        List<TabTrainPython> list=this.list(queryWrapper);
        TabTrainPython tabTrainPython7=new TabTrainPython();
        for (TabTrainPython tabTrainPython:list){
            switch (tabTrainPython.getPySort()){
                case 1:
                    //执行创建文件夹脚本
                    createFolderStructure(tabTrainPython.getPyPath());
                    //将标注好的xml 图片放到指定 文件夹
                    QueryWrapper<TabEasyPic> queryWrapperPc=new QueryWrapper<>();
                    queryWrapperPc.eq("model_id",id);
                    List<TabEasyPic> tabEasyPicList=tabEasyPicService.list(queryWrapperPc);
                    copyFile(tabEasyPicList, tabTrainPython);
                    break;
                case 2:
                    // 训练、验证和测试集，并生成相应的txt
                    sendTxt(tabTrainPython.getPyPath());
                    break;
                case 3:
                    // xml转中心坐标点并生成数据源
                    sendLable(tabTrainPython, tabModelTryService.getById(id));
                    break;
                case 4:
                    //生成coco配置文件
                    sendCocoYaml( tabTrainPython,tabModelTryService.getById(id));
                    break;
                case 5:
                    //修改原版 80 的nc 根据实际情况修改 nc
                    sendYolov5s( tabTrainPython,tabModelTryService.getById(id));
                    break;
                case 6:
                    //修改训练配置文件yaml
                    copyYolov5Train(tabTrainPython);
                    tabTrainPython7=tabTrainPython;
                    break;

            }
            try {
                Thread.sleep(1000);
                log.info("【当前执行{}停顿1s后继续执行】",tabTrainPython.getPySort());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        //配置文件配置完成开始训练 //丢给子线程去执行
            if(StringUtils.isNotEmpty(tabTrainPython7.getPyPath())) {

                yolov5Path=tabTrainPython7.getPyPath();
                //开始执行训练
                startTrain(tabTrainPython7,id);
            }else{
                return Result.error("执行失败");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return Result.error("执行失败");
        }
        return Result.ok("正在执行训练");
    }


    /***
     * 检查运行的任务是否正常
     * @return
     */
    public boolean  checkRunModel(String id){


        TabModelTry tabModelTry=tabModelTryService.getById(id);
        //刚开始进入检查是否运行了  0未运行 1运行
        if(tabModelTry.getRunState()!=null&&tabModelTry.getRunState()!=0){
            return  false; //已经在运行不可重复运行
        }else{
            tabModelTry.setRunState(1);
            tabModelTry.setOnnxIsok(0);
            tabModelTry.setRunDateStart(new Date());
            tabModelTryService.updateById(tabModelTry);
            return true;
        }


    }

    public  void startTrain( TabTrainPython tabTrainPython7,String id){
         String  path=tabTrainPython7.getPyPath();//yolov5根目录

        Thread trainingThread = new Thread(() -> {
            StringBuffer stringBuffer=new StringBuffer();
            String cmdTxt="未找到";
            String cmdPath="未找到";
            String cmdEpchs="未找到";
           try {

               log.info("【第7步】进入yolov5根目录{}"," Hello  "+path );
               executePythonScript(new String[]{"/bin/bash", "-c","echo 'Hello, World!'"});
               log.info("【当你在日志中看到了hello world 说明你已经成功90%了】");
                //executePythonScript(new String[]{"/bin/bash", "-c","cd "+path+" && source myenv/bin/activate && python3 wgtrain.py"});
               // Step 1: 进入 YOLOv5 根目录
                log.info("【第7步】进入yolov5根目录{}"," cd  "+path );
                // Step 2: 进入虚拟环境
                log.info("【第7步】进入虚拟环境");
                // Step 3: 执行 YOLOv5 训练脚本
                log.info("【第7步】执行训练脚本");

                log.info("训练完成，退出代码: " );
               try {
                   ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"/bin/bash", "-c","cd "+path+" && source myenv/bin/activate && python3 wgtrain.py"});
                   processBuilder.redirectErrorStream(true);  // 合并错误流到标准输出流

                   Process process = processBuilder.start();
                   log.info("执行脚本内容");
                   // 捕获标准输出流
                   BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                   String line;

                   while ((line = reader.readLine()) != null) {
                       log.info(line);  // 打印日志到控制台
                       stringBuffer.append(line+"\n");

                       String textCmd=getLogImportant(line);
                       if(!textCmd.equals("未找到")) {
                           cmdTxt=textCmd;
                       }
                       String pathcmd=getLogPath(line);
                       if(!pathcmd.equals("未找到")) {
                            cmdPath=pathcmd;
                       }
                       String epochscmd=getLogEpochs(line);
                       if(!epochscmd.equals("未找到")) {
                           cmdEpchs=epochscmd;
                       }
                   }

                   // 等待进程完成
                   int exitCode = process.waitFor();
                  log.info("Python script executed with exit code: " + exitCode);
               } catch (IOException | InterruptedException e) {
                   e.printStackTrace();
               }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
               log.info("【不管结果如何都要去保存日志并修正训练标记】");
               if(StringUtils.isNotEmpty(cmdPath)){  //转换pt为onnx文件
                   try {
                       ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"/bin/bash", "-c","cd "+path+" && source myenv/bin/activate &&  python export.py --weights "+cmdPath+"/weights/best.pt --img 640 --batch 1 --device 0 --include onnx --simplify --opset 11"});
                       processBuilder.redirectErrorStream(true);  // 合并错误流到标准输出流

                       Process process = processBuilder.start();
                       log.info("执行转换脚本内容");
                       // 捕获标准输出流
                       BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                       String line;

                       while ((line = reader.readLine()) != null) {
                           log.info(line);  // 打印日志到控制台
                           stringBuffer.append(line+"\n");

                       }

                       // 等待进程完成
                       int exitCode = process.waitFor();
                       log.info("Python pt to onnx 完成 " + exitCode);
                   }catch (Exception ex){
                       ex.printStackTrace();
                       log.error("转换失败");
                   }

               }
               saveLog(stringBuffer,id,cmdTxt,cmdPath, cmdEpchs);

           }
        });

        // 启动子线程
        trainingThread.start();

        // 主线程可以继续做其他事情
       log.info("子线程已启动，正在进行 YOLOv5 训练...");
    }

    /***
     * 训练结束保存日志
     */
    public  void saveLog( StringBuffer stringBuffer,String id,String text,String path,String epochscmd){
        try {
            /**
             * 先移除 后保存
             */
            QueryWrapper<TabTrainLog> tabTrainLogQueryWrapper=new QueryWrapper<>();
            tabTrainLogQueryWrapper.eq("model_id",id);
            tabTrainLogService.remove(tabTrainLogQueryWrapper);

            QueryWrapper<TabTrainResult> tabTrainResultQueryWrapper=new QueryWrapper<>();
            tabTrainResultQueryWrapper.eq("model_id",id);
            tabTrainResultService.remove(tabTrainResultQueryWrapper);

            TabTrainLog tabTrainLog=new TabTrainLog();
            tabTrainLog.setModelId(id);
            tabTrainLog.setTrainLog(stringBuffer.toString());
            tabTrainLog.setCmdText(text);
            tabTrainLog.setCmdPath(path);
            tabTrainLogService.save(tabTrainLog);

            /***
             * 保存内容
             */
            TabModelTry modelTry=tabModelTryService.getById(id);
            modelTry.setRunState(0);//运行结束
            modelTry.setRunDateEnd(new Date());

            if(StringUtils.isNotEmpty(path)){ //当前不为空说明运行正常
                //读取当前文件下的数据copy到目录
                modelTry.setOnnxIsok(1);
                String targetPaht=uploadPath+File.separator+modelTry.getPicName();
                String targetPath=modelTry.getPicName();
                TabTrainResult tabTrainResult=new TabTrainResult();
                if(copyYolov5Onnx(path, targetPaht)){
                    modelTry.setModelOnnx(targetPath+"/weights/best.onnx");
                    tabTrainResult.setModelId(id);
                    tabTrainResult.setStartTime(modelTry.getRunDateStart());
                    tabTrainResult.setEndTime(modelTry.getRunDateEnd());
                    tabTrainResult.setLabels(targetPath+"/labels.jpg"); //
                    tabTrainResult.setLabelsCorrelogram(targetPath+"/labels_correlogram.jpg");
                    tabTrainResult.setTrainBatch0(targetPath+"/train_batch0.jpg");
                    tabTrainResult.setTrainBatch1(targetPath+"/train_batch1.jpg");
                    tabTrainResult.setTrainBatch2(targetPath+"/train_batch2.jpg");
                    tabTrainResult.setValBatch0Lables(targetPath+"/val_batch0_labels.jpg");
                    tabTrainResult.setValBatch0Pred(targetPath+"/val_batch0_pred.jpg");
                    tabTrainResult.setConfusionMatrix(targetPath+"/confusion_matrix.png");
                    tabTrainResult.setF1Curve(targetPath+"/F1_curve.png");
                    tabTrainResult.setPpCurve(targetPath+"/P_curve.png");
                    tabTrainResult.setPrCurve(targetPath+"/PR_curve.png");
                    tabTrainResult.setRrCurve(targetPath+"/R_curve.png");
                    tabTrainResult.setResults(targetPath+"/results.png");
                    tabTrainResult.setHypYaml(targetPath+"/hyp.yaml");
                    tabTrainResult.setOptYaml(targetPath+"/opt.yaml");
                    tabTrainResult.setBestPt(targetPath+"/weights/best.pt");
                    tabTrainResult.setLastPt(targetPath+"/weights/last.pt");
                    tabTrainResult.setEpochs(epochscmd);
                    tabTrainResult.setTrainImages(modelTry.getPicNumber());
                    tabTrainResult.setTrainState("1");
                    tabTrainResult.setOnnxWeight(targetPath+"/weights/best.onnx");
                    if(StringUtils.isNotEmpty(text)){
                        String [] classtxt=text.split(" ");
                        int i=0;
                        for (String classt:classtxt) {
                            if(StringUtils.isNotEmpty(classt)){
                                if(i==0){
                                    tabTrainResult.setTrainClass(classt);
                                }else if(i==3){
                                    tabTrainResult.setPercision(classt);
                                }else if(i==4){
                                    tabTrainResult.setRecall(classt);
                                }else if(i==5) {
                                    tabTrainResult.setMap50(classt);
                                }else if(i==6) {
                                    tabTrainResult.setMap5095(classt);
                                }
                                i++;
                            }
                        }
                    }

                    tabTrainResultService.save(tabTrainResult);
                    //更新模型库
                    QueryWrapper<TabAiModel> tabAiModelQueryWrapper=new QueryWrapper<>();
                    tabAiModelQueryWrapper.eq("spare_five",id);
                    TabAiModel tabAiModel=iTabAiModelService.getOne(tabAiModelQueryWrapper);
                    if(tabAiModel==null){//报错
                        tabAiModel=new TabAiModel();
                        tabAiModel.setAiName(modelTry.getModelName());
                        tabAiModel.setAiWeights(modelTry.getModelOnnx());
                        tabAiModel.setSpareOne("2");
                        tabAiModel.setAiNameName( setToTxtClass(uploadPath,targetPath+"/className.txt",modelTry.getTxtInfo()));
                        iTabAiModelService.save(tabAiModel);
                    }else{
                        tabAiModel.setAiName(modelTry.getModelName());
                        tabAiModel.setAiWeights(modelTry.getModelOnnx());
                        tabAiModel.setSpareOne("2");
                        tabAiModel.setAiNameName( setToTxtClass(uploadPath,targetPath+"/className.txt",modelTry.getTxtInfo()));
                        iTabAiModelService.updateById(tabAiModel);
                    }

                }

            }else{
                log.error("【训练任务执行失败请检查日志】");
            }
            tabModelTryService.updateById(modelTry);

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * 把识别分组写入到txt
     * @param path
     * @param className
     */
    public static String setToTxtClass(String uploadPath,String path,String className){
        String [] str=className.split(",");
        File file = new File(uploadPath+File.separator+path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // 遍历分割后的数组，每个元素写入一行
            for (String part : str) {
                writer.write(part);  // 写入当前元素
                writer.newLine();     // 换行
            }
            log.info("【写入class成功路径：{}】",path);
            log.info("【写入class成功完整版路径：{}】", uploadPath+File.separator+path);

            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    //复制结果文件到目录内
    public static boolean copyYolov5Onnx(String path,String targetPaths){
        Path sourcePath = Paths.get(path);  // 源文件路径
        Path targetPath = Paths.get(targetPaths);  // 目标文件路径

        try {
            // 拷贝文件并覆盖目标文件（如果存在）
            copyDirectory(sourcePath, targetPath);
            log.info("文件已成功拷贝并覆盖");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件拷贝失败: " + e.getMessage());
            return false;
        }
    }

    // 递归拷贝目录及其中的所有文件
    public static void copyDirectory(Path source, Path target) throws IOException {
        // 如果目标目录不存在，则创建
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        // 遍历源目录下的所有文件和子目录
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 计算目标文件的路径
                Path targetFile = target.resolve(source.relativize(file));
                // 拷贝文件到目标目录
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // 为每个子目录创建对应的目标目录
                Path targetDir = target.resolve(source.relativize(dir));
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
    /***
     * 分解训练日志内容1
     * @param cmd
     */
    public String getLogImportant(String cmd){

        String inputcmd=cmd.trim(); //先去首尾空格
        if(inputcmd.startsWith("all")){
            log.info("【cmd的结果集数据】{}",inputcmd);
            return inputcmd;
        }

        return "未找到";
    }


    /***
     * 分解训练日志内容2
     * @param cmd
     */
    public String getLogPath(String cmd){

        String inputcmd=cmd.trim(); //先去首尾空格


        if(inputcmd.indexOf("save_dir")>-1){

            log.info("【Path的结果集数据】{}",inputcmd);
            int start=inputcmd.lastIndexOf(":");
            return yolov5Path+File.separator+inputcmd.substring(start+1).replaceAll(" ","");
        }

        return "未找到";
    }
    public String getLogEpochs(String cmd){

        String inputcmd=cmd.trim(); //先去首尾空格


        if(inputcmd.indexOf("epochs")>-1){
            if(inputcmd.indexOf("warmup_epochs")>-1){
                return "未找到";
            }
            log.info("【Epochs的结果集数据】{}",inputcmd);
            int start=inputcmd.lastIndexOf(":");
            return inputcmd.substring(start+1).replaceAll(" ","");
        }

        return "未找到";
    }
    public static void main(String[] args) {

    }

    // 捕捉进程输出的辅助方法
    private static void executePythonScript(String[] cmd) {
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
//        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), StandardCharsets.UTF_8))) {
//            writer.write(jsonData.toJSONString());
//            writer.newLine();
//            writer.flush();
//        }
            log.info("当前执行命令{}",cmd);

            // Read and print the command output
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String s;
            while ((s = stdInput.readLine()) != null) {
               log.info(s);  // Standard output
            }
            while ((s = stdError.readLine()) != null) {
                log.error(s);  // Standard error
            }

            int exitVal = proc.waitFor();
              log.info("Process exit value: " + exitVal);



        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    //复制覆盖文件
    public static void copyYolov5Train(TabTrainPython tabTrainPython){
        Path sourcePath = Paths.get(uploadPath+File.separator+tabTrainPython.getPyUrl());  // 源文件路径
        Path targetPath = Paths.get(tabTrainPython.getPyPath()+"/wgtrain.py");  // 目标文件路径

        try {
            // 拷贝文件并覆盖目标文件（如果存在）
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("文件已成功拷贝并覆盖");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("文件拷贝失败: " + e.getMessage());
        }
    }

    /**
     * 修改cfg文件
     * @param tabTrainPython
     * @param tabModelTry
     */
    public static void sendYolov5s(TabTrainPython tabTrainPython,TabModelTry tabModelTry) {
        // 原始文件路径
        String filePath = uploadPath+File.separator+tabTrainPython.getPyUrl();
        String nc=tabModelTry.getTxtInfo();
        String [] c=nc.split(",");
        // 新文件保存路径
        String newFilePath = tabTrainPython.getPyPath()+"/wgai.yaml";    // 替换为你要保存的新文件路径
        String keyword = "nc:";                             // 你要查找的关键字
        String replacementLine = "nc: "+c.length;              // 新的行内容

        try {
            // 读取文件内容
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            // 遍历文件行，查找并修改包含关键字的行
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains(keyword)) {
                    // 找到关键字，替换整行
                    lines.set(i, replacementLine);
                }
            }

            // 将修改后的内容保存到新的文件
            Files.write(Paths.get(newFilePath), lines);

            System.out.println("文件修改并保存成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 保存指定coco文件目录
     * @param tabTrainPython
     */
    public  static void sendCocoYaml(TabTrainPython tabTrainPython,TabModelTry tabModelTry){
        // 定义文件路径
        String basePath = tabTrainPython.getPyPath()+"/data";
        String trainPath = basePath + "/train.txt";
        String valPath = basePath + "/val.txt";
        String testPath = basePath + "/test.txt";
        String configPath = tabTrainPython.getPyPath() + "/config.yaml";
        String nc=tabModelTry.getTxtInfo();
        String [] c=nc.split(",");
        String d="";
        for (String b:c) {
            d += "'" +b + "'," ;
        }
        // 创建数据目录
        File dataDir = new File(basePath);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // 如果目录不存在，创建目录
        }

        // 需要写入文件的内容
        String path = "path: " + basePath;
        String trainContent = "train: " + trainPath;
        String valContent = "val: " + valPath;
        String testContent = "test: " + testPath;

        String configContent = path+"\n"+trainContent+"\n"+valContent+"\n"+testContent+"\n"+"nc: "+c.length+"\n" + "# Classes\n" + "names: ["+d.substring(0,d.length()-1)+"]";

        // 写入文件
        try {

            // 写入 config
            writeToFile(configPath, configContent);

            log.info("【{}/config.yaml文件生成成功:{}】",basePath,configPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 写入文件的辅助方法
    private static void writeToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);

        // 如果文件不存在，创建文件
        if (!file.exists()) {
            file.createNewFile();
        }

        // 使用 BufferedWriter 写入内容
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            writer.newLine();
        }
    }
    /***
     * 训练、验证和测试集，并生成相应的txt
     * @param path2
     */
    public static void sendTxt(String path2 ){
        // 设置相关参数
        double trainvalPercent = 0.1;
        double trainPercent = 0.9;
        String xmlFilePath = path2+"/data/Annotations";  // XML 文件所在目录
        String txtSavePath = path2+"/data/ImageSets";  // 保存文件列表的路径

        log.info("【XML 文件所在目录{}】",xmlFilePath);
        log.info("【TXT 保存文件列表的路径{}】",txtSavePath);
        // 获取所有 XML 文件
        File xmlDir = new File(xmlFilePath);
        File[] totalXmlFiles = xmlDir.listFiles((dir, name) -> name.endsWith(".xml"));

        if (totalXmlFiles == null || totalXmlFiles.length == 0) {
            log.info("【未找到当前文件夹】 " + xmlFilePath);
            return;
        }

        int num = totalXmlFiles.length;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(i);
        }

        // 计算训练、验证和测试集的数量
        int tv = (int) (num * trainvalPercent);  // trainval 的数量
        int tr = (int) (tv * trainPercent);     // train 的数量

        // 随机选择 trainval 和 train 数据集
        Collections.shuffle(list, new Random());
        List<Integer> trainval = list.subList(0, tv);
        List<Integer> train = trainval.subList(0, tr);

        // 创建目录
        File savePath = new File(txtSavePath);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }

        // 创建输出文件
        File trainvalFile = new File(txtSavePath, "trainval.txt");
        File testFile = new File(txtSavePath, "test.txt");
        File trainFile = new File(txtSavePath, "train.txt");
        File valFile = new File(txtSavePath, "val.txt");
        log.info("【trainvalFile 文件所在目录{}】",trainvalFile.getPath());
        log.info("【testFile 文件所在目录{}】",testFile.getPath());
        log.info("【trainFile 文件所在目录{}】",trainFile.getPath());
        log.info("【valFile 文件所在目录{}】",valFile.getPath());
        // 使用 BufferedWriter 来写入文件
        try (BufferedWriter ftrainval = new BufferedWriter(new FileWriter(trainvalFile));
             BufferedWriter ftest = new BufferedWriter(new FileWriter(testFile));
             BufferedWriter ftrain = new BufferedWriter(new FileWriter(trainFile));
             BufferedWriter fval = new BufferedWriter(new FileWriter(valFile))) {

            // 遍历所有 XML 文件，按照划分规则写入到相应的 .txt 文件
            for (int i = 0; i < num; i++) {
                String name = totalXmlFiles[i].getName().substring(0, totalXmlFiles[i].getName().length() - 4); // 去掉扩展名

                if (trainval.contains(i)) {
                    ftrainval.write(name + "\n");
                    if (train.contains(i)) {
                        ftest.write(name + "\n");
                    } else {
                        fval.write(name + "\n");
                    }
                } else {
                    ftrain.write(name + "\n");
                }
            }

           log.info("【文件生成成功】" + txtSavePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /***
     * 创建文件夹
     * @param baseDir
     */
    public static void createFolderStructure(String baseDir) {
        // 在指定目录下创建第一个文件夹
        File baseFolder = new File(baseDir, "data");
        if (!baseFolder.exists()) {
            if (baseFolder.mkdirs()) {
                log.info("【创建成功】 " + baseFolder.getAbsolutePath());
            } else {
                log.info("【创建失败】: " + baseFolder.getAbsolutePath());
                return;
            }
        }else{
            log.info("【文件夹存在清空】");
            clearDirectory(baseFolder);
        }

        // 在 'data' 文件夹下创建四个子文件夹
        String[] subfolders = {"Annotations", "images", "ImageSets", "labels"};
        for (String subfolder : subfolders) {
            File subfolderPath = new File(baseFolder, subfolder);
            if (!subfolderPath.exists()) {
                if (subfolderPath.mkdir()) {
                    log.info("【创建成功】 " + subfolderPath.getAbsolutePath());
                } else {
                    log.info("【创建失败】: " + subfolderPath.getAbsolutePath());
                }
            }
        }
    }


    // 清空文件夹内容
    public static void clearDirectory(File directory) {
        // 获取文件夹中的所有文件和子文件夹
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子文件夹，则递归调用清空方法
                    clearDirectory(file);
                }
                // 删除文件或空文件夹
                file.delete();
            }
        }
    }

    /***
     * 拷贝文件到指定目录
     * @param
     */
    public static void copyFile( List<TabEasyPic> tabEasyPicList,TabTrainPython tabTrainPython)  {

        String xmlPath=tabTrainPython.getPyPath()+File.separator+"data"+File.separator+"Annotations";
        String imgPath=tabTrainPython.getPyPath()+File.separator+"data"+File.separator+"images";
        String filePath=uploadPath+File.separator;
        log.info("【xmlPath：{}】",xmlPath);
        log.info("【imgPath：{}】",imgPath);
        log.info("【filePath：{}】",filePath);
        for (TabEasyPic pic:tabEasyPicList) {

                try {
                    Path sourcePicFile = Paths.get(filePath+pic.getPicUrl());  // 源文件路径
                    Path targetPicDir = Paths.get(imgPath);  // 目标目录路径
                    // 使用 Files.copy() 复制文件，覆盖目标文件
                    Path targetFilePic = targetPicDir.resolve(sourcePicFile.getFileName());
                    Files.copy(sourcePicFile, targetFilePic, StandardCopyOption.REPLACE_EXISTING);
                    log.info("【图片源文件】 " + sourcePicFile + " 【拷贝到】 " + targetPicDir);




                    Path sourceXmlFile = Paths.get(filePath+pic.getMarkXml());  // 源文件路径
                    Path targetXmlDir = Paths.get(xmlPath);  // 目标目录路径
                    Path targetFileXml = targetXmlDir.resolve(sourceXmlFile.getFileName());
                    Files.copy(sourceXmlFile, targetFileXml, StandardCopyOption.REPLACE_EXISTING);
                    log.info("【xml源文件】 " + sourceXmlFile + " 【拷贝到】 " + targetXmlDir);

                }catch (Exception ex){
                    ex.printStackTrace();
                    log.error("拷贝失败");
                }

        }
        PngChangeJpg(imgPath);

    }

    /***
     * png转jpg统一变成jpg处理
     * @param directoryPath
     */
    public static void PngChangeJpg(String directoryPath){
        // 指定目录路径


        // 打开目录
        File dir = new File(directoryPath);

        // 过滤出目录中的所有 PNG 文件
        File[] pngFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });

        if (pngFiles != null && pngFiles.length > 0) {
            for (File pngFile : pngFiles) {
                // 原文件名（去掉扩展名）
                String fileNameWithoutExtension = pngFile.getName().substring(0, pngFile.getName().lastIndexOf('.'));
                // 目标文件路径（转换后的 JPG 文件）
                File jpgFile = new File(dir, fileNameWithoutExtension + ".jpg");

                try {
                    // 读取 PNG 图像
                    BufferedImage pngImage = ImageIO.read(pngFile);

                    // 创建 RGB 格式的 BufferedImage（没有透明度）
                    BufferedImage jpgImage = new BufferedImage(pngImage.getWidth(), pngImage.getHeight(), BufferedImage.TYPE_INT_RGB);

                    // 将 PNG 图像绘制到 RGB BufferedImage 中
                    jpgImage.createGraphics().drawImage(pngImage, 0, 0, null);

                    // 将 RGB BufferedImage 保存为 JPG 文件
                    ImageIO.write(jpgImage, "jpg", jpgFile);
                    // 删除原始 PNG 文件
                    if (pngFile.delete()) {
                        log.info("转换并删除成功: " + pngFile.getName() + " -> " + jpgFile.getName());
                    } else {
                        log.info("删除失败: " + pngFile.getName());
                    }

                } catch (IOException e) {
                    log.info("转换失败: " + pngFile.getName());
                    e.printStackTrace();
                }
            }
        } else {
            log.info("目录中没有找到 PNG 文件。");
        }
    }


}
