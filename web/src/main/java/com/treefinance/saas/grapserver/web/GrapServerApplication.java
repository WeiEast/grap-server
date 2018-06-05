package com.treefinance.saas.grapserver.web;

import com.treefinance.saas.assistant.annotation.EnableMonitorPlugin;
import com.treefinance.saas.assistant.variable.notify.annotation.EnableVariableNotifyListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan("com.treefinance.saas.grapserver.web")
@ImportResource("classpath:spring/applicationContext.xml")
@EnableAsync
@EnableMonitorPlugin
@EnableVariableNotifyListener
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class GrapServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrapServerApplication.class);
    }
}
