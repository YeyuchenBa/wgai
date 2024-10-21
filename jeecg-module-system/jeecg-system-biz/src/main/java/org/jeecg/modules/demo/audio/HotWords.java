package org.jeecg.modules.demo.audio;

import com.k2fsa.sherpa.onnx.*;
/**
 * @author wggg
 * @date 2024/10/18 16:54
 */
public class HotWords {

    static String path="F:\\JAVAAI\\audio\\sherpa-onnx-conformer-zh-stateless2-2023-05-23\\";
    public static void main(String[] args) {
        // please refer to
        // https://k2-fsa.github.io/sherpa/onnx/pretrained_models/online-transducer/zipformer-transducer-models.html#csukuangfj-sherpa-onnx-streaming-zipformer-bilingual-zh-en-2023-02-20-bilingual-chinese-english
        // to download model files
        String encoder =
                path+"encoder-epoch-99-avg-1.int8.onnx";
        String decoder =
                path+"decoder-epoch-99-avg-1.onnx";
        String joiner =
                path+ "joiner-epoch-99-avg-1.onnx";
        String tokens =  path+"tokens.txt";

        String hotwords=path+"hotwords_cn.txt";

        String waveFilename =
                path+  "test_wavs\\25.wav";
        WaveReader reader = new WaveReader(waveFilename);
        OfflineTransducerModelConfig transducer =
                OfflineTransducerModelConfig.builder()
                        .setEncoder(encoder)
                        .setDecoder(decoder)
                        .setJoiner(joiner)
                        .build();
        OfflineModelConfig modelConfig =
                OfflineModelConfig.builder()
                        .setTransducer(transducer)
                        .setTokens(tokens)
                        .setNumThreads(1)
                        .setDebug(true)
                        .setModelingUnit("cjkchar")
                        .build();
                       // .build();
        OfflineRecognizerConfig config =
                OfflineRecognizerConfig.builder()
                        .setOfflineModelConfig(modelConfig)
                        .setDecodingMethod("modified_beam_search")
                        .setHotwordsFile(hotwords)
                        .setHotwordsScore(20.0f)
                        .build();
        OfflineRecognizer recognizer = new OfflineRecognizer(config);
        OfflineStream stream = recognizer.createStream();
        stream.acceptWaveform(reader.getSamples(), reader.getSampleRate());
        recognizer.decode(stream);
        String text = recognizer.getResult(stream).getText();
        System.out.printf("filename:%s\nresult:%s\n", waveFilename, text);
        stream.release();
        recognizer.release();
    }

}
