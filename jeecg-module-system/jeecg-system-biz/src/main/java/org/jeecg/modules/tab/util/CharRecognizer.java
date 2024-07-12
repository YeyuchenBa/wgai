package org.jeecg.modules.tab.util;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

public class CharRecognizer {
    private KNearest knnChinese;
    private KNearest knnAlphanumeric;
    private List<Mat> chineseTemplates;
    private List<Mat> alphanumericTemplates;
    private List<Integer> chineseChars;
    private List<Integer> alphanumericChars;
    private String[] chineseSet;
    private String[] alphanumericSet;
    public CharRecognizer() {
        knnChinese = KNearest.create();
        knnAlphanumeric = KNearest.create();
        chineseTemplates = new ArrayList<>();
        alphanumericTemplates = new ArrayList<>();
        chineseChars = new ArrayList<>();
        alphanumericChars = new ArrayList<>();
        loadTemplates();
        trainKNN();
    }

    private void loadTemplates() {
        // 加载中文省份简称模板
         chineseSet =new String[] {"京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪", "苏",
                "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂",
                "琼", "渝", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新"};
        for (int i = 0; i < chineseSet.length; i++) {
            Mat template = Imgcodecs.imread("F:\\JAVAAI\\yolov5\\car_numbr_temp\\hu.png", Imgcodecs.IMREAD_GRAYSCALE);
            if (template.empty()) {
                System.err.println("Failed to load template: " + chineseSet[i]);
                continue;
            }
            chineseTemplates.add(preprocessChar(template));
            chineseChars.add(i);  // 使用索引作为标签
        }

        // 加载数字和字母模板
        alphanumericSet =new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F", "G", "H", "J", "K",
                "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        for (int i = 0; i < alphanumericSet.length; i++) {
            Mat template = Imgcodecs.imread("F:\\JAVAAI\\yolov5\\car_numbr_temp\\" + alphanumericSet[i] + ".png", Imgcodecs.IMREAD_GRAYSCALE);
            if (template.empty()) {
                System.err.println("Failed to load template: " + alphanumericSet[i]);
                continue;
            }
            alphanumericTemplates.add(preprocessChar(template));
            alphanumericChars.add(i);  // 使用索引作为标签
        }
    }

    private void trainKNN() {
        trainKNNForSet(knnChinese, chineseTemplates, chineseChars);
        trainKNNForSet(knnAlphanumeric, alphanumericTemplates, alphanumericChars);
    }

    private void trainKNNForSet(KNearest knn, List<Mat> templates, List<Integer> chars) {
        Mat samples = new Mat();
        Mat responses = new Mat();

        for (int i = 0; i < templates.size(); i++) {
            Mat sample = templates.get(i).reshape(1, 1);
            samples.push_back(sample);
            responses.push_back(new MatOfInt(chars.get(i)));
        }

        samples.convertTo(samples, CvType.CV_32F);
        responses.convertTo(responses, CvType.CV_32S);

        knn.train(samples, Ml.ROW_SAMPLE, responses);
    }

    public char recognizeChineseChar(Mat charMat) {
        return recognizeChar(charMat, knnChinese, chineseSet);
    }

    public char recognizeChar(Mat charMat) {
        return recognizeChar(charMat, knnAlphanumeric, alphanumericSet);
    }

    private char recognizeChar(Mat charMat, KNearest knn, String[] charSet) {
        Mat processedChar = preprocessChar(charMat);
        Mat sample = processedChar.reshape(1, 1);

        Mat results = new Mat();
        Mat neighborResponses = new Mat();
        Mat dists = new Mat();

        knn.findNearest(sample, 1, results, neighborResponses, dists);

        int classIndex = (int) results.get(0, 0)[0];
        return charSet[classIndex].charAt(0);
    }

    private Mat preprocessChar(Mat charMat) {
        Mat processed = new Mat();
        Imgproc.resize(charMat, processed, new Size(20, 20));
        processed.convertTo(processed, CvType.CV_32F);
        Core.normalize(processed, processed, 0, 1, Core.NORM_MINMAX);
        return processed;
    }
}