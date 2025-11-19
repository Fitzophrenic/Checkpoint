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
        String userID = null;
        String projectID = null;
        String userName= null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

        String line;
        while ((line = reader.readLine()) != null) {
            if(line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] partsInText = line.split(":", 3);
            String command = partsInText[0].trim();
            String arg1 = partsInText.length > 1 ? partsInText[1].trim() : null;
            String arg2 = partsInText.length > 2 ? partsInText[2].trim() : null;

            switch (command) {
                case "userID" -> { //userID: userID
                    userID = arg1;
                }

                case "projectID" -> { //projectID: projectID
                    projectID = arg1;
                }

                case "userName" -> { //userName: userName
                    userName = arg1;
                }
                
                case "create-user" -> { //create-user
                    sqlRequest.createUser(userName);
                }
                    
                case "create-project" -> { //create-project: projectName
                    sqlRequest.createProject(line.substring(15), userID);
                }
                case "delete-project" -> { //delete-project
                    sqlRequest.deleteProject(projectID, userID);
                }
                case "remove-user-from-project" -> { //remove-user-from-project: anotherUserID
                    if(!sqlRequest.checkPermissionLevel(projectID, userID).equals("o")) {
                        continue;
                    }
                    sqlRequest.deleteProject(projectID, arg1);
                }
                case "add-user-to-project" -> { //add-user-to-project: userID : permissionLevel
                    if(!sqlRequest.checkPermissionLevel(projectID, userID).equals("o")) {
                        continue;
                    }
                    sqlRequest.changePermissionLevel(projectID, arg1, arg2);
                }
                default -> {
                    
                }
            }

            System.out.println(line);
            }
        } catch (IOException e) {


        }
        return null;
    }
}
