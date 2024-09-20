package cn.cuiot.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.dto.message.MessageDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @author wggg
 * @date 2024/9/20 9:33
 */
@Slf4j
@RestController
@RequestMapping("/treeStudy")
public class TreeStudyController {

    @GetMapping("/test")
    public String test(){

      log.info("输出内容！！！！！！！！！！！！！！！！！");
      return "1111";
    }
}
