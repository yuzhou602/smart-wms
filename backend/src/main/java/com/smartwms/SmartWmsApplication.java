package com.smartwms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.smartwms.**.mapper")
@ComponentScan(basePackages = "com.smartwms")
public class SmartWmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartWmsApplication.class, args);
    }
}
