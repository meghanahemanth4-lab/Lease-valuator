package com.spinteg.lv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.spinteg.lv.ExceptionHandling.GlobalExceptionHandler;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
@Import(GlobalExceptionHandler.class)
public class LvApplication {

	public static void main(String[] args) {
		SpringApplication.run(LvApplication.class, args);
	}

}
