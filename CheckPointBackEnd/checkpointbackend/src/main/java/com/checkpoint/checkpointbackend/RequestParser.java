package com.checkpoint.checkpointbackend;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/files")
public class RequestParser {
    @Autowired
    private SQLRequest sqlRequest;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

        String line;
        while ((line = reader.readLine()) != null) {


            System.out.println(line);
            }
        } catch (IOException e) {


        }
        return null;
    }
}
