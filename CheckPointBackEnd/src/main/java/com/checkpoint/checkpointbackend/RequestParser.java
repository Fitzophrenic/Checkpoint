package com.checkpoint.checkpointbackend;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        String username = null;
        String projectID = null;
        Map<String, Object> response = new HashMap<>();

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
            System.out.println(line);

            switch (command) {
                case "username" -> { //userID: userID
                    username = arg1;
                }

                case "projectID" -> { //projectID: projectID
                    projectID = arg1;
                }

                // case "userName" -> { //userName: userName
                //     userName = arg1;
                // }
                
                case "create-user" -> { //create-user: password
                    sqlRequest.createUser(username, arg1);
                    
                }
                    
                case "create-project" -> { //create-project: projectName
                    String id = sqlRequest.createProject(arg1, username);
                    response.put("projectID", id);
                    response.put("projectName", arg1);
                    return ResponseEntity.ok(response);
                }
                case "delete-project" -> { //delete-project
                    sqlRequest.deleteProject(projectID, username);
                }
                case "remove-user-from-project" -> { //remove-user-from-project : anotherUserID
                    if(!sqlRequest.checkPermissionLevel(projectID, username).equals("o")) {
                        continue;
                    }
                    sqlRequest.removeUserFromProject(projectID, arg1);
                }
                case "add-user-to-project" -> { //add-user-to-project: userID : permissionLevel
                    if(!sqlRequest.checkPermissionLevel(projectID, username).equals("o")) {
                        continue;
                    }
                    sqlRequest.addUserToProject(projectID, arg1, arg2);
                }
                case "change-permission-level" -> { //change-permission-level: userID : permissionLevel
                    if(!sqlRequest.checkPermissionLevel(projectID, username).equals("o")) {
                        continue;
                    }
                    sqlRequest.changePermissionLevel(projectID, arg1, arg2);

                }
                case "get-project-boards" -> { // get-project-boards
                    return ResponseEntity.ok(sqlRequest.requestProjectBoard(projectID));
                }
                case "get-user-json" -> { // get-user-json
                    return ResponseEntity.ok(sqlRequest.requestUserJson(username));
                }
                case "add-board-to-project" -> { // add-board-to-project: boardName : content
                    sqlRequest.addBoardToProject(username, projectID, arg1, arg2);
                }
                case "update-board-section" -> { // update-board-section: boardName : content
                    sqlRequest.updateBoardSection(username, projectID, arg1, arg2);
                }
                case "get-board-data" -> {
                    String content = sqlRequest.getBoardSection(projectID, arg1);
                    response.put("content", content);
                    return ResponseEntity.ok(response);
                }
                case "login" -> {
                    return ResponseEntity.ok(sqlRequest.userLogIn(username, arg1));

                }
                default -> {
                    
                }
            }

            }
        } catch (IOException e) {


        }
        
        response.put("status", "command completed sucessfully");
        return ResponseEntity.ok(response);
    }
}
