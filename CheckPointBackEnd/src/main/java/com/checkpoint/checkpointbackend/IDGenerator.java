package com.checkpoint.checkpointbackend;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;


@Component
public class IDGenerator {
    private final static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateID(){

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i<12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
