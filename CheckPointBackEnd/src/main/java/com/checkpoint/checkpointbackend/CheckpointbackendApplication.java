package com.checkpoint.checkpointbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CheckpointbackendApplication {
        
    @Autowired
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CheckpointbackendApplication.class, args);
        SQLRequest sqlRequest = context.getBean(SQLRequest.class);

    }
}