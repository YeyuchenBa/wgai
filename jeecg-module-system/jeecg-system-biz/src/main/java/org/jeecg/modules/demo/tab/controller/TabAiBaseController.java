package org.jeecg.modules.demo.tab.controller;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.demo.tab.service.ITabAiBaseService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

import static org.jeecg.modules.tab.AIModel.AIModelYolo3.*;

/**
 * @Description: AI基础信息
 * @Author: jeecg-boot
 * @Date:   2024-03-20
 * @Version: V1.0
 */
@Api(tags="AI基础信息")
@RestController
@RequestMapping("/tab/tabAiBase")
@Slf4j
public class TabAiBaseController extends JeecgController<TabAiBase, ITabAiBaseService> {
	@Autowired
	private ITabAiBaseService tabAiBaseService;


	@Resource
	WebSocket webSocket;

	// @GetMapping(value = "/test")
	 public void test() {
		 System.load("F:\\JAVAAI\\opencv481\\opencv\\build\\java\\x64\\opencv_java481.dll");
		 //   SendPicYoloV3("yolov3.weights","yolov3.cfg","coco.names","car.jpg","test","F:\\JAVAAI\\yolo3\\yuanshi");
		 //     SendPicYoloV5("NBplate.onnx","coco.names","writecat.jpg","","F:\\JAVAAI\\yolov5");
		 //    SendPicYoloV5Car("NBplate.onnx","coco.names","bluecar.jpg","","F:\\JAVAAI\\yolov5");
		 String rtspUrl="rtsp://admin:ch255899@192.168.0.200/Streaming/Channels/102";

//        String rtspUrl = "rtsp://[用户名]:[密码]@[IP地址]:[端口]/[码流类型]";

		 FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
		 OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		 VideoWriter videoWriter = new VideoWriter();
		 try {
			 grabber.setOption("rtsp_transport", "tcp"); // 使用TCP而不是UDP
			 grabber.start();
			 System.out.println("连接到RTSP流成功");
			 Java2DFrameConverter converter = new Java2DFrameConverter();
			 Frame frame;
			 int a=0;

			 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			 FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(byteArrayOutputStream, grabber.getImageWidth(), grabber.getImageHeight());
//			 recorder = new FFmpegFrameRecorder(new org.bytedeco.javacv.FrameRecorder.FrameRecorderAVIO(
//					 (org.bytedeco.javacv.FrameRecorder.FrameRecorderAVIOCallback) bufferCallback::call, null
//			 ), 1280, 720);
			 recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
			 recorder.setFormat("flv");
			 System.out.println("帧率"+grabber.getFrameRate());
			 recorder.setFrameRate(grabber.getFrameRate());
			 recorder.setGopSize(30); // 每30帧一个关键帧
			 recorder.setVideoQuality(0); // 最好的质量
			 recorder.setVideoOption("preset", "ultrafast");
			 recorder.setVideoOption("tune", "zerolatency");
			 recorder.start();


			 byte[] flvHeader = createFLVHeader();
			 SendWebSocket(flvHeader);
			 while ((frame = grabber.grab()) != null) {

				 // 将Frame转换为JavaCV的Mat
				 int width=frame.imageWidth;
				 int height=frame.imageHeight;
				 System.out.println(a+"成功获取帧宽度"+width+"高度:"+height);
				 Mat opencvMat=bufferedImageToMat(   converter.getBufferedImage(frame));
				 if(a<=500) {
					 // 录制帧 b释放才会保存
					 Frame processedFrame = converterToFrame(opencvMat);
					 //重置byteArrayOutputStream
					// byteArrayOutputStream.reset();
					 //编码写入FLV
					 recorder.record(processedFrame);
					 // 强制刷新recorder，确保数据写入ByteArrayOutputStream
					 //	 recorder.flush();
					 // 获取编码后的数据
					 byte[] encodedData = byteArrayOutputStream.toByteArray();
					 if (encodedData.length > 0) {
						 SendWebSocket(encodedData);
						 byteArrayOutputStream.reset(); // 重置输出流
					 }
//                    Imgcodecs.imwrite("F:\\JAVAAI\\model\\test3\\test"+a+".jpg", opencvMat);
					// saveVideo(opencvMat, frame, videoWriter, "F:\\JAVAAI\\model\\test3\\test1.mp4");
				 }else{
					 break;
				 }
				 a++;


				 // 显示帧（如果您想要显示视频）
				 //   canvasFrame.showImage(frame);

			 }

			 grabber.stop();
			 grabber.release();

			 recorder.stop();
			 recorder.release();
			 //     canvasFrame.dispose();
		 } catch (Exception e) {
			 System.err.println("发生错误: " + e.getMessage());
			 e.printStackTrace();
		 }
	 }

	 public void SendWebSocket(byte[] text ){
		 webSocket.pushMessageByte(text);
	 }

	private byte[] createFLVHeader() {
		byte[] header = new byte[13];
		header[0] = 'F';
		header[1] = 'L';
		header[2] = 'V';
		header[3] = 1;
		header[4] = 5; // 有视频和音频
		header[5] = 0;
		header[6] = 0;
		header[7] = 0;
		header[8] = 9;
		return header;
	}
	/**
	 * 分页列表查询
	 *
	 * @param tabAiBase
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "AI基础信息-分页列表查询")
	@ApiOperation(value="AI基础信息-分页列表查询", notes="AI基础信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabAiBase>> queryPageList(TabAiBase tabAiBase,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabAiBase> queryWrapper = QueryGenerator.initQueryWrapper(tabAiBase, req.getParameterMap());
		Page<TabAiBase> page = new Page<TabAiBase>(pageNo, pageSize);
		IPage<TabAiBase> pageList = tabAiBaseService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabAiBase
	 * @return
	 */
	@AutoLog(value = "AI基础信息-添加")
	@ApiOperation(value="AI基础信息-添加", notes="AI基础信息-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_base:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabAiBase tabAiBase) {
		tabAiBaseService.save(tabAiBase);
		return Result.OK("添加成功！");
	}

	 @AutoLog(value = "AI基础信息-刷新缓存")
	 @ApiOperation(value="AI基础信息-添加", notes="AI基础信息-添加")
	 @PostMapping(value = "/sendRedis")
	 public Result<String> sendRedis() {
		 tabAiBaseService.SendRedisBase();
		 return Result.OK("刷新成功！");
	 }
	/**
	 *  编辑
	 *
	 * @param tabAiBase
	 * @return
	 */
	@AutoLog(value = "AI基础信息-编辑")
	@ApiOperation(value="AI基础信息-编辑", notes="AI基础信息-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_base:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabAiBase tabAiBase) {
		tabAiBaseService.updateById(tabAiBase);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "AI基础信息-通过id删除")
	@ApiOperation(value="AI基础信息-通过id删除", notes="AI基础信息-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_base:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabAiBaseService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "AI基础信息-批量删除")
	@ApiOperation(value="AI基础信息-批量删除", notes="AI基础信息-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_base:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabAiBaseService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "AI基础信息-通过id查询")
	@ApiOperation(value="AI基础信息-通过id查询", notes="AI基础信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabAiBase> queryById(@RequestParam(name="id",required=true) String id) {
		TabAiBase tabAiBase = tabAiBaseService.getById(id);
		if(tabAiBase==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabAiBase);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabAiBase
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_ai_base:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabAiBase tabAiBase) {
        return super.exportXls(request, tabAiBase, TabAiBase.class, "AI基础信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_ai_base:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabAiBase.class);
    }

}
