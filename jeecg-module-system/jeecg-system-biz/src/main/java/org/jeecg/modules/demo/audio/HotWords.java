package org.jeecg.modules.demo.audio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.k2fsa.sherpa.onnx.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

/**
 * @author wggg
 * @date 2024/10/18 16:54
 */
@Slf4j
public class HotWords {

    static String path="F:\\JAVAAI\\audio\\sherpa-onnx-conformer-zh-stateless2-2023-05-23\\";
    public static void main(String[] args) throws Exception {
        String data = new String("{\"run_state\":1,\"door_state\":4,\"now_tier\":2,\"load_state\":140,\"load_weight\":1500,\"top_tier\":7,\"low_tier\":1,\"run_speed\":1,\"now_position\":23,\"sum_run_time\":1962,\"error_code\":1,\"bpq_tmp\":60,\"motherboard_state\":0,\"curve_run\":1}");
        log.info("设备预测事件, data:{}", data);
        JSONObject jsonData = JSONObject.parseObject(data, Feature.OrderedField);
        if(!checkMqtt(jsonData)){
            log.error("数据不和标准输出");
        }
        log.info("数据通过{}",jsonData.toJSONString());

        String cmd = "python F:\\JAVAAI\\yucemoxing\\new\\inference.py";

        Process proc = Runtime.getRuntime().exec(cmd);
//        String  a=" {" +
//                "    'run_state': 1," +
//                "    'door_state': 4," +
//                "    'now_tier': 2," +
//                "    'load_state': 2," +
//                "    'load_weight': 1500," +
//                "    'top_tier': 7," +
//                "    'low_tier': 1," +
//                "    'run_speed': 0," +
//                "    'now_position': 167," +
//                "    'sum_run_time': 2514," +
//                "    'error_code_isok': 1, " +
//                "    'bpq_tmp': 26" +
//                "}";
//        JSONObject jsonData=JSONObject.parseObject(a);
        // 向 Python 脚本发送参数

        OutputStream stdin = proc.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin, StandardCharsets.UTF_8));
        writer.write(jsonData.toJSONString());
        writer.newLine();
        writer.flush();
        writer.close();
       // Thread.sleep(2000);
        // 读取 Python 脚本的标准输出
        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        String output ="";
        String line;
        while ((line = stdoutReader.readLine()) != null) {
            System.out.println("读取值"+line);
            output+=(line);
        }

        // 检查是否有错误信息
//        BufferedReader stderrReader = new BufferedReader(new InputStreamReader(proc.getErrorStream(), StandardCharsets.UTF_8));
//        while ((line = stderrReader.readLine()) != null) {
//            log.error(line);
//        }

        // 等待进程结束
        proc.waitFor();
        log.info("预测事件结果: {}", output);
        String [] outputSplit=output.split("\\|");
        for (int i = 0; i <outputSplit.length ; i++) {
            System.out.println(outputSplit[i]);
        }
        log.info("结果内容{}",output);
        int state=Integer.parseInt(outputSplit[outputSplit.length-1].trim());
        log.info("预测事件结果状态值: {}", state);
        // please refer to
        // https://k2-fsa.github.io/sherpa/onnx/pretrained_models/online-transducer/zipformer-transducer-models.html#csukuangfj-sherpa-onnx-streaming-zipformer-bilingual-zh-en-2023-02-20-bilingual-chinese-english
        // to download model files
