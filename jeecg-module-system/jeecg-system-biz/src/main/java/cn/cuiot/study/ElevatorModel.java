package cn.cuiot.study;

import lombok.Data;

/**
 * @author wggg
 * @date
 */
@Data
public class ElevatorModel {

    //数据特征-电机震动
    public int motorVibration;
    //数据特诊- 电机电压
    public int motorVoltage;
    //数据特征-运行时长
    public int runTime;
    //数据特征-运行温度
    public int runTemp;
    //数据特征-运行频率次数
    public int runNum;
    //数据特征-危险行为
    public  int dangerousAct;
    //数据特征其他
    public int other;

    //数据特征预算结果
    public int result;
}
