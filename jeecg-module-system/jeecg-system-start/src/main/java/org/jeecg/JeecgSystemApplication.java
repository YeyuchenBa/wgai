package org.jeecg;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.oConvertUtils;
import org.opencv.core.Core;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
* 单体启动类
* 报错提醒: 未集成mongo报错，可以打开启动类上面的注释 exclude={MongoAutoConfiguration.class}
*/
@Slf4j
@ComponentScan(value = {"org.jeecg","cn.cuiot"})
@SpringBootApplication
//@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class JeecgSystemApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JeecgSystemApplication.class);
    }



    public static void main(String[] args) throws UnknownHostException {
       // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

       // System.load("C:\\JAVAAI\\opencv\\build\\java\\x64\\opencv_java481.dll");
        ConfigurableApplicationContext application = SpringApplication.run(JeecgSystemApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = oConvertUtils.getString(env.getProperty("server.servlet.context-path"));
        String opencvpath = env.getProperty("opencv");
        String audiopath = env.getProperty("audio.dll");


        log.info("\n----------------------------------------------------------\n\t" +
                "Application Jeecg-Boot is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                "Swagger文档: \thttp://" + ip + ":" + port + path + "/doc.html\n\t" +
                "opencvpath:"+opencvpath+"\n\t  "+
                "audio:"+audiopath+"\n\t  "+
                "----------------------------------------------------------");
        File opencv=new File(opencvpath);
        if(opencv.exists()){
            System.load(opencvpath);
        }else{
            log.error("opencv文件不存在！请检查地址是否正确 或 是否编译opencv");
        }

        File audio=new File(audiopath);
        if(audio.exists()){
            System.load(audiopath);
        }else{
            log.error("audio文件不存在！请检查地址是否正确 或 是否编译audio");
        }

    }

}