//        String encoder =
//                path+"encoder-epoch-99-avg-1.int8.onnx";
//        String decoder =
//                path+"decoder-epoch-99-avg-1.onnx";
//        String joiner =
//                path+ "joiner-epoch-99-avg-1.onnx";
//        String tokens =  path+"tokens.txt";
//
//        String hotwords=path+"hotwords_cn.txt";
//
//        String waveFilename =
//                path+  "test_wavs\\24.wav";
//        WaveReader reader = new WaveReader(waveFilename);
//        OfflineTransducerModelConfig transducer =
//                OfflineTransducerModelConfig.builder()
//                        .setEncoder(encoder)
//                        .setDecoder(decoder)
//                        .setJoiner(joiner)
//                        .build();
//        OfflineModelConfig modelConfig =
//                OfflineModelConfig.builder()
//                        .setTransducer(transducer)
//                        .setTokens(tokens)
//                        .setNumThreads(1)
//                        .setDebug(true)
//                        .setModelingUnit("cjkchar")
//                        .build();
//                       // .build();
//        OfflineRecognizerConfig config =
//                OfflineRecognizerConfig.builder()
//                        .setOfflineModelConfig(modelConfig)
//                        .setDecodingMethod("modified_beam_search")
//                        .setHotwordsFile(hotwords)
//                        .setHotwordsFile(hotwords)
//                        .setHotwordsScore(0.5f)
//                        .build();
//        OfflineRecognizer recognizer = new OfflineRecognizer(config);
//        OfflineStream stream = recognizer.createStream();
//        stream.acceptWaveform(reader.getSamples(), reader.getSampleRate());
//        recognizer.decode(stream);
//        String text = recognizer.getResult(stream).getText();
//        System.out.printf("filename:%sresult:%s", waveFilename, text);
//        stream.release();
//        recognizer.release();
    }


    //检查数据符合内容
//    上诉参数，绿色不用校验，故障码数据可以为空，剩余8个关键参数
//    1、run_state 数据非0/1/2 的数据丢弃
//    2、door_state 数据非0/1/2/3/4 的数据丢弃
//    3、now_tier  为空的数据丢弃
//    4、load_state 小于0的数据丢弃
//    5、run_speed 大于6的数据丢弃
//    6、now_position 为空数据丢弃
//    7、bpq_tmp  大于150的数据丢弃
//    8、一组数据中6个为空的数据丢弃
    public static boolean checkMqtt(JSONObject object){
        Integer run_state=object.getInteger("run_state");
        Integer door_state=object.getInteger("door_state");
        String now_tier=object.getString("now_tier");
        Integer load_state=object.getInteger("load_state");
        Double run_speed=object.getDouble("run_speed");
        String now_position=object.getString("now_position");
        Double bpq_tmp=object.getDouble("bpq_tmp");
        try {
            if(StringUtils.isEmpty(now_position)||StringUtils.isEmpty(now_tier)||run_state==null&&door_state==null&&StringUtils.isEmpty(now_tier)&&load_state==null&&run_speed==null&&StringUtils.isEmpty(now_position)){
                log.error("参数为空");
                return false;
            }
            if(run_state>2||run_state<0){
                log.error("运行速度状态不正确{}",run_state);
                return false;
            }
            if(door_state>4||door_state<0){
                log.error("门状态不正确{}",door_state);
                return false;
            }
            if(load_state<0){
                log.error("载重状态不正确{}",load_state);
                return false;
            }
            if(run_speed>6){
                log.error("运行速度大于6 {}",run_speed);
                return false;
            }
            if(bpq_tmp>150){
                log.error("变频器温度大于150 ：{}",bpq_tmp);
                return false;
            }
            return true;
        }catch (Exception ex){
            log.error("当前处理数据错误失败{}");
            ex.printStackTrace();
            return false;
        }

    }

    /***
     * 换取string内容
     * @param state
     * @return
     */
    public String charText(int state,int other){
        String text="";
        switch (state){
            case 1:
                text="超总导致机械磨损风险|机械磨损风险";
                break;
            case 2:
                text="变频器的风道堵塞或控制柜的风道被阻塞|机械磨损风险";
                break;
            case 3:
                text="机械磨损风险|缆绳形变风险";
                break;
            case 4:
            case 6:
                text="机械磨损风险|";
                break;
            case 5:
                text="变频器的风道堵塞或控制柜的风道被阻塞|机械磨损风险|缆绳形变风险";
                break;
            case 7:
                text="变频器的风道堵塞或控制柜的风道被阻塞|";
                break;
            case 8:
                text="缆绳形变风险|";
                break;
            case 9:
                text="轴承磨损风险|";
                break;
            case 10:
                text="日常周期性维护|";
                break;
            case 11:
                text="缆断裂风险|";
                break;
            default:
                text="正常|";
        }
        if(other>0){
            text=text+"火灾风险|";
        }
        return text;
    }



}
