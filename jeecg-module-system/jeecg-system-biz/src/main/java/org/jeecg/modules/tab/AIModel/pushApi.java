package org.jeecg.modules.tab.AIModel;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2024/4/8 18:52
 */
public class pushApi  implements Runnable {

    /**
     * 车牌识别内容
     * @param picUrl
     * @return
     * @throws Exception
     */
    public static String SendPicOpencvCarStr(String picUrl) throws Exception {
        System.load("F:\\JAVAAI\\opencv\\build\\java\\x64\\opencv_java3416.dll");
        Mat src = Imgcodecs.imread("F:\\JAVAAI\\xunlianhuidupic\\car.jpg");
        // 加载预训练的车牌检测级联分类器
        CascadeClassifier plateCascade = new CascadeClassifier("D:\\opt\\upFiles\\temp\\haarcascade_russian_plate_number_1718184313278.xml");
        Long a=System.currentTimeMillis();




        if (src.empty()) {
            System.out.println("Error: Cannot load image!");
            return null;
        }


        MatOfRect plates = new MatOfRect();
        plateCascade.detectMultiScale(src, plates, 1.1, 5, 0, new Size(),  new Size());
//        plateCascade.detectMultiScale(src, plates);

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("F:\\JAVAAI\\tessdata");
        tesseract.setLanguage("eng+chi_sim");

        List<Rect> plateRectangles = plates.toList();
        Rect largestPlateRect = findLargestPlate(plateRectangles);

        if (largestPlateRect != null) {
            Mat plate = new Mat(src, largestPlateRect);
            Imgproc.cvtColor(plate, plate, Imgproc.COLOR_BGR2GRAY);

            // 图像增强：调整对比度和亮度
            plate.convertTo(plate, -1, 1.2, 30); // 增强对比度和亮度

            // 去除噪点
     //       Imgproc.GaussianBlur(plate, plate, new Size(3, 3), 0);

            // 二值化处理
            Imgproc.threshold(plate, plate, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

            // 调整分辨率到300 DPI
            Mat resizedPlate = new Mat();
            Size size = new Size(plate.width() * 2, plate.height() * 2);
            Imgproc.resize(plate, resizedPlate, size, 0, 0, Imgproc.INTER_CUBIC);

            // 保存处理后的车牌图像

            String savepath="D://opt/upFiles" + File.separator + "temp" + File.separator;


            savepath+=System.currentTimeMillis()+".jpg";
            Imgcodecs.imwrite(savepath, resizedPlate);

            // OCR 识别
            try {
                String result = tesseract.doOCR(new File(savepath));
                result = result.replaceAll("\\s+", "").replaceAll("|",""); // 去除空白字符
                System.out.println("Detected License Plate: " + result.replaceAll("|",""));
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No license plate detected in the image.");
        }


        Long b=System.currentTimeMillis();

        return "savepath";
    }

    // 找到最大的车牌区域
    private static Rect findLargestPlate(List<Rect> plateRectangles) {
        if (plateRectangles.isEmpty()) {
            return null;
        }

        Rect largestRect = plateRectangles.get(0);
        double largestArea = largestRect.area();

        for (Rect rect : plateRectangles) {
            double area = rect.area();
            if (area > largestArea) {
                largestArea = area;
                largestRect = rect;
            }
        }

        return largestRect;
    }
    // 将 Mat 转换为 BufferedImage
    private static BufferedImage matToBufferedImage(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
    public static void main(String[] args) throws Exception {
        SendPicOpencvCarStr("F:\\JAVAAI\\xunlianhuidupic\\temp_image.jpg");
    }
    @Override
    public void run() {

    }
}
