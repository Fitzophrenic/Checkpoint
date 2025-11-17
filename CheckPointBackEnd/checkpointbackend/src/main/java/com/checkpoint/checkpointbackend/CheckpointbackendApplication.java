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

        if (sqlRequest.getConnection() != null) {
            sqlRequest.createUser("ham");
            sqlRequest.createUser("burg");
            sqlRequest.createUser("er");
            sqlRequest.createProject("biggmonet", "000000000001");
        } else {
            System.err.println("Database connection not initialized.");
        }
    }
}