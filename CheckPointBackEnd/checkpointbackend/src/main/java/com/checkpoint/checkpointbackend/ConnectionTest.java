package com.checkpoint.checkpointbackend;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ConnectionTest {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void verifyConnection() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Verified: Spring Boot is connected to MySQL!");
            System.out.println(conn.toString());
            
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}