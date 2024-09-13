package org.jeecg.modules.demo.study;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.wlld.randomForest.DataTable;
import org.wlld.randomForest.RandomForest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * 机器学习 - 开始训练学习
 * @author wggg
 * @date
 */
public class StartMachineLearning {

    static RandomForest randomForest;



    public static void main(String[] args) throws Exception {

        Set<String> column=new HashSet<>();
        column.add("motorVibration");    //数据特征-电机震动
        column.add("motorVoltage");    //数据特诊- 电机电压
        column.add("runTime");    //数据特征-运行时长
        column.add("runTemp");    //数据特征-运行温度
        column.add("runNum"); //数据特征-运行次数
        column.add("dangerousAct");//危险行为 当前有为1
        column.add("other"); //其他特征 有为1 没有为0
        column.add("result");

        DataTable dataTable=new DataTable(column);
        dataTable.setKey("result"); //推理结果值
        randomForest=new RandomForest(7);
        randomForest.init(dataTable);  //唤醒随机森林内容
       String path="F:\\JAVAAI\\RandomForest.txt";
       File file=new File(path);
       Long startTime = System.currentTimeMillis();
        //进行文件的读取，返回结果：每行数据都是一个string字符串
        List<String> lines = FileUtils.readLines(file, "GB2312");
        int k=0;
        for (String line : lines) {
            // 在这里添加对每行数据的处理逻辑
            System.out.println("采集条目"+k+"Processing line: " + line);
            k++;
            String [] lineList=line.split(",");
            ElevatorModel elevatorModel=new ElevatorModel();
            elevatorModel.setMotorVibration(getReadText(lineList[3]));
            elevatorModel.setMotorVoltage(getReadText(lineList[3]));
            elevatorModel.setRunTemp(getReadText(lineList[3]));
            elevatorModel.setRunTime(getReadText(lineList[3]));
            elevatorModel.setRunNum(getReadText(lineList[3]));
            elevatorModel.setDangerousAct(getReadText(lineList[3]));
            elevatorModel.setOther(getReadText(lineList[4]));
            elevatorModel.setResult(1); //为1号预案
            randomForest.insert(elevatorModel);
            Long endTime = System.currentTimeMillis();
            long consume = (endTime - startTime)/1000;
            System.out.println("************训练耗时:"+consume+"秒*****************");


        }
        Long endTime = System.currentTimeMillis();
        long consume = (endTime - startTime)/1000;
        System.out.println("************总共耗时:"+consume+"秒*****************");
        randomForest.study(); //开始学习内容
        //开始训练 并出结果内容
     //   RandomForestDataStudy();


        //开始识别
        // 结果输出
        ElevatorModel elevatorModel=new ElevatorModel();
        elevatorModel.setResult(0);
        elevatorModel.setDangerousAct(getInt());
        elevatorModel.setRunNum(getInt());
        elevatorModel.setMotorVoltage(getInt());
        elevatorModel.setRunTemp(getInt());
        elevatorModel.setRunTime(getInt());
        elevatorModel.setMotorVibration(getInt());
        elevatorModel.setOther(getInt());
        int c=randomForest.forest(elevatorModel);
        System.out.println("当前结果值:"+c+"应该走"+c+"号预案预测结果值");
    }

        public static int getReadText(String text) throws InterruptedException {
      //  Thread.sleep(getInt()*1000);
        return Integer.parseInt(text.replaceAll("PMS","").replaceAll("AL-","").replaceAll("KW","").replaceAll("HZ","").replaceAll("V",""));
        }

    /***
     *
     * 随机森林样本数据训练
     */
    public static boolean RandomForestDataStudy(){
        Set<String> column=new HashSet<>();
        column.add("motorVibration");    //数据特征-电机震动
        column.add("motorVoltage");    //数据特诊- 电机电压
        column.add("runTime");    //数据特征-运行时长
        column.add("runTemp");    //数据特征-运行温度
        column.add("runNum"); //数据特征-运行次数
        column.add("dangerousAct");//危险行为 当前有为1
        column.add("other"); //其他特征 有为1 没有为0
        column.add("result");
        try {
            DataTable dataTable=new DataTable(column);
            dataTable.setKey("result"); //推理结果值
            randomForest=new RandomForest(7);
            randomForest.init(dataTable);  //唤醒随机森林内容
            //创建实体类输入数据先使用5000条目数据 //都设置为远
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static  int getInt(){
        int a=new Random().nextInt(3)+1;
       // System.out.println(a);
        return  a;
    }
    /***
     *
     * 决策树训练数据集
     */
    public boolean DecisionTreeDataStudy(){



        return true;
    }






}
