package com.treefinance.saas.grapserver.web;

import com.treefinance.saas.assistant.annotation.EnableMonitorPlugin;
import com.treefinance.saas.assistant.config.annotation.EnableConfigUpdateListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan("com.treefinance.saas.grapserver.web")
@ImportResource("classpath:spring/applicationContext.xml")
@EnableAsync
@EnableMonitorPlugin
@EnableConfigUpdateListener
public class GrapServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrapServerApplication.class);
    }
}
