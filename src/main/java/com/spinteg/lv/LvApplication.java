package com.spinteg.lv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.spinteg.lv.exception.GlobalExceptionHandler;

@SpringBootApplication
@EnableScheduling
@Import(GlobalExceptionHandler.class)
public class LvApplication {

    public static void main(String[] args) {
        SpringApplication.run(LvApplication.class, args);
    }
}
