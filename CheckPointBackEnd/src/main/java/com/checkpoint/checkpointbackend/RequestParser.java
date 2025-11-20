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
                    String id = sqlRequest.createUser(userName);
                    Map<String, Object> response = new HashMap<>();
                    response.put("userID", id);
                    response.put("userName", userName);
                    return ResponseEntity.ok(response);
                }
                    
                case "create-project" -> { //create-project: projectName
                    String id = sqlRequest.createProject(arg1, userID);
                    Map<String, Object> response = new HashMap<>();
                    response.put("projectID", id);
                    response.put("projectName", arg1);
                    return ResponseEntity.ok(response);
                }
                case "delete-project" -> { //delete-project
                    sqlRequest.deleteProject(projectID, userID);
                }
                case "remove-user-from-project" -> { //remove-user-from-project : anotherUserID
                    if(!sqlRequest.checkPermissionLevel(projectID, userID).equals("o")) {
                        continue;
                    }
                    sqlRequest.removeUserFromProject(projectID, arg1);
                }
                case "add-user-to-project" -> { //add-user-to-project: userID : permissionLevel
                    if(!sqlRequest.checkPermissionLevel(projectID, userID).equals("o")) {
                        continue;
                    }
                    sqlRequest.addUserToProject(projectID, arg1, arg2);
                }
                case "change-permission-level" -> { //change-permission-level: userID : permissionLevel
                    if(!sqlRequest.checkPermissionLevel(projectID, userID).equals("o")) {
                        continue;
                    }
                    sqlRequest.changePermissionLevel(projectID, arg1, arg2);

                }
                case "get-project-boards" -> { // get-project-boards
                    return ResponseEntity.ok(sqlRequest.requestProjectBoard(projectID));
                }
                case "get-user-json" -> { // get-user-json
                    return ResponseEntity.ok(sqlRequest.requestUserJson(userID));
                }
                case "add-board-to-project" -> { // add-board-to-project: boardName : content
                    sqlRequest.addBoardToProject(userID, projectID, arg1, arg2);
                }
                case "update-board-section" -> { // update-board-section: boardName : content
                    sqlRequest.updateBoardSection(userID, projectID, arg1, arg2);
                }
                default -> {
                    
                }
            }

            System.out.println(line);
            }
        } catch (IOException e) {


        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", "command completed sucessfully");
        return ResponseEntity.ok(response);
    }
}